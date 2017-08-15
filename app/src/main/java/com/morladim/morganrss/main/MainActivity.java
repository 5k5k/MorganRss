package com.morladim.morganrss.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.morladim.morganrss.IImageManager;
import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.image.ImageService;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    MHandler handler = new MHandler();
    private RecyclerView recyclerView;

    //    private List<Item> data;
    private Rss2Adapter adapter;

    //    public static final String rssUrl = "http://www.ftchinese.com/rss/feed";
//    public static final String rssUrl = "http://techcrunch.cn/feed/";
//    public static final String rssUrl = "http://www.pocketgamer.co.uk/rss.asp";
//    public static final String rssUrl = "https://www.gamespot.com/feeds/news/";//fan
//    public static final String rssUrl = "http://davidkphotography.com/index.php?x=rss";
//    public static final String rssUrl = "http://www.techlearning.com/RSS";
//    public static final String rssUrl = "http://feeds.washingtonpost.com/rss/rss_blogpost";
//    public static final String rssUrl = "http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml";//fan
//    public static final String rssUrl = "http://www.toodaylab.com/feed";
//    public static final String rssUrl = "http://www.qingniantuzhai.com/feed";
//    public static final String rssUrl = "http://jandan.net/feed";
//    public static final String rssUrl = "http://www.dxy.cn/bbs/rss/2.0/all.xml";
//    public static final String rssUrl = "http://www.infoq.com/cn/feed";//待测试
//    public static final String rssUrl = "http://www.oschina.net/news/rss";
//    public static final String rssUrl = "http://www.geekpark.net/rss";
//    public static final String rssUrl = "https://www.huxiu.com/rss/4.xml";
//    public static final String rssUrl = "https://sspai.com/feed";
//    public static final String rssUrl = "http://www.pingwest.com/feed/";
//    public static final String rssUrl = "http://www.sootoo.com/feeds/tag/5.xml";
//    public static final String rssUrl = "https://www.huxiu.com/rss/0.xml";
//    public static final String rssUrl = "http://www.tmtpost.com/rss.xml";
//    public static final String rssUrl = "http://www.qdaily.com/feed.xml";
//    public static final String rssUrl = "http://blog.sina.com.cn/rss/yilinzazhi.xml";
//    public static final String rssUrl = "http://36kr.com/feed/";
//    public static final String rssUrl = "http://www.techweb.com.cn/rss/focus.xml";
//    public static final String rssUrl = "http://cn.engadget.com/rss.xml";
//    public static final String rssUrl = "http://www.juzicy.com/blog/feed";
//    public static final String rssUrl = "http://www.adaymag.com/feed/";
//    public static final String rssUrl = "http://feed.smzdm.com/";
//    public static final String rssUrl = "http://www.ifanr.com/feed";
//    public static final String rssUrl = "http://www.williamlong.info/blog/rss.xml";
//    public static final String rssUrl = "http://feed.read.org.cn";
//    public static final String rssUrl = "https://www.zhihu.com/rss";
    public static final String rssUrl = "http://www.appinn.com/feed/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Channel> list = ChannelManager.getInstance().getAll();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final SmoothRefreshLayout refreshLayout = (SmoothRefreshLayout) findViewById(R.id.smooth);
        refreshLayout.setMode(SmoothRefreshLayout.MODE_BOTH);
        refreshLayout.setHeaderView(new ClassicHeader(this));
        refreshLayout.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                // TODO: 2017/8/8 刷新声音
                if (isRefresh) {
                    NewsProvider.getXml(rssUrl, new Consumer<List<Item>>() {
                        @Override
                        public void accept(@NonNull List<Item> items) throws Exception {
                            adapter.refresh(items);
                            if (items != null && items.size() == adapter.getLimit()) {
//                                adapter.setHasMore(true);
                                adapter.setOffset(adapter.getLimit());
                                refreshLayout.setDisableLoadMore(false);
                            } else {
//                                adapter.setHasMore(false);
                                refreshLayout.setDisableLoadMore(true);
                            }
                            refreshLayout.refreshComplete();

                        }
                    }, new ErrorConsumer(findViewById(R.id.content_main)), 0, adapter.getLimit());
                } else {
                    NewsProvider.getXml(rssUrl, new Consumer<List<Item>>() {
                        @Override
                        public void accept(@NonNull List<Item> items) throws Exception {
                            adapter.loadMore(items);
                            if (items != null && items.size() == adapter.getLimit()) {
//                                adapter.setHasMore(true);
                                adapter.setOffset(adapter.getOffset() + adapter.getLimit());
                            } else {
//                                adapter.setHasMore(false);
                                refreshLayout.setDisableLoadMore(true);
                            }
                            refreshLayout.refreshComplete();
                        }
                    }, new ErrorConsumer(findViewById(R.id.content_main)), adapter.getOffset(), adapter.getLimit());
                }
            }
        });
//        refreshLayout.setOnLoadMoreScrollCallback(new SmoothRefreshLayout.OnLoadMoreScrollCallback() {
//            @Override
//            public boolean onScroll(View content, float deltaY) {
//                return false;
//            }
//        });


//        cachedThreadPool.inv
        refreshLayout.setEnableScrollToBottomAutoLoadMore(true);
        refreshLayout.autoRefresh(false);
//        refreshLayout.setONLoad
        recyclerView = (RecyclerView) findViewById(R.id.single_recycler);
//        data = new ArrayList<>();
        adapter = new Rss2Adapter(DeviceUtils.getScreenWidth(this));
//        adapter = new Rss2Adapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Intent intent = new Intent(this, ImageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        test();
    }

    private void test() {
        MultipleRequestManager.getInstance().generateChannels(new MultipleRequestManager.GenerateChannelsListener() {
            @Override
            public void allDone(int success, int error) {
                System.out.println("success " + success + " error " + error);
            }

            @Override
            public void oneChannelDone(Channel channel) {
                System.out.println("mor title " + channel.getTitle());
            }

            @Override
            public void oneChannelError() {

            }
        });
//        final String[] urls = getResources().getStringArray(R.array.defaultUrls);
//        System.out.println("mor done " + urls.length);

//        List<Observable<Rss2Xml>> oList = new ArrayList<>(urls.length);
//        List<Boolean> doneList = new ArrayList<>(urls.length);
//        for (String u : urls) {
//            oList.add(RssHttpClient.getNewsApi().getXml(u));
//            doneList.add(false);
//        }

//        Observable.zip(oList.iterator(), new Function<Observable<Rss2Xml>[], String>() {
//            @Override
//            public String apply(@NonNull Observable<Rss2Xml>[] observables) throws Exception {
//                return null;
//            }
//        }).subscribe();
//        Observable.zipIterable(oList, new Function<Observable[], Observable>() {
//            @Override
//            public Observable apply(@NonNull Observable[] rss2XmlObservable) throws Exception {
//                return null;
//            }
//        },true,1024);
//        Observable.zip(Observable.fromArray(oList), Observable.fromArray(doneList), new BiFunction<List<Observable<Rss2Xml>>, List<Boolean>, List<String>>() {
//
//            @Override
//            public List<String> apply(@NonNull List<Observable<Rss2Xml>> rss2XmlList, @NonNull List<Boolean> b) throws Exception {
//                for()
//                return null;
//            }
//        })


//        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//
//        for(int i = 0;i<8;i++ ){
//        count = urls.length;
//        for(String url:urls){
////            String url = urls[i];
//
//            NewsProvider.getXml(url, new Consumer<List<Item>>() {
//                @Override
//                public void accept(@NonNull List<Item> items) throws Exception {
//                    synchronized (obj){
//                        count --;
//                        System.out.println("mor done "+count);
//                    }
//                }
//            }, new Consumer<Throwable>() {
//                @Override
//                public void accept(@NonNull Throwable throwable) throws Exception {
////                    System.out.println("mor done error");
//
//                    synchronized (obj){
//                        count --;
//                        System.out.println("mor done error "+count);
//                    }
//                }
//            }, 0, 10);
//                }


//        }
//        cachedThreadPool.


//        Observable.just(urls)
//                .flatMap(new Function<String[], ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(@NonNull String[] strings) throws Exception {
//                        return Observable.fromArray(strings);
//                    }
//                })
////                .subscribeOn(Schedulers.newThread())
//                .subscribeOn(Schedulers.io())
//                .window(1)
//                .map(new Function<Observable<String>, String>() {
//                    @Override
//                    public String apply(@NonNull Observable<String> stringObservable) throws Exception {
////                        return stringObservable..blockingFirst();
//                        return null;
//                    }
//                }).flatMap(new Function<String, Observable<Rss2Xml>>() {
//            @Override
//            public Observable<Rss2Xml> apply(@NonNull String s) throws Exception {
//                return RssHttpClient.getNewsApi().getXml(s);
//            }
//        }).subscribeOn(Schedulers.io())
////                .flatMap(new Function<Observable<String>, ObservableSource<?>>() {
////                    @Override
////                    public ObservableSource<?> apply(@NonNull Observable<String> stringObservable) throws Exception {
////
////                      return   stringObservable.subscribe(new Consumer<String>() {
////                            @Override
////                            public void accept(@NonNull String s) throws Exception {
////                            }
////                        });
////
//////                        return RssHttpClient.getNewsApi().getXml(s);
////                    }
////                })
////                .concatMap(new Function<String, ObservableSource<Rss2Xml>>() {
////                    @Override
////                    public ObservableSource<Rss2Xml> apply(@NonNull String s) throws Exception {
////                        return  RssHttpClient.getNewsApi().getXml(s);
////                    }
////                })
//
////                .map(new Function<String, Rss2Xml>() {
////            @Override
////            public Rss2Xml apply(@NonNull String s) throws Exception {
////                return RssHttpClient.getNewsApi().getXml(s).subscribe();
////            }
////        })
//
//                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe()
//                .map(new Function<Rss2Xml, List<Item>>() {
//                    @Override
//                    public List<Item> apply(@NonNull Rss2Xml rss2Xml) throws Exception {
//                        String version = rss2Xml.version;
//                        if (version != null) {
//                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
//                            long channelId = ChannelManager.getInstance().insertOrUpdate(rss2Xml.channel, versionId);
//                            return ItemManager.getInstance().getList(channelId, 0, 10);
//                        }
//                        return null;
//                    }
//                })
//                .retry(3).subscribe(new Consumer<List<Item>>() {
//            @Override
//            public void accept(@NonNull List<Item> items) throws Exception {
//                System.out.println("mor done ");
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(@NonNull Throwable throwable) throws Exception {
//                System.out.println("mor done error");
//
//            }
//        }, new Action() {
//            @Override
//            public void run() throws Exception {
//                System.out.println("mor done all");
//
//            }
//        });
////                .subscribe(new Consumer<String>() {
////                    @Override
////                    public void accept(@NonNull String s) throws Exception {
////                        NewsProvider.getXml(s, new Consumer<List<Item>>() {
////                            @Override
////                            public void accept(@NonNull List<Item> items) throws Exception {
////                                System.out.println("mor done ");
////                            }
////                        }, new ErrorConsumer(findViewById(R.id.content_main)), 0, 10);
////                    }
////                }, new Consumer<Throwable>() {
////                    @Override
////                    public void accept(@NonNull Throwable throwable) throws Exception {
////                        System.out.println("mor done error");
////
////                    }
////                }, new Action() {
////                    @Override
////                    public void run() throws Exception {
////                        System.out.println("mor done all");
////                    }
////                })
////        ;
    }

    public static final Object obj = new Object();

    volatile Integer count;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IImageManager manager = IImageManager.Stub.asInterface(iBinder);
//            adapter.setManager(manager);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    //    AndroidSchedulers


//    private OkHttpClient getClient() {
//        OkHttpClient client =
//                new OkHttpClient.Builder()
//                        .connectTimeout(CONNECT_TIME_OUT, SECONDS)
//                        .readTimeout(READ_TIME_OUT, SECONDS)
//                        .writeTimeout(WRITE_TIME_OUT, SECONDS)
////                        .cache(getCache())
//                        .retryOnConnectionFailure(true)
////                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
////                        .addInterceptor(new HeadInterceptor())
////                        .addInterceptor(new CookieInterceptor())
////                        .addInterceptor(new CacheInterceptor())
////                        .addInterceptor(new LoginInterceptor())
//                        .build();
//        return client;
//    }

//    public <T> T createByXML(String baseUrl, Class<T> service) {


//        AnnotationStrategy annotationStrategy = new AnnotationStrategy();
////        Format format = new Format(0, null, new HyphenStyle(), Verbosity.HIGH);
//        Persister persister = new Persister(annotationStrategy);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(getClient())
//                .addConverterFactory(SimpleXmlConverterFactory.create(persister))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//        return retrofit.create(service);
//    }

//    RssFeed rssFeed;

//    private class MHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            System.out.println("---------------------");
//            System.out.println(rssFeed.getAllItems());
//            System.out.println("---------------------");
//        }
//    }

//    private RssFeed getFeed(String urlString) {
////        try {
//        try {
//            URL url = new URL(urlString);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        SAXParserFactory factory = SAXParserFactory.newInstance();  // 构建Sax解析工厂
//        SAXParser parser = null; // 使用Sax解析工厂构建Sax解析器
//        try {
//            parser = factory.newSAXParser();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
////        try {
////            parser.setProperty(OutputKeys.ENCODING,"UTF-8");
////        } catch (SAXNotRecognizedException e) {
////            e.printStackTrace();
////        } catch (SAXNotSupportedException e) {
////            e.printStackTrace();
////        }
//
////            SAXParser parser = factory.newSAXParser(); // 使用Sax解析工厂构建Sax解析器
////            parser.setProperty(OutputKeys.ENCODING,"UTF-8");
////            Charset charset = Charset.forName("UTF-8");
////            XMLReader xmlreader = parser.getXMLReader();   // 使用Sax解析器构建xml Reader
//
//        RssHandler rssHandler = new RssHandler(); // 构建自定义的RSSHandler作为xml Reader的处理器（或代理）
////            rssHandler.
////            xmlreader.setContentHandler(rssHandler);     // 构建自定义的RSSHandler作为xml Reader的处理器（或代理）
//
//
//        InputSource is = new InputSource();      // 使用url打开流,并将流作为xml Reader的输入源并解析
////            is.setEncoding("GB2312");
////            is.setEncoding("gbk");
//        is.setEncoding("UTF-8");
//        is.setCharacterStream(new StringReader(urlString));
//        try {
//            parser.parse(is, rssHandler);
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//            xmlreader.parse(is);

//            InputStream inputStream = new BufferedInputStream(url.openStream());
//
//            InputStreamReader isr = new InputStreamReader(inputStream,"utf-8");


//        return rssHandler.getFeed();     // 将解析结果作为 RSSFeed 对象返回
//        } catch (Exception ee) {
//            ee.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 上次触摸时间
     */
    private long lastClickTime;

    private boolean interceptDoubleClick = true;

    /**
     * 是否拦截双击事件
     *
     * @param interceptDoubleClick 拦截
     */
    protected void setInterceptDoubleClick(boolean interceptDoubleClick) {
        this.interceptDoubleClick = interceptDoubleClick;
    }

    /**
     * 是否处理触摸事件
     *
     * @param ev 事件
     * @return 处理则不下发
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastClick() && interceptDoubleClick) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * activity中连续点击时间间隔，单位毫秒
     */
    public static final int DISPATCH_DURATION = 0;

    /**
     * 是否连续点击
     *
     * @return 连点与否
     */
    private boolean isFastClick() {
        long time = SystemClock.elapsedRealtime();
        long timeD = time - lastClickTime;
        if (timeD > 0 && timeD < DISPATCH_DURATION) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
