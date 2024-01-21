module com.husseinamro.exchange {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires java.sql;
    requires gson;
    requires retrofit2.converter.gson;

    opens com.husseinamro.exchange to javafx.fxml;
    opens com.husseinamro.exchange.api.model to javafx.base, gson;
    exports com.husseinamro.exchange;
    exports com.husseinamro.exchange.rates;
    opens com.husseinamro.exchange.rates to javafx.fxml;
    requires java.prefs;
    opens com.husseinamro.exchange.login to javafx.fxml;
    opens com.husseinamro.exchange.register to javafx.fxml;
    opens com.husseinamro.exchange.transactions to javafx.fxml;
    opens com.husseinamro.exchange.statistics to javafx.fxml;
    exports com.husseinamro.exchange.platform;
    opens com.husseinamro.exchange.platform to javafx.fxml;
}