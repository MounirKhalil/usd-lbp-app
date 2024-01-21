package com.husseinamro.exchange.login;

import com.husseinamro.exchange.Authentication;
import com.husseinamro.exchange.OnPageCompleteListener;
import com.husseinamro.exchange.PageCompleter;
import com.husseinamro.exchange.api.ExchangeService;
import com.husseinamro.exchange.api.model.Token;
import com.husseinamro.exchange.api.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login implements PageCompleter {
    public TextField usernameTextField;
    public TextField passwordTextField;
    private OnPageCompleteListener onPageCompleteListener;
    public void login(ActionEvent actionEvent) {
        User user = new User(usernameTextField.getText(), passwordTextField.getText());
        ExchangeService.exchangeApi().authenticate(user).enqueue(new Callback<Token>() {
         @Override
         public void onResponse(Call<Token> call, Response<Token> response) {
             Authentication.getInstance().saveToken(response.body().getToken());
             Platform.runLater(() -> {
                 onPageCompleteListener.onPageCompleted();
             });
         }
         @Override
         public void onFailure(Call<Token> call, Throwable throwable) {}
        });
    }
    public void setOnPageCompleteListener(OnPageCompleteListener onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
