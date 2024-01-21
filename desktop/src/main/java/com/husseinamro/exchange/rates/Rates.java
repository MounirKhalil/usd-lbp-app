package com.husseinamro.exchange.rates;

import com.husseinamro.exchange.Authentication;
import com.husseinamro.exchange.api.ExchangeService;
import com.husseinamro.exchange.api.model.ExchangeRates;
import com.husseinamro.exchange.api.model.Transaction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.DecimalFormat;

public class Rates {
    public Label buyUsdRateLabel;
    public Label sellUsdRateLabel;
    public TextField lbpTextField;
    public TextField usdTextField;
    public ToggleGroup transactionType;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Calculator
    public TextField AmountTextField;
    public ToggleGroup CalcTransactionType;
    public Label calcResult;
    public void initialize() {
        fetchRates();
    }

    private void fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                ExchangeRates exchangeRates = response.body();
                Platform.runLater(() -> {
                    buyUsdRateLabel.setText(df.format(exchangeRates.lbpToUsd) + "  LBP");
                    sellUsdRateLabel.setText(df.format(exchangeRates.usdToLbp) + "  LBP");
                });
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable throwable) {
            }
        });
    }

    public void addTransaction(ActionEvent actionEvent) {
        if ((transactionType.getSelectedToggle() == null) || (usdTextField.getText().isEmpty()) || (lbpTextField.getText().isEmpty()) || (Double.parseDouble(usdTextField.getText())<=0) || (Double.parseDouble(lbpTextField.getText())<=0)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Fill All Required Fields with Appropriate Input!");
            alert.showAndWait();
            usdTextField.setText("");
            lbpTextField.setText("");
            return;
        }
        Transaction transaction = new Transaction(
                Float.parseFloat(usdTextField.getText()),
                Float.parseFloat(lbpTextField.getText()),
                ((RadioButton)transactionType.getSelectedToggle()).getText().equals("Sell USD")
        );

        String userToken = Authentication.getInstance().getToken();
        String authHeader = userToken != null ? "Bearer " + userToken : null;
        ExchangeService.exchangeApi().addTransaction(transaction, authHeader).enqueue(new Callback<Object>() {
        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
            fetchRates();
            Platform.runLater(() -> {
            usdTextField.setText("");
            lbpTextField.setText("");
            });
        }
        @Override
        public void onFailure(Call<Object> call, Throwable throwable)
        {
        }
        });
    }

    public void calculate(ActionEvent actionEvent) {
        if ((CalcTransactionType.getSelectedToggle() == null) || (AmountTextField.getText().isEmpty()) || (Double.parseDouble(AmountTextField.getText())<=0)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Fill All Required Fields with Appropriate Input!");
            alert.showAndWait();
            AmountTextField.setText("");
            calcResult.setText("Result");
            return;
        }
        String amount = AmountTextField.getText();
        double rate;
        double convertedAmount;
        if (CalcTransactionType.getSelectedToggle() != null) {
            RadioButton selectedRadioButton = (RadioButton) CalcTransactionType.getSelectedToggle();
            String selectedValue = selectedRadioButton.getText();
            if (selectedValue.equals("LBP to USD")) {
                String buyUsdRateLabelString = buyUsdRateLabel.getText();
                rate = Double.parseDouble(buyUsdRateLabelString.substring(0,buyUsdRateLabelString.length()-6));
                convertedAmount = Double.parseDouble(amount) / rate;
                calcResult.setText(df.format(convertedAmount)+" USD");
            } else if (selectedValue.equals("USD to LBP")) {
                String sellUsdRateLabelString = sellUsdRateLabel.getText();
                rate = Double.parseDouble(sellUsdRateLabelString.substring(0,sellUsdRateLabelString.length()-4));
                convertedAmount = Double.parseDouble(amount) * rate;
                calcResult.setText(df.format(convertedAmount)+" LBP");
            }
        }
    }

}
