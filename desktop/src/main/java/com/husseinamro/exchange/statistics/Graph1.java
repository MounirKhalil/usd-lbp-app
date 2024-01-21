package com.husseinamro.exchange.statistics;

import com.husseinamro.exchange.Authentication;
import com.husseinamro.exchange.api.Exchange;
import com.husseinamro.exchange.api.ExchangeService;
import com.husseinamro.exchange.api.model.BuyData;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Graph1 implements Initializable {

    public Exchange exchange = ExchangeService.exchangeApi();
    public LineChart<Number, Number> lineChart1;
    public LineChart<Number, Number> lineChart2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            List<BuyData> buyDataList = fetchBuyData();
            List<BuyData> processedBuyDataList = preprocessBuyData(buyDataList);
            plotBuyData(processedBuyDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<BuyData> fetchBuyData() throws IOException {
        String authorization = "Bearer " + Authentication.getInstance().getToken();
        Call<List<BuyData>> call = exchange.getBuyData(authorization);
        Response<List<BuyData>> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new IOException("Error fetching data: " + response.message());
        }
    }
    public List<BuyData> preprocessBuyData(List<BuyData> buyDataList) {
        List<BuyData> processedBuyDataList = new ArrayList<>();
        for (BuyData buyData : buyDataList) {
            Float buyUsd = buyData.buyUsd;
            String number = buyData.number;
            LocalDateTime date = LocalDateTime.parse(buyData.date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            processedBuyDataList.add(new BuyData(buyUsd, number, date));
        }
        Collections.sort(processedBuyDataList, (bd1, bd2) -> bd1.date.compareTo(bd2.date));
        return processedBuyDataList;
    }
    public void plotBuyData(List<BuyData> processedBuyDataList) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("USD to LBP");

        for (BuyData buyData : processedBuyDataList) {
            Number xValue = buyData.date.toEpochSecond(null);
            Number yValue = buyData.buyUsd;
            series.getData().add(new XYChart.Data<>(xValue, yValue));
        }

        lineChart1.getData().clear();
        lineChart1.getData().add(series);
        lineChart1.getXAxis().setLabel("Date");
        lineChart1.getYAxis().setLabel("USD");

        // Format x-axis labels
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lineChart1.getXAxis().setLabel(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                LocalDateTime date = LocalDateTime.ofEpochSecond(object.longValue(), 0, null);
                return date.format(formatter);
            }

            @Override
            public Number fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }.toString());
    }
}