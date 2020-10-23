package com.example.smartfarming.utils;

import android.content.Intent;
import android.widget.LinearLayout;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Lines;
import com.github.abel533.echarts.series.MarkPoint;
import com.github.abel533.echarts.series.Series;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.github.abel533.echarts.code.SeriesType.line;
import static com.github.abel533.echarts.code.SeriesType.pie;
import static com.github.abel533.echarts.code.Symbol.roundRect;

public class EchartOptionUtil {

    public static GsonOption getLineChartOptions(Object[] arrayListX, Object[] arrayListY) {
        GsonOption option = new GsonOption();


        option.title("浊度监测");
        option.tooltip().trigger(Trigger.axis);
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.min(0);
        valueAxis.max(6);

        CategoryAxis categorxAxis = new CategoryAxis();
        categorxAxis.axisLine().onZero(false);
        categorxAxis.boundaryGap(true);
        categorxAxis.data(arrayListX);

        option.xAxis(categorxAxis);
        option.yAxis(valueAxis);

        Line line1 = new Line();

        MarkPoint markPoint = new MarkPoint();
        markPoint.geoCoord("Q", "2","10");
        line1.markPoint(markPoint);
        line1.smooth(false).data(arrayListY).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        option.series(line1);
        return option;
    }
}