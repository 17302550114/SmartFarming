    package com.example.smartfarming;

    import androidx.appcompat.app.AppCompatActivity;

    import android.os.Bundle;
    import android.view.View;
    import android.webkit.WebView;
    import android.webkit.WebViewClient;
    import android.widget.Button;
    import android.widget.CompoundButton;
    import android.widget.EditText;
    import android.widget.Switch;

    import com.example.smartfarming.data.NodeData;
    import com.example.smartfarming.data.Order;
    import com.example.smartfarming.utils.Constant;
    import com.example.smartfarming.utils.EchartOptionUtil;
    import com.example.smartfarming.utils.EchartView;
    import com.example.smartfarming.utils.FileUtil;
    import com.example.smartfarming.utils.JsonRootBean;
    import com.example.smartfarming.views.CircleProgress;
    import com.example.smartfarming.views.DialProgress;
    import com.google.gson.Gson;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    import okhttp3.OkHttpClient;
    import okhttp3.Request;

    import static java.lang.Character.getType;

    public class MainActivity extends AppCompatActivity {

        private static final String APIKEY = "ccOaQkJydurl7Frt0=jjvCbtrpM=";
        private static final String DEVICEID = "598768194";
        private static final String Turbidity01 = "tur01";//数据流名称
        private static final String Water_Height = "water_height";//数据流名称\
        private static final float PI = (float) 3.14;
        private DialProgress dialProgress;

        //基础控件
        private EditText mEditTur01;
        private EditText mEditWaterHeight_01;
        private Button mBtnGetData;
        private float mLoss = 5;
        private Button mBtnGenInfo;//生成信息报表
        //图表控件
        private EchartView lineChart;
        //数据实时获取开关
        private Switch mSwitchOntime;
        //自定义显示控件
        private CircleProgress mCirclePro;

        //实时获取数据线程对象
        private Thread mThreadAutoData;

        //传感器值
        public ArrayList<String> mTimeBuffer = new ArrayList<>();
        public ArrayList<String> mTur01ValueBuffer = new ArrayList<>();
        List<Order> list;
        List<NodeData> list_NodeData;
        //数据类

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            list_NodeData = new ArrayList<>();
            list = new ArrayList<Order>();
            for (int i = 1; i <= 10; i++) {
                list.add(new Order(i,i+1,"女"+i,"南京"+i));
            }
            list_NodeData.add(new NodeData(1,"2020.11.1 -2020.11.2",20,100));
            initView();
            event();
        }

        public void event() {
            //ArrayList初始化；
            for (int k = 0; k < 7; k++) {
                mTur01ValueBuffer.add("0");
                mTimeBuffer.add("0");
            }

            mBtnGenInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FileUtil.writeToExcel(MainActivity.this,list_NodeData,"NodeData");
//                        FileUtil.writeToExcel(MainActivity.this,list,"orderTest");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //echart图表控件
            lineChart.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //最好在h5页面加载完毕后再加载数据，防止html的标签还未加载完成，不能正常显示
                    Object[] x = new Object[]{
                            "0", "1", "2", "3", "4", "5", "6"
                    };
                    Object[] y = new Object[]{
                            0, 0, 0, 0, 0, 0, 0
                    };
                    lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));
                }
            });

            //获取实时数据开关
            mSwitchOntime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mBtnGetData.setEnabled(false);
                        startAutoData();
                    } else {
                        mBtnGetData.setEnabled(true);
                        stopAutoData();

                    }
                }
            });

            //获取数据按钮（单次）
            mBtnGetData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Get(DEVICEID, Turbidity01, APIKEY);
                }
            });
        }

        /**
         * 使用okHttp从oneNet平台获取响应
         * 参数列表
         * 1.String datastream_id ：数据流名称
         * 返回值： 返回网页响应，供Gjson进行解析
         */
        public void Get(String deviceID, String dataStreamId, String apiKey) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        String response_tur = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID + "/datapoints?datastream_id=" + Turbidity01).header("api-key", apiKey).build()).execute().body().string();
                        String response_wh = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID + "/datapoints?datastream_id=" + Water_Height).header("api-key", apiKey).build()).execute().body().string();
                        parseJSONWithGSON(response_tur);
                        parseJSONWithGSON_wh(response_wh);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        /**
         * 解析网页响应，返回浊度数值
         *
         * @param responseData：网页响应
         * @return :返回解析结果——传感器值
         */
        private void parseJSONWithGSON(String responseData) {
            JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
            List<Datastreams> streams = app.getData().getDatastreams();
            List<Datapoints> points = streams.get(0).getDatapoints();
            System.out.println("数据为：" + responseData);
            System.out.println("数据点大小为：" + points.size());
            int count = app.getData().getCount();//获取数据的数量
            for (int i = 0; i < points.size(); i++) {
                String mTur01Time = points.get(i).getAt().substring(11, 19);
                String mTur01Value = points.get(i).getValue();
                System.out.println(mTur01Time);
                System.out.println(mTur01Value);
                mTimeBuffer.add(0, mTur01Time);
                mTur01ValueBuffer.add(0, mTur01Value);
                Object[] x = new Object[7];
                Object[] y = new Object[7];
                for (int j = 0; j < x.length; j++) {
                    x[x.length - 1 - j] = new String(mTimeBuffer.get(j));
                    y[x.length - 1 - j] = new String(mTur01ValueBuffer.get(j));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));
                    }
                });
            }
        }

        /**
         * 解析网页响应，返回传感器数值 液位高度
         *
         * @param responseData：网页响应
         * @return :返回解析结果——传感器值。。液位
         */
        private void parseJSONWithGSON_wh(String responseData) {
            JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
            List<Datastreams> streams = app.getData().getDatastreams();
            List<Datapoints> points = streams.get(0).getDatapoints();
            int count = app.getData().getCount();//获取数据的数量

            String mWhValue = points.get(0).getValue();
            mLoss += (float) (Float.parseFloat(mWhValue) * (0.1866 * Float.parseFloat(mTur01ValueBuffer.get(0))) - 0.683);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialProgress.setValue(Float.parseFloat(mWhValue));
                    mCirclePro.setValue(mLoss);
                }
            });
        }
        /**
         * 绘制图表
         * @param arrayListX: 坐标横轴：时间
         * @param arrayListY：坐标纵轴：传感器值
         */
    //    private void refreshLineChart(ArrayList<String> arrayListX, ArrayList<String> arrayListY){
    //        Object[] x = new Object[7];
    //        Object[] y = new Object[7];
    //        for (int j=0;j<x.length;j++)
    //        {
    //            x[x.length-1-j] = new String(arrayListX.get(j));
    //            y[x.length-1-j] = new String(arrayListY.get(j));
    //        }
    //        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));
    //    }

        /**
         * 开始自动获取数据
         */
        private void startAutoData() {
            mThreadAutoData = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        Get(DEVICEID, Turbidity01, APIKEY);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            mThreadAutoData.start();
        }

        /**
         * 关闭自动获取数据
         */
        private void stopAutoData() {
            if (mThreadAutoData != null && !mThreadAutoData.isInterrupted()) {
                mThreadAutoData.interrupt();
            }
        }

        //控件初始化
        private void initView() {
            //单次获取数据按钮
            mBtnGetData = findViewById(R.id.btn_GetData);
            //图表初始化
            lineChart = findViewById(R.id.lineChart);
            //初始化实时数据开关
            mSwitchOntime = findViewById(R.id.sw_Ontime);
            mCirclePro = findViewById(R.id.circle_progress_bar1);
            dialProgress = findViewById(R.id.dial_progress_bar);

            mBtnGenInfo = (Button) findViewById(R.id.btnGenInfo);
        }
    }