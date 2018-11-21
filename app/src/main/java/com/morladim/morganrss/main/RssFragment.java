package com.morladim.morganrss.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ItemManager;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.network.ErrorConsumer;
import com.morladim.morganrss.base.network.NewsProvider;
import com.morladim.morganrss.base.ui.BaseFragment;
import com.morladim.morganrss.base.ui.ContentView;
import com.morladim.morganrss.base.util.DeviceUtils;
import com.morladim.morganrss.base.util.ImageLoader;

import java.lang.ref.SoftReference;
import java.util.List;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;

/**
 * @author morladim
 */
@ContentView(R.layout.fragment_rss)
public class RssFragment extends BaseFragment {

    public static RssFragment newInstance(String title, String url, long id) {
        RssFragment fragment = new RssFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(URL, url);
        args.putLong(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            url = getArguments().getString(URL);
            id = getArguments().getLong(ID);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !loadDone && initView) {
            init();
            loadDone = true;
        }
        if (isVisibleToUser) {
            hasShown = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        if (!initView) {
            initView = true;
        }
        if (hasShown && !loadDone) {
            init();
            loadDone = true;
        }
    }

    private void init() {
        refreshLayout.setEnableAutoLoadMore(true);
        refreshLayout.setDisableLoadMore(false);
        adapter = new Rss2Adapter(DeviceUtils.getScreenWidth(getAttachActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getAttachActivity()));
        refreshLayout.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                if (isRefresh) {
                    if (adapter.getItemCount() == 0) {
                        //無數據時下拉刷新，先加載本地數據，若本地也無數據則從網絡獲取
                        List<Item> items = ItemManager.getInstance().getList(id, 0, adapter.getLimit());
                        if (items != null && items.size() != 0) {
                            loadMore(items);
                        } else {
                            loadFresh();
                        }
                    } else {
                        //有數據時下拉刷新直接從網絡獲取
                        loadFresh();
                    }
                } else {
                    //加載更多時
                    List<Item> items = null;
                    if (adapter.getItemCount() == 0) {
                        items = ItemManager.getInstance().getList(id, 0, adapter.getLimit());
                    } else {
                        Item item = adapter.getDataAt(adapter.getItemCount() - 1);
                        if (item != null && item.getId() != null) {
                            items = ItemManager.getInstance().getListFrom(item.getChannelId(), item.getPubDate(), adapter.getLimit());
                        }
                    }
                    if (items != null && items.size() != 0) {
                        adapter.loadMore(items);
                        refreshLayout.setDisableLoadMore(false);
                    } else {
                        refreshLayout.setDisableLoadMore(true);
                    }
                    refreshLayout.refreshComplete();
                }
            }
        });
        refreshLayout.autoRefresh();
        // TODO: 2017/8/11 tag picasso
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Object tag = new Object();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ImageLoader.getInstance().resumeTag(tag);
                } else {
                    ImageLoader.getInstance().pauseTag(tag);
                }
            }
        });
        // TODO: 2017/7/19 需要加入访问网络前网络状态的判断
    }

    private void loadMore(List<Item> items) {
        if (items.size() == 0) {
            refreshLayout.setDisableLoadMore(true);
            refreshLayout.refreshComplete();
            return;
        }
        adapter.loadMore(items);
        refreshLayout.refreshComplete();
    }

    private void loadFresh() {
        NewsProvider.getXml(url, new Consumer<List<Item>>() {
            @Override
            public void accept(@NonNull List<Item> items) {
                adapter.refresh(items);
                if (items != null && items.size() == adapter.getLimit()) {
                    adapter.setOffset(adapter.getLimit());
                }
                refreshLayout.setDisableLoadMore(false);
                refreshLayout.refreshComplete();
            }
        }, getErrorConsumer(), 0, adapter.getLimit());
    }

    private static class RssErrorConsumer extends ErrorConsumer {

        private SoftReference<SmoothRefreshLayout> refreshLayoutSoftReference;

        public RssErrorConsumer(View snackView, SmoothRefreshLayout refreshLayout) {
            super(snackView);
            refreshLayoutSoftReference = new SoftReference<>(refreshLayout);
        }

        @Override
        public void accept(Throwable throwable) {
            super.accept(throwable);
            if (refreshLayoutSoftReference.get() != null) {
                refreshLayoutSoftReference.get().refreshComplete();
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String ID = "id";

    private String title;
    private String url;
    private long id;

    @BindView(R.id.single_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.smooth)
    SmoothRefreshLayout refreshLayout;

    private boolean loadDone = false;
    private boolean initView = false;
    private boolean hasShown = false;

    private Rss2Adapter adapter;

    private View rootView;

    private RssErrorConsumer errorConsumer;

    private RssErrorConsumer getErrorConsumer() {
        if (errorConsumer == null) {
            return errorConsumer = new RssErrorConsumer(rootView, refreshLayout);
        }
        return errorConsumer;
    }

    /**
     * 標識是否在中刷新{@link RssPagerAdapter#getItemPosition(Object)}
     */
    private boolean refresh = false;
}
