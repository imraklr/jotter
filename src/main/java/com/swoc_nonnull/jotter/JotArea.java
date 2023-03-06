package com.swoc_nonnull.jotter;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;

class JotArea extends TextArea {
    private final Rectangle2D screenBounds;
    private double aspectRatioValue;
    private final Stage stage;
    private final Parent parent;
    JotArea(Stage stage, Parent parent) {
        this.stage = stage;
        this.parent = parent;
        setFocused(true);
        // Positioning
        setTranslateX(.0);
        setTranslateY(.0);

        screenBounds = Screen.getPrimary().getBounds();
        // Width and height
        setWidth(stage.getWidth()-Entry.node_arc_value);
        setHeight(stage.getHeight()-Entry.node_arc_value);
        // Setting same width and height for the parent(StackPane) as well
        {
            // Placeholder
        }
        // Setting Aspect Ratio Value
        aspectRatioValue = screenBounds.getWidth()/screenBounds.getHeight();

        // Apply CSS styling
        File cssFile = new File("src/main/resources/com/swoc_nonnull/jotter/styles/jot_area.css");
        getStylesheets().add("file:///" + cssFile.getAbsolutePath().replace("\\", "/"));

        // Font face
        setFont(Font.font("Arial"));

        if(parent instanceof Pane pane)
            pane.getChildren().add(this);

        makeUI();
        attachListeners();
    }

    private void makeUI() {
        setText("Epsem Flora Tiana");
    }

    private void attachListeners() {
        // listen for stage width and height changes
        stage.widthProperty().addListener((observableValue, number, t1) -> {
            setWidth(t1.doubleValue()-Entry.node_arc_value);
            if(parent instanceof StackPane stackPane)
                stackPane.setPrefWidth(t1.doubleValue()-Entry.node_arc_value);
        });
        stage.heightProperty().addListener((observableValue, number, t1) -> {
            setHeight(t1.doubleValue()-Entry.node_arc_value);
            if(parent instanceof StackPane stackPane)
                stackPane.setPrefHeight(t1.doubleValue()-Entry.node_arc_value);
        });
    }
}
