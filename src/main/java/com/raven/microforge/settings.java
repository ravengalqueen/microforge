package com.raven.microforge;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class settings{
    private static Button okButton;
    private static CheckBox initialCode;
    private static ChoiceBox langChoice;

    public static String settings="Settings";
    public static String ok ="ok";
    public static String initialCodeString="initial code";
    public static void display()
    {
        BorderPane borderPane = new BorderPane();
        borderPane.setBottom(createButtonBox());
        borderPane.setBottom(createCheckBox());
        borderPane.setBottom(createChoiceBox());
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(settings);
        popupwindow.setResizable(false);

        Label label1= new Label(settings);

        okButton.setOnAction((ActionEvent event) -> {
            initialCode.setSelected(initialCode.isSelected());
            popupwindow.close();
        });

        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, langChoice, initialCode, okButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }
    private static Node createButtonBox() {
        okButton = new Button(ok);
        okButton.getStyleClass().add("settingsButton");
        return null;
    }
    private static Node createChoiceBox(){
        langChoice = new ChoiceBox();
        langChoice.getItems().addAll("English","Deutsch","Esp\u00f1ol","Lingua latina", "Nederlands");
        langChoice.getStyleClass().add("settingsChoiceBox");
        langChoice.setValue("English");
        return null;
    }
    private static Node createCheckBox(){
        initialCode = new CheckBox();
        initialCode.getStyleClass().add("settingsCheckBox");
        initialCode.setText(initialCodeString);
        initialCode.setSelected(true);
        return null;
    }


}
