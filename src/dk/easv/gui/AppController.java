package dk.easv.gui;

import com.jfoenix.controls.*;
import dk.easv.bll.bot.IBot;
import static dk.easv.gui.util.FontAwesomeHelper.getFontAwesomeIconFromPlayerId;
import static dk.easv.dal.DynamicBotClassReader.loadBotList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.layout.AnchorPane;

public class AppController implements Initializable {

    public JFXButton btnTrash;
    public JFXButton btnDiamond;
    @FXML
    private JFXTextField txtHumanNameLeft;
    @FXML
    private JFXRadioButton radioRightAI;
    @FXML
    private JFXTextField txtHumanNameRight;
    @FXML
    private JFXRadioButton radioLeftAI;
    @FXML
    private JFXRadioButton radioRightHuman;
    @FXML
    private ToggleGroup toggleLeft;
    @FXML
    private ToggleGroup toggleRight;

    @FXML
    private JFXButton btnStart;
    @FXML
    private JFXComboBox<IBot> comboBotsRight;
    @FXML
    private JFXComboBox<IBot> comboBotsLeft;
    @FXML
    private JFXRadioButton radioLeftHuman;
    @FXML
    private JFXSlider sliderSpeed;
    
    StatsModel statsModel = new StatsModel();
    @FXML
    private AnchorPane anchorMain;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<IBot> bots = FXCollections.observableArrayList();
        try {
            bots = loadBotList();
        }
        catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }

        comboBotsLeft.setButtonCell(new CustomIBotListCell());
        comboBotsLeft.setCellFactory(p -> new CustomIBotListCell());
        comboBotsLeft.setItems(bots);
        comboBotsRight.setButtonCell(new CustomIBotListCell());
        comboBotsRight.setCellFactory(p -> new CustomIBotListCell());
        comboBotsRight.setItems(bots);
        btnStart.setDisableVisualFocus(true);
        btnDiamond.setGraphic(getFontAwesomeIconFromPlayerId("1"));
        btnTrash.setGraphic(getFontAwesomeIconFromPlayerId("0"));

        radioLeftAI.selectedProperty().addListener((observable, oldValue, newValue) -> comboBotsLeft.setDisable(!newValue));
        radioLeftHuman.selectedProperty().addListener((observable, oldValue, newValue) -> txtHumanNameLeft.setDisable(!newValue));
        radioRightAI.selectedProperty().addListener((observable, oldValue, newValue) -> comboBotsRight.setDisable(!newValue));
        radioRightHuman.selectedProperty().addListener((observable, oldValue, newValue) -> txtHumanNameRight.setDisable(!newValue));
        comboBotsLeft.getSelectionModel().selectFirst();
        comboBotsLeft.setDisable(true);
        comboBotsRight.getSelectionModel().selectFirst();
        comboBotsRight.setDisable(true);

    }

    @FXML
    private void clickOpenStats(ActionEvent event) throws IOException {
        Stage primaryStage = new Stage();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        FXMLLoader fxLoader = new FXMLLoader(
                getClass().getResource("Stats.fxml"));

        Parent root = fxLoader.load();

        StatsController controller = 
                ((StatsController) fxLoader.getController());
        controller.setStatsModel(statsModel);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

    }
    
    private class CustomIBotListCell extends ListCell<IBot> {

        @Override
        protected void updateItem(IBot item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty && item != null)
                setText(item.getBotName());
            else
                setText(null);
        }
    }
    @FXML
    public void clickStart(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = new Stage();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        FXMLLoader fxLoader = new FXMLLoader(getClass().getResource("UTTTGame.fxml"));

        Parent root = fxLoader.load();

        UTTTGameController controller = ((UTTTGameController) fxLoader.getController());

        if (toggleLeft.getSelectedToggle().equals(radioLeftAI)
                && toggleRight.getSelectedToggle().equals(radioRightAI)) {
            controller.setupGame(comboBotsLeft.getSelectionModel().getSelectedItem(), comboBotsRight.getSelectionModel().getSelectedItem());
            primaryStage.setTitle(
                    comboBotsLeft.getSelectionModel().getSelectedItem().getBotName()
                    + " vs "
                    + comboBotsRight.getSelectionModel().getSelectedItem().getBotName());
        }
        else if (toggleLeft.getSelectedToggle().equals(radioLeftHuman)
                && toggleRight.getSelectedToggle().equals(radioRightAI)) {
            controller.setupGame(txtHumanNameLeft.getText(), comboBotsRight.getSelectionModel().getSelectedItem());
            primaryStage.setTitle(
                    txtHumanNameLeft.getText()
                    + " vs "
                    + comboBotsRight.getSelectionModel().getSelectedItem().getBotName());
        }
        else if (toggleLeft.getSelectedToggle().equals(radioLeftAI)
                && toggleRight.getSelectedToggle().equals(radioRightHuman)) {
            controller.setupGame(comboBotsLeft.getSelectionModel().getSelectedItem(), txtHumanNameRight.getText());
            primaryStage.setTitle(
                    comboBotsLeft.getSelectionModel().getSelectedItem().getBotName()
                    + " vs "
                    + txtHumanNameRight.getText());
        }
        else if (toggleLeft.getSelectedToggle().equals(radioLeftHuman)
                && toggleRight.getSelectedToggle().equals(radioRightHuman)) {
            controller.setupGame(txtHumanNameLeft.getText(), txtHumanNameRight.getText());
            primaryStage.setTitle(
                    txtHumanNameLeft.getText()
                    + " vs "
                    + txtHumanNameRight.getText());
        }
        controller.setSpeed(sliderSpeed.getMax()-sliderSpeed.getValue());
        controller.startGame();
        controller.setStatsModel(statsModel);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

    }
}
