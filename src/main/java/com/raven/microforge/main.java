package com.raven.microforge;

import javafx.application.*;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.slf4j.*;

import java.io.*;
import java.util.concurrent.ExecutionException;

public class main extends Application {
    private final static Logger logger = LoggerFactory.getLogger(main.class);
    public String name ="Microforge";
    public String fileMenuName="File";
    public String version = "preAlpha";
    public String terminalText ="this is the terminal, errors among other things will show here";
    public String saveButtonText="save";
    public String settingsButtonText="Settings";
    public String openButtonText="Open";

    public String textAreaText="/*Welcome to Microforge\n" +
            "version =" +version+"*/\n" +
            "void setup(){\n" +
            "//input code here to run once\n" +
            "}\n" +
            "void loop(){\n" +
            "//input code here to run over and over again forever\n" +
            "}";

    public Boolean initialText = true;
    private Button saveButton;
    private Button openButton;
    private Button settingsButton;
    private TextArea textArea;
    private TextArea terminal;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createTextAreaBox());
        borderPane.setBottom(createButtonBox());
        borderPane.setTop(createMenuAreaBox());


        Scene scene = new Scene(borderPane);
        primaryStage.getIcons().add(new Image(main.class.getResourceAsStream("/textures/icon.png")));
        primaryStage.setTitle(name);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        saveButton.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Microcontroller sketch (*.ino)", "*.ino");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file != null){
                SaveFile(textArea.getText(), file);
            }
        });
        openButton.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File fileToLoad = fileChooser.showOpenDialog(null);
            if(fileToLoad != null){
                loadFileToTextArea(fileToLoad);
            }

        });
        settingsButton.setOnAction(e -> settings.display());
    }

    private Node createButtonBox() {
        saveButton = new Button(saveButtonText);
        openButton = new Button(openButtonText);
        settingsButton = new Button(settingsButtonText);

        return null;
    }

    private Node createMenuAreaBox() {
        CustomMenuItem openButtonItem = new CustomMenuItem();
        openButtonItem.setContent(openButton);
        openButtonItem.setHideOnClick(false);
        CustomMenuItem saveButtonItem = new CustomMenuItem();
        saveButtonItem.setContent(saveButton);
        saveButtonItem.setHideOnClick(false);
        CustomMenuItem settingsButtonItem = new CustomMenuItem();
        settingsButtonItem.setContent(settingsButton);
        settingsButtonItem.setHideOnClick(false);

        Menu fileMenu = new Menu();
        fileMenu.getItems().addAll(saveButtonItem, openButtonItem, settingsButtonItem);
        fileMenu.setText(fileMenuName);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);

        VBox vBox = new VBox();
        vBox.getChildren().add(menuBar);
        return (vBox);
    };
    private Node createTextAreaBox() {
        textArea = new TextArea();
        if(initialText){
            textArea.setText(textAreaText);
        }
        else{
            textArea.setText("");
        }
        terminal = new TextArea(terminalText);
        terminal.setEditable(false);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(textArea);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    private void SaveFile(String content, File file){
        try {

            FileWriter fileWriter;
            content = textArea.getText() ;
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            //another error String
            String errorDuringSave = "error occured during file save\n";
            terminal.setText(errorDuringSave);

        }
        }
        
    private Task<String> fileLoaderTask(File fileToLoad){
        //error strings
        String couldNotLoad="Could not load file from:\n " + fileToLoad.getAbsolutePath() + "\n";
        //actual code
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                StringBuilder totalFile = new StringBuilder();
                return totalFile.toString();
            }
        };
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
            } catch (InterruptedException | ExecutionException e) {
                terminal.setText(couldNotLoad);
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            terminal.setText(couldNotLoad);
        });
        return loadFileTask;
    }
    private void loadFileToTextArea(File fileToLoad) {
        Task<String> loadFileTask = fileLoaderTask(fileToLoad);
        loadFileTask.run();
    }
    public void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File fileToLoad = fileChooser.showOpenDialog(null);
        if(fileToLoad != null){
            loadFileToTextArea(fileToLoad);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
//code in java they said, write once run everywhere they said. I hate this