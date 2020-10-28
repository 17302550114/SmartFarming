package com.example.smartfarming.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import com.example.smartfarming.data.NodeData;
import com.example.smartfarming.data.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class FileUtil {
    public static void writeToExcel(Context context, List<NodeData> orders, String fileName) throws Exception {
        //sd卡是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage(context) > 1000000) {
            Toast.makeText(context, "SD卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] title = {"降雨次数", "时间段", "平均浊度", "流失量"};//表头
        File file;
        File dir = new File(context.getExternalFilesDir(null).getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建文件
        file = new File(dir, fileName + ".xls");
        //创建Excel表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        //创建第一个表并设置第一格名字
        WritableSheet sheet = wwb.createSheet("降雨次数", 0);//表名
        //第一行头部
        Label label;//(头部4列)
        for (int i = 0; i < title.length; i++) {
            //每一格是以二维数组的形式存在（列,行）（0,0； 1,0 ；2,0 ；3,0）
            label = new Label(i, 0, title[i], getHeader());//一个单元格;
            sheet.addCell(label);//将单元格加入表中
        }
        //具体数据(口口口口)
        for (int i = 0; i < orders.size(); i++) {
            NodeData nodeData = orders.get(i);
            Label id = new Label(0, i + 1, String.valueOf(nodeData.getNumber()));
            Label phone = new Label(1, i + 1, nodeData.getmRainPeriod());
            Label name = new Label(2, i + 1, String.valueOf( nodeData.getmPerTur()));
            Label addr = new Label(3, i + 1, String.valueOf( nodeData.getmLoss()));
            sheet.addCell(id);
            sheet.addCell(phone);
            sheet.addCell(name);
            sheet.addCell(addr);
        }
        wwb.write();//写入数据
        wwb.close();
        Toast.makeText(context, "写入完成", Toast.LENGTH_SHORT).show();
    }

    /**
     * 表格样式
     *
     * @return
     */
    public static WritableCellFormat getHeader() {
        //参数1：字体大小， 2：18，3：粗体，4：斜体
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD, true);// 定义字体
        try {
            font.setColour(Colour.getInternalColour(Color.BLUE));// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        //单元格样式
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);// 黑色边框
            format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    /**
     * 获取SD可用容量
     */
    private static long getAvailableStorage(Context context) {
        String root = context.getExternalFilesDir(null).getPath();
        //获取磁盘使用情况
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }
}
