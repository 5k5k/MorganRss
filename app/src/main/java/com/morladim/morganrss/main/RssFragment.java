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
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.network.ErrorConsumer;
import com.morladim.morganrss.base.network.NewsProvider;
import com.morladim.morganrss.base.util.DeviceUtils;
import com.morladim.morganrss.base.util.ImageLoader;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import me.dkzwm.smoothrefreshlayout.RefreshingListenerAdapter;
import me.dkzwm.smoothrefreshlayout.SmoothRefreshLayout;
import me.dkzwm.smoothrefreshlayout.extra.header.ClassicHeader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RssFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RssFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RssFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String ID = "id";

    private String title;
    private String url;
    private long id;

//    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    private Rss2Adapter adapter;

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
        final SmoothRefreshLayout refreshLayout = view.findViewById(R.id.smooth);
        refreshLayout.setMode(SmoothRefreshLayout.MODE_BOTH);
        refreshLayout.setHeaderView(new ClassicHeader(getContext()));

        refreshLayout.setEnableScrollToBottomAutoLoadMore(true);
        recyclerView = view.findViewById(R.id.single_recycler);
        adapter = new Rss2Adapter(DeviceUtils.getScreenWidth(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                // TODO: 2017/8/8 刷新声音
                if (isRefresh) {
                    NewsProvider.getXml(url, new Consumer<List<Item>>() {
                        @Override
                        public void accept(@NonNull List<Item> items) throws Exception {
                            adapter.refresh(items);
                            if (items != null && items.size() == adapter.getLimit()) {
                                adapter.setOffset(adapter.getLimit());
                                refreshLayout.setDisableLoadMore(false);
                            } else {
                                refreshLayout.setDisableLoadMore(true);
                            }
                            refreshLayout.refreshComplete();

                        }
                    }, new ErrorConsumer(view.findViewById(R.id.content_main)), 0, adapter.getLimit());
                } else {
                    NewsProvider.getXml(url, new Consumer<List<Item>>() {
                        @Override
                        public void accept(@NonNull List<Item> items) throws Exception {
                            adapter.loadMore(items);
                            if (items != null && items.size() == adapter.getLimit()) {
                                adapter.setOffset(adapter.getOffset() + adapter.getLimit());
                            } else {
                                refreshLayout.setDisableLoadMore(true);
                            }
                            refreshLayout.refreshComplete();
                        }
                    }, new ErrorConsumer(view.findViewById(R.id.content_main)), adapter.getOffset(), adapter.getLimit());
                }
            }
        });
        refreshLayout.autoRefresh(false);
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

    public String getTitle() {
        return title;
    }

    private boolean refresh = true;

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
