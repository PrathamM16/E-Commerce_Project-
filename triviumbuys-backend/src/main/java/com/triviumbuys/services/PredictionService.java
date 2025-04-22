package com.triviumbuys.services;

import com.triviumbuys.entity.UploadedOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

@Service
public class PredictionService {

    @Autowired
    private FileUploadService fileUploadService;

    public Map<String, Double> predictRevenueNextDays(int numberOfDays) {
        List<UploadedOrder> orders = fileUploadService.getUploadedOrders();

        if (orders == null || orders.isEmpty()) {
            return generateSampleData(numberOfDays);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            TreeMap<String, Double> revenueByDate = new TreeMap<>();

            for (UploadedOrder order : orders) {
                if (order.getDate() != null && order.getTotalAmount() != null) {
                    revenueByDate.put(order.getDate(), 
                        revenueByDate.getOrDefault(order.getDate(), 0.0) + order.getTotalAmount());
                }
            }

            if (revenueByDate.isEmpty()) {
                return generateSampleData(numberOfDays);
            }

            List<Double> xData = new ArrayList<>();
            List<Double> yData = new ArrayList<>();

            int index = 0;
            for (Map.Entry<String, Double> entry : revenueByDate.entrySet()) {
                xData.add((double) index);
                yData.add(entry.getValue());
                index++;
            }

            WeightedObservedPoints points = new WeightedObservedPoints();
            for (int i = 0; i < xData.size(); i++) {
                points.add(xData.get(i), yData.get(i));
            }

            // Use Polynomial Degree 2 (Quadratic Curve)
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
            double[] coefficients = fitter.fit(points.toList());

            Map<String, Double> predictions = new LinkedHashMap<>();

            Calendar cal = Calendar.getInstance();
            if (!revenueByDate.isEmpty()) {
                cal.setTime(sdf.parse(revenueByDate.lastKey()));
            }

            for (int i = 1; i <= numberOfDays; i++) {
                double x = xData.size() + i - 1;
                double predictedRevenue = coefficients[0] + coefficients[1] * x + coefficients[2] * x * x;
                cal.add(Calendar.DATE, 1);
                predictions.put(sdf.format(cal.getTime()), Math.max(predictedRevenue, 0.0));
            }

            return predictions;

        } catch (Exception e) {
            return generateSampleData(numberOfDays);
        }
    }

    private Map<String, Double> generateSampleData(int days) {
        Map<String, Double> sampleData = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        double baseRevenue = 1000.0;
        Random random = new Random();

        for (int i = 1; i <= days; i++) {
            cal.add(Calendar.DATE, 1);
            double variation = (random.nextDouble() * 0.4) - 0.2;
            double revenue = baseRevenue * (1 + variation);
            sampleData.put(sdf.format(cal.getTime()), revenue);
        }

        return sampleData;
    }
}
