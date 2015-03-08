package com.jbake.ui.controller;

import com.jbake.ui.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

/**
 * Created by julianliebl on 08.03.2015.
 */
public class SettingsController implements Initializable {
    @FXML private ComboBox<String> mCountryChoiceBox;
    @FXML private ComboBox<String> mLanguageChoiceBox;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.addAll(availableLocales.stream().map(Locale::getCountry).filter(s -> !s.isEmpty()).distinct().sorted().collect(toList()));
        mCountryChoiceBox.setItems(countries);

        ObservableList<String> languages = FXCollections.observableArrayList();
        languages.addAll(availableLocales.stream().map(Locale::getLanguage).filter(s -> !s.isEmpty()).distinct().sorted().collect(toList()));
        mLanguageChoiceBox.setItems(languages);

        mCountryChoiceBox.getSelectionModel().select(Settings.getInstance().getLocaleCountry());
        mLanguageChoiceBox.getSelectionModel().select(Settings.getInstance().getLocaleLanguage());
        
        mCountryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Settings.getInstance().setLocaleCountry(newValue);
        });

        mLanguageChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Settings.getInstance().setLocaleLanguage(newValue);
        });
    }

    @FXML
    protected void handleCloseButtonAction(ActionEvent event) {
        ((Stage)((Node) event.getSource()).getScene().getWindow()).close();
    }
}
