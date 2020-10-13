package com.example.smartfarming;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;

import java.util.ArrayList;
import java.util.List;

public class EchartOptionUtil {

    public static GsonOption getLineChartOptions(Object[] arrayListX, Object[]  arrayListY) {
        GsonOption option = new GsonOption();

        option.title("浊度监测");
        option.legend("浊度");
        option.tooltip().trigger(Trigger.axis);
        ValueAxis valueAxis = new ValueAxis();
        option.yAxis(valueAxis);

        CategoryAxis categorxAxis = new CategoryAxis();
        categorxAxis.axisLine().onZero(false);
        categorxAxis.boundaryGap(true);
        categorxAxis.data(arrayListX);
        categorxAxis.axisLabel().rotate(45);
        categorxAxis.name("时间");
        option.xAxis(categorxAxis);

        Line line = new Line();
        line.smooth(false).name("浊度").data(arrayListY).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        option.series(line);
        return option;
    }
}