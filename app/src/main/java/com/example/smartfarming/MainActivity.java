package com.example.smartfarming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.example.smartfarming.utils.EchartOptionUtil;
import com.example.smartfarming.views.CircleProgress;
import com.example.smartfarming.views.DialProgress;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static java.lang.Character.getType;

public class MainActivity extends AppCompatActivity {

    private static final String APIKEY = "ccOaQkJydurl7Frt0=jjvCbtrpM=";

    private static final String DEVICEID_xintiao = "638280004";
    private static final String DATE_xintiao = "638280004";

    private static final String DEVICEID_jiasd = "638278449";
    private static final String DATE_jiasudu = "638280004";

    private static final String DEVICEID_tem = "598768194";
    private static final String DATE_tem = "638280004";

    private static final String DEVICEID = "598768194";

    private static final String Turbidity01 = "tur01";//数据流名称
    private static final String Water_Height = "water_height";//数据流名称\

    private static final String Temperature = "temperature";
    private static final String Acceler = "jiasudu";
    private static final String Ecg = "xintiao";
    private CircleProgress circleProgress;
    private DialProgress dialProgress;
    private EditText mEditTemperture;

    private static int step = 0;

    private float result = 0;

    private EditText mEditTur01;
    private EditText mEditWaterHeight_01;
    private Button mBtnGetData;
    private EditText mNode01Loss;
    //图表控件
    private EchartView lineChart;
    //数据实时获取开关
    private Switch mSwitchOntime;

    private CircleProgress mCirclePro;

    //实时获取数据线程对象
    private Thread mThreadAutoData;

    //传感器值
    public ArrayList<String> mTimeBuffer = new ArrayList<>();
    public ArrayList<String> mTur01ValueBuffer = new ArrayList<>();


    public ArrayList<String> mWaterHeightValueBuffer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        event();
    }

    public void event() {
        //ArrayList初始化；
        for (int k = 0; k < 7; k++) {
            mTur01ValueBuffer.add("0");
            mTimeBuffer.add("0");
        }
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

    public void Get_step(String deviceID, String dataStreamId, String apiKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    String response_acceler = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID_jiasd + "/datapoints?datastream_id=" + Acceler).header("api-key", apiKey).build()).execute().body().string();
                    parseJSONWithGSON_acceler(response_acceler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    String response_tem = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID_tem + "/datapoints?datastream_id=" + Temperature).header("api-key", apiKey).build()).execute().body().string();
                    String response_acceler = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID_jiasd + "/datapoints?datastream_id=" + Acceler).header("api-key", apiKey).build()).execute().body().string();
                    String response_ecg = client.newCall(new Request.Builder().url("http://api.heclouds.com/devices/" + DEVICEID_xintiao + "/datapoints?datastream_id=" + Ecg).header("api-key", apiKey).build()).execute().body().string();//
                    parseJSONWithGSON_tem(response_tem);
//                    parseJSONWithGSON_acceler(response_acceler);
                    parseJSONWithGSON_ecg(response_ecg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    int j = 200;
    Object[] x = new Object[j];
    Object[] y = new Object[j];

    private void parseJSONWithGSON_ecg(String responseData) {

        JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
        List<Datastreams> streams = app.getData().getDatastreams();
        List<Datapoints> points = streams.get(0).getDatapoints();
        int count = app.getData().getCount();//获取数据的数量
        String mTur01Time = points.get(0).getAt().substring(11, 19);
        String mTur01Value = points.get(0).getValue();

        mTimeBuffer.add(0, mTur01Time);
        mTur01ValueBuffer.add(0, mTur01Value);
        System.out.println("-----------");
        System.out.println(mTur01ValueBuffer.size());
//        System.out.println(mTur01Value);
        for (int i = 0; i < j; i++) {
            x[i] = i;
        }
        if (mTur01ValueBuffer.size() > j) {
            for (int i = 0; i < j; i++) {
//                if (mTur01ValueBuffer.get(i).contains("-")) {
//                    y[j - i - 1] = -30;
//                }
                y[j - i - 1] = -1*((Float.parseFloat(mTur01ValueBuffer.get(i))) / 10000-10);
                System.out.println(y[j - i - 1]);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));

            }
        });
    }

    private void parseJSONWithGSON_acceler(String responseData) {

        JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
        List<Datastreams> streams = app.getData().getDatastreams();
        List<Datapoints> points = streams.get(0).getDatapoints();
        int count = app.getData().getCount();//获取数据的数量
        for (int i = 0; i < points.size(); i++) {
            String mTur01Value = points.get(i).getValue();
            System.out.println(Float.parseFloat(mTur01Value));
            if (Double.parseDouble(mTur01Value) > 120) {
                step++;
            }
            System.out.println(step);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    circleProgress.setValue(step);
                    dialProgress.setValue((float) (step * 0.6123));
                }
            });
        }
    }

    /**
     * 解析网页响应，返回传感器数值
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
//                    mEditTur01.setText(mTur01Value);
//                    lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y));
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
    private void parseJSONWithGSON_tem(String responseData) {
        JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
        List<Datastreams> streams = app.getData().getDatastreams();
        List<Datapoints> points = streams.get(0).getDatapoints();
//        System.out.println("数据点大小为：" + points.size());
        int count = app.getData().getCount();//获取数据的数量
        for (int i = 0; i < points.size(); i++) {
            String mWhValue = points.get(i).getValue();
            String mTemperature = mWhValue.substring(0, 2) + "." + mWhValue.substring(3, 4);
            double mTemperater_f = 1.14 * (Float.parseFloat(mTemperature));
            Object[] x = new Object[7];
            Object[] y = new Object[7];
            DecimalFormat fnum = new DecimalFormat("##0.00");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEditTemperture.setText(("" + mTemperater_f).substring(0, 5));
                }
            });
        }
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Get_step(DEVICEID, Turbidity01, APIKEY);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();

        mThreadAutoData = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    Get(DEVICEID, Turbidity01, APIKEY);
                    try {
                        Thread.sleep(300);
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
//        mEditTur01 = findViewById(R.id.et_Tur01);
//        mEditWaterHeight_01 = findViewById(R.id.et_WaterHeight01);
        mBtnGetData = findViewById(R.id.btn_GetData);
//        mNode01Loss = findViewById(R.id.et_node01Loss);

//        图表初始化
        lineChart = findViewById(R.id.lineChart);
        //初始化实时数据开关
        mSwitchOntime = findViewById(R.id.sw_Ontime);

        mCirclePro = findViewById(R.id.circle_progress_bar1);

        circleProgress = findViewById(R.id.circle_progress_bar1);

        dialProgress = findViewById(R.id.dial_progress_bar);

        mEditTemperture = findViewById(R.id.et_Temperture);

    }

}