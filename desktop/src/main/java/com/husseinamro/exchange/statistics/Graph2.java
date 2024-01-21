package com.husseinamro.exchange.statistics;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class Graph2 {
    public LineChart<Number, Number> lineChart2;
    public LineChart<Number, Number> lineChart1;
    public void initialize() {
        // Create a series of data points
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 23));
        series.getData().add(new XYChart.Data<>(2, 14));
        series.getData().add(new XYChart.Data<>(3, 15));
        series.getData().add(new XYChart.Data<>(4, 24));
        series.getData().add(new XYChart.Data<>(5, 34));

        // Add the series to the line chart
        lineChart1.getData().add(series);

        // Create a new series for the second graph
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data<>(1, 23));
        series2.getData().add(new XYChart.Data<>(2, 14));
        series2.getData().add(new XYChart.Data<>(3, 15));
        series2.getData().add(new XYChart.Data<>(4, 24));
        series2.getData().add(new XYChart.Data<>(5, 34));

        // Add the series to the second line chart
        lineChart2.getData().add(series2);
    }

}