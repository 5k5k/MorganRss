package com.morladim.morganrss.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ItemManager;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.network.ErrorConsumer;
import com.morladim.morganrss.base.network.NewsProvider;
import com.morladim.morganrss.base.util.DeviceUtils;
import com.morladim.morganrss.base.util.ImageLoader;

import java.lang.ref.SoftReference;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import timber.log.Timber;

import static me.dkzwm.widget.srl.config.Constants.MODE_SCALE;

/**
 * @author morladim
 */
public class RssFragment extends Fragment {

    public RssFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rss, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        refreshLayout = view.findViewById(R.id.smooth);
        refreshLayout.setEnableAutoLoadMore(true);
        recyclerView = view.findViewById(R.id.single_recycler);
        adapter = new Rss2Adapter(DeviceUtils.getScreenWidth(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                            items = ItemManager.getInstance().getListFrom(item.getChannelId(), item.getId(), adapter.getLimit());
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
                    ImageLoader.resumeTag(tag);
                } else {
                    ImageLoader.pauseTag(tag);
                }
            }
        });
        // TODO: 2017/7/19 需要加入访问网络前网络状态的判断

    }

    private void loadMore(List<Item> items) {
        adapter.loadMore(items);
        if (items.size() == adapter.getLimit()) {
            adapter.setOffset(adapter.getOffset() + adapter.getLimit());
            refreshLayout.setDisableLoadMore(false);
        } else {
            refreshLayout.setDisableLoadMore(true);
        }
        refreshLayout.refreshComplete();
    }

    private void loadFresh() {
        NewsProvider.getXml(url, new Consumer<List<Item>>() {
            @Override
            public void accept(@NonNull List<Item> items) {
                adapter.refresh(items);
                if (items != null && items.size() == adapter.getLimit()) {
                    adapter.setOffset(adapter.getLimit());
                    refreshLayout.setDisableLoadMore(false);
                } else {
                    refreshLayout.setDisableLoadMore(true);
                }
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

    private RecyclerView recyclerView;

    private SmoothRefreshLayout refreshLayout;

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
