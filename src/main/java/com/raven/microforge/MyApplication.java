package com.raven.microforge;

import javafx.application.*;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;


/**
 * User: mjparme
 * Date: 3/21/22
 * Time: 8:49 PM
 */
public class MyApplication extends Application {
    private final static Logger logger = LoggerFactory.getLogger(MyApplication.class);
    public String name ="Microforge";
    public String savename = "test";
    private Button saveButton;
    private Button openButton;
    private TextArea textArea;
    private TextArea terminal;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createTextAreaBox());
        borderPane.setBottom(createButtonBox());


        Scene scene = new Scene(borderPane);
        primaryStage.getIcons().add(new Image("https://avatars.githubusercontent.com/u/132362783?v=4"));
        primaryStage.setTitle(name);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
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
            //only allow text files to be selected using chooser
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
            );
            //set initial directory somewhere user will recognise
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            //let user select file
            File fileToLoad = fileChooser.showOpenDialog(null);
            //if file has been chosen, load it using asynchronous method (define later)
            if(fileToLoad != null){
                loadFileToTextArea(fileToLoad);
            }
        });
    }

    private Node createButtonBox() {
        saveButton = new Button("Save");

        openButton = new Button("Open");


        HBox box = new HBox(5);
        box.setPadding(new Insets(3, 3, 3, 3));
        box.getChildren().addAll(saveButton, openButton);
        box.setAlignment(Pos.CENTER);

        return box;
    }

    private Node createTextAreaBox() {
        textArea = new TextArea("/*Welcome to Microforge*/");
        ScrollPane scrollPane = new ScrollPane(textArea);
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
            logger.debug("error occured during file save");

        }
        }
        
    private Task<String> fileLoaderTask(File fileToLoad){
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }
                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }
                return totalFile.toString();
            }
        };
        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
                //statusMessage.setText("File loaded: " + fileToLoad.getName());
            } catch (InterruptedException | ExecutionException e) {
                terminal.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });
        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            terminal.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
          //  statusMessage.setText("Failed to load file");
        });
        return loadFileTask;
    }
    private void loadFileToTextArea(File fileToLoad) {
        Task<String> loadFileTask = fileLoaderTask(fileToLoad);
        loadFileTask.run();
    }
    public void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //only allow text files to be selected using chooser
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
        );
        //set initial directory somewhere user will recognise
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //let user select file
        File fileToLoad = fileChooser.showOpenDialog(null);
        //if file has been chosen, load it using asynchronous method (define later)
        if(fileToLoad != null){
            loadFileToTextArea(fileToLoad);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
