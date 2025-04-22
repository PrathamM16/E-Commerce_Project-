package com.triviumbuys.utils;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphGenerator {

	public static File createRevenueLineChart(Map<String, Double> revenueByDate, String filePath) throws Exception {
	    XYChart chart = new XYChartBuilder()
	            .width(600)
	            .height(400)
	            .title("Revenue Over Time")
	            .xAxisTitle("Date")
	            .yAxisTitle("Revenue")
	            .build();

	    List<String> dates = new ArrayList<>(revenueByDate.keySet());
	    List<Double> revenues = new ArrayList<>(revenueByDate.values());

	    double[] xData = new double[dates.size()];
	    double[] yData = new double[revenues.size()];

	    for (int i = 0; i < dates.size(); i++) {
	        xData[i] = i; // keep 0,1,2,3 on x (for uniform spacing)
	        yData[i] = revenues.get(i);
	    }

	    chart.addSeries("Revenue", xData, yData);

	    File file = new File(filePath);
	    BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
	    return file;
	}

    public static File createOrderStatusPieChart(Map<String, Integer> statusCount, String filePath) throws Exception {
        PieChart chart = new PieChartBuilder()
                .width(600)
                .height(400)
                .title("Order Status Breakdown")
                .build();

        for (String status : statusCount.keySet()) {
            chart.addSeries(status, statusCount.get(status));
        }

        File file = new File(filePath);
        BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        return file;
    }

    public static File createCategorySalesPieChart(Map<String, Double> categorySales, String filePath) throws Exception {
        PieChart chart = new PieChartBuilder()
                .width(600)
                .height(400)
                .title("Category Sales Breakdown")
                .build();

        for (String category : categorySales.keySet()) {
            chart.addSeries(category, categorySales.get(category));
        }

        File file = new File(filePath);
        BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        return file;
    }
}
