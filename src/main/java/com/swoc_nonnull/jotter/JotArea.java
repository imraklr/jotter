package com.swoc_nonnull.jotter;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

class JotArea extends TextArea {
    private final Stage stage;
    private final Parent parent;
    public static String filePath;
    private static final List<String> defaultCssStrings; static { defaultCssStrings = new ArrayList<>(); }

    JotArea(Stage stage, Parent parent) {
        this.stage = stage;
        this.parent = parent;
        // Disable context menu
        setContextMenu(new ContextMenu());
        setFocused(true);
        // Positioning
        setTranslateX(.0);
        setTranslateY(.0);

        // Width and height
        setWidth(stage.getWidth());
        setHeight(stage.getHeight()-Entry.node_arc_value);

        // Apply CSS styling
        File cssFile = new File("src/main/resources/com/swoc_nonnull/jotter/styles/jot_area.css");
        filePath = "file:///" + cssFile.getAbsolutePath().replace("\\", "/");
        getStylesheets().add(filePath);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(cssFile))) {
            String line;
            while((line = bufferedReader.readLine())!=null)
                defaultCssStrings.add(line);
        }catch (Exception ignored) {}
        // Font face
        setFont(Font.font("Arial"));

        if(parent instanceof Pane pane)
            pane.getChildren().add(this);

        makeUI();
        attachListeners();
    }

    private void makeUI() {
        setText("Love");
    }

    private void attachListeners() {
        // listen for stage width and height changes
        stage.widthProperty().addListener((observableValue, number, t1) -> {
            setWidth(t1.doubleValue());
            if(parent instanceof StackPane stackPane)
                stackPane.setPrefWidth(t1.doubleValue());
        });
        stage.heightProperty().addListener((observableValue, number, t1) -> {
            setHeight(t1.doubleValue()-Entry.node_arc_value);
            if(parent instanceof StackPane stackPane)
                stackPane.setPrefHeight(t1.doubleValue()-Entry.node_arc_value);
        });
        // listen for focus property
        focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(isFocused()) {
                if (MenuStage.menuStage.isShowing()) {
                    // hide with fade disappearance
                    FadeTransition fadeTransition = new FadeTransition();
                    fadeTransition.setNode(MenuStage.menuStage.getScene().getRoot());
                    fadeTransition.setRate(0.4);
                    fadeTransition.setFromValue(1.);
                    fadeTransition.setToValue(.0);
                    fadeTransition.play();
                    fadeTransition.setOnFinished(e -> {
                        // Hide the stage(close)
                        MenuStage.menuStage.close();
                        MenuStage.toolStage.close();
                        Entry.menu.setVisible(true);
                        Entry.plus_vertical.setVisible(true);
                        Entry.plus_horizontal.setVisible(true);
                    });
                }
                if (MenuStage.toolStage.isShowing()) {
                    FadeTransition fadeTransition = new FadeTransition();
                    fadeTransition.setNode(MenuStage.toolStage.getScene().getRoot());
                    fadeTransition.setRate(0.4);
                    fadeTransition.setFromValue(1.);
                    fadeTransition.setToValue(.0);
                    fadeTransition.play();
                    fadeTransition.setOnFinished(e -> {
                        // Hide the stage(close)
                        MenuStage.toolStage.close();
                        MenuStage.toolStage.close();
                        Entry.menu.setVisible(true);
                        Entry.plus_vertical.setVisible(true);
                        Entry.plus_horizontal.setVisible(true);
                    });
                }
            }
        });
    }

    // On window exit, the CSS file might not get edited properly so the original file content is maintained through
    // getDefaultCssStrings() method which returns a String which is then written in the CSS file so that on
    // relaunch, default CSS is loaded
    public static List<String> getDefaultCssStrings() {
        return defaultCssStrings;
    }
}
