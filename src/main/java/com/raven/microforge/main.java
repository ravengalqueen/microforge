package com.raven.microforge;

import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.PreferencesFxEvent;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Setting;
import com.dlsc.preferencesfx.model.Group;
import com.dlsc.preferencesfx.view.PreferencesFxDialog;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
//import org.reactfx.Subscription;
import org.slf4j.*;
import org.fxmisc.richtext.*;

import java.io.*;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class main extends Application {
    private final static Logger logger = LoggerFactory.getLogger(main.class);
    private int i = 0;

    private String name = "Microforge";
    private String fileMenuName = "File";
    private String version = "preAlpha 0.0.1";
    private String newFileButtonString = "Ignore";
    private String newSaveString = "Save";
    private String newReturnButtons = "Return";
    private String terminalText = "this is the terminal, errors among other things will show here";
    private String saveButtonText = "save";
    private String settingsButtonText = "Settings";
    private String openButtonText = "Open";
    private String leaveButtonText = "Leave Anyways";
    private String saveLeaveButtonText = "Save before leaving";
    private String returnButtonText = "Return to Microforge";

    private String textAreaText = "/*Welcome to Microforge\n" +
            "version = " + version + "*/\n" +
            "void setup(){\n" +
            "  //input code here to run once\n" +
            "}\n" +
            "void loop(){\n" +
            "  //input code here to run over and over again forever\n" +
            "}";
    private String theme = "dark.css";
    private String leaveString = "Are you sure you want to leave?\n" +
            "you have unsaved changes";
    private String newFileString = "Are you sure you want to make a new File?\n" +
            "you have unsaved changes";
    BooleanProperty iC = new SimpleBooleanProperty(true);
    private Boolean hasSaved = false;
    private Boolean hasChanged = false;
    private Boolean settingsOpen = false;
    private TextArea terminal;
    private CodeArea mainCodeArea;
    private MenuItem openItem;
    private MenuItem saveItem;
    MenuItem newItem;
    private MenuItem settingsItem;
    private MenuButton menuBar;
    private Button okButton;
    private Button leaveButton;
    private Button saveLeaveButton;
    private Button saveNewButton;
    private Button returnButton;
    private Button returnNewButton;
    private Button newFileButton;
    private static ChoiceBox langChoice;
    private static ChoiceBox themeChoice;
    private static ChoiceBox fontChoice;
    IntegerProperty fontsize = new SimpleIntegerProperty(13);
    private static String initialCodeString = "initial code";
     File themes = new File("src/main/resources/themes");
     File[] listOfThemes = themes.listFiles();

     File fonts = new File("src/main/resources/fonts");
     File[] listOfFonts = fonts.listFiles();

     private String font = "Miracode";
    ObservableList themeItems;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createTextAreaBox());
        borderPane.setTop(createMenuAreaBox());

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("/themes/" + theme).toExternalForm());
        primaryStage.getIcons().add(new Image(main.class.getResourceAsStream("/textures/logonormal.png")));
        primaryStage.setTitle(name);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        Platform.setImplicitExit(false);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(600);
        primaryStage.setOnCloseRequest(event -> {
            if (hasChanged == true && hasSaved == false) {

                BorderPane leavePane = new BorderPane();
                leavePane.setBottom(createButtonBox());
                Stage leaveDialog = new Stage();
                leaveDialog.setResizable(false);
                leaveDialog.initStyle(StageStyle.UNDECORATED);
                leaveDialog.initModality(Modality.APPLICATION_MODAL);
                leaveDialog.initOwner(primaryStage);
                HBox idkHBox = new HBox(30);
                idkHBox.setAlignment(Pos.CENTER);
                idkHBox.getChildren().addAll(saveLeaveButton, returnButton, leaveButton);
                Label leaveLabel = new Label(leaveString);
                leaveLabel.setTextAlignment(TextAlignment.CENTER);
                leaveLabel.getStyleClass().add("labelC");
                VBox idkBox = new VBox(20);
                idkBox.setAlignment(Pos.CENTER);
                idkBox.getChildren().addAll(leaveLabel, idkHBox);
                Scene leaveScene = new Scene(idkBox, 450, 200);
                leaveScene.getStylesheets().add(getClass().getResource("/themes/" + theme).toExternalForm());
                leaveDialog.setScene(leaveScene);
                leaveButton.setOnAction((leaveEvent) ->
                        primaryStage.close()
                );
                returnButton.setOnAction((returnEvent) -> {
                    event.consume();
                    leaveDialog.hide();
                });
                saveLeaveButton.setOnAction((saveBeforeEvent) -> {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter =
                            new FileChooser.ExtensionFilter("Microcontroller sketch (*.ino)", "*.ino");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        SaveFile(mainCodeArea.getText(), file);
                    }
                    hasSaved = true;
                    primaryStage.close();
                });
                leaveDialog.showAndWait();
            }
        });

        saveItem.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Microcontroller sketch (*.ino)", "*.ino");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                SaveFile(mainCodeArea.getText(), file);
            }
            hasSaved = true;
        });
        openItem.setOnAction((ActionEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Microcontroller sketches (*.ino)", "*.ino")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File fileToLoad = fileChooser.showOpenDialog(null);
            if (fileToLoad != null) {
                loadFileToTextArea(fileToLoad);
            }
        });
        newItem.setOnAction((event -> {
            if (hasSaved == false) {

                BorderPane leavePane = new BorderPane();
                leavePane.setBottom(createButtonBox());
                Stage newDialog = new Stage();
                newDialog.setResizable(false);
                newDialog.initStyle(StageStyle.UNDECORATED);
                newDialog.initModality(Modality.APPLICATION_MODAL);
                newDialog.initOwner(primaryStage);
                HBox idkHBox = new HBox(30);
                idkHBox.setAlignment(Pos.CENTER);
                idkHBox.getChildren().addAll(saveNewButton, returnNewButton, newFileButton);
                Label newLabel = new Label(newFileString);
                newLabel.setTextAlignment(TextAlignment.CENTER);
                newLabel.getStyleClass().add("labelC");
                VBox idkBox = new VBox(20);
                idkBox.setAlignment(Pos.CENTER);
                idkBox.getChildren().addAll(newLabel, idkHBox);
                Scene newScene = new Scene(idkBox, 450, 200);
                newScene.getStylesheets().add(getClass().getResource("/themes/" + theme).toExternalForm());
                newDialog.setScene(newScene);
                returnButton.setOnAction((returnEvent) -> {
                    newDialog.hide();
                });
                saveNewButton.setOnAction((saveBeforeEvent) -> {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter =
                            new FileChooser.ExtensionFilter("Microcontroller sketch (*.ino)", "*.ino");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        SaveFile(mainCodeArea.getText(), file);
                        hasSaved = true;
                    }
                    if (iC.get() == true) {
                        mainCodeArea.replaceText(textAreaText);
                    } else {
                        mainCodeArea.replaceText("");
                    }
                    newDialog.hide();
                });
                newFileButton.setOnAction(event1 -> {
                    if (iC.get() == true) {
                        mainCodeArea.replaceText(textAreaText);
                    } else {
                        mainCodeArea.replaceText("");
                    }
                    newDialog.hide();
                });
                newDialog.showAndWait();
            }
        }));


        settingsItem.setOnAction(event -> {
            if (!settingsOpen) {
                preferencesFx();
            }
        });
    }

    public void preferencesFx() {
        ObservableList fontItems = FXCollections.observableArrayList(Arrays.asList(listOfFonts));
        ObjectProperty font = new SimpleObjectProperty<>(listOfFonts[0]);

        themeItems = FXCollections.observableArrayList(Arrays.asList(listOfThemes));
        ObjectProperty theme = new SimpleObjectProperty<>(listOfThemes[0]);
        PreferencesFx preferencesFx =
                PreferencesFx.of(main.class,
                        Category.of("Main",
                                Setting.of("Initial Code", iC),
                                Setting.of("Font", fontItems, font),
                                Setting.of("Theme", themeItems, theme),
                                Setting.of("Font size", fontsize, 2, 40)
                        )
                );
        preferencesFx.buttonsVisibility(false);
        settingsOpen = true;
        preferencesFx.dialogTitle("settings");
        preferencesFx.persistWindowState(true);
        preferencesFx.persistApplicationState(true);
        preferencesFx.addEventHandler(PreferencesFxEvent.EVENT_PREFERENCES_SAVED, event -> {
            settingsOpen = false;
        });
        preferencesFx.show();
    }


    private Node createMenuAreaBox() {
        newItem = new MenuItem("New File");
        openItem = new MenuItem(openButtonText);
        saveItem = new MenuItem(saveButtonText);
        settingsItem = new MenuItem(settingsButtonText);
        menuBar = new MenuButton();
        menuBar.setText(fileMenuName);
        menuBar.getItems().addAll(saveItem, openItem, newItem, settingsItem);
        menuBar.getStyleClass().add("menuBar");
        VBox vBox = new VBox();
        vBox.getChildren().add(menuBar);
        vBox.getStyleClass().add("topMenuBar");
        return (vBox);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        return spansBuilder.create();
    }

    private Node createTextAreaBox() {
        mainCodeArea = new CodeArea();
        mainCodeArea.getStyleClass().add("main-code-area");
        mainCodeArea.setId("mainCodeArea");
        mainCodeArea.setStyle("@font-face {font-family:idfc; src:url("+font +") }" +
                "-fx-font-family:idfc;" +
                "-fx-font-size: " + fontsize + ";");
        mainCodeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                i = i + 1;
                if (i != 1) {
                    hasChanged = true;
                    hasSaved = false;
                }
            }
        });

        final Pattern whiteSpace = Pattern.compile("\\B\\s+");
        mainCodeArea.addEventHandler(KeyEvent.KEY_PRESSED, KE ->
        {
            if (KE.getCode() == KeyCode.ENTER) {
                int caretPosition = mainCodeArea.getCaretPosition();
                int currentParagraph = mainCodeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(mainCodeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> mainCodeArea.insertText(caretPosition, m0.group()));
            }
        });


        mainCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(mainCodeArea));
        if (iC.get() == true) {
            mainCodeArea.replaceText(textAreaText);
        } else {
            mainCodeArea.replaceText("");
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainCodeArea);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("scrollPane");
        return scrollPane;
    }

    private Node createButtonBox() {
        okButton = new Button("ok");
        okButton.getStyleClass().add("settingsButton");
        saveLeaveButton = new Button(saveLeaveButtonText);
        saveLeaveButton.getStyleClass().add("leaveButton");
        returnButton = new Button(returnButtonText);
        returnButton.getStyleClass().add("returnButton");
        returnButton.getStyleClass().add("leaveButton");
        leaveButton = new Button(leaveButtonText);
        leaveButton.getStyleClass().add("leaveButton");
        newFileButton = new Button(newFileButtonString);
        newFileButton.getStyleClass().add("leaveButton");
        returnNewButton = new Button(newReturnButtons);
        returnNewButton.getStyleClass().add("returnButton");
        returnNewButton.getStyleClass().add("leaveButton");
        saveNewButton = new Button(newSaveString);
        saveNewButton.getStyleClass().add("leaveButton");
        return null;
    }


    private void SaveFile(String content, File file) {
        try {

            FileWriter fileWriter;
            content = mainCodeArea.getText();
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            //another error String
            String errorDuringSave = "error occured during file save\n";
            terminal.setText(errorDuringSave);

        }
    }

    private Task<String> fileLoaderTask(File fileToLoad) {
        //error strings
        String couldNotLoad = "Could not load file from:\n " + fileToLoad.getAbsolutePath() + "\n";
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
                mainCodeArea.replaceText(loadFileTask.get());
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
        if (fileToLoad != null) {
            loadFileToTextArea(fileToLoad);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
//code in java they said, write once run everywhere they said. I hate this