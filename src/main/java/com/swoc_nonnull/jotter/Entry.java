package com.swoc_nonnull.jotter;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class Entry extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(makeUI());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add other functionalities

        // Add stylesheet
        File f = new File("src/main/resources/com/swoc_nonnull/jotter/styles/styles.css");
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        // Add resizer which helps in resizing all nodes in the visible scene
        Resizer resizer = new Resizer(scene.getRoot());
    }

    final double node_arcWidth;
    final double node_arcHeight;
    Rectangle2D screenBounds;
    {
        screenBounds = Screen.getPrimary().getBounds();
        node_arcHeight = 20*(screenBounds.getWidth()/screenBounds.getHeight());
        node_arcWidth = 20*(screenBounds.getHeight()/screenBounds.getWidth());
    }

    private boolean isStageMaximized = false;
    private double initStageWidth, initStageHeight, initStageSetX, initStageSetY;
    private Parent makeUI() {

        FlowPane parent = new FlowPane();

        // minimum width and height for parent
        parent.setMinWidth(1100);
        parent.setMinHeight(800);

        // give shape to the parent
        Rectangle shape = new Rectangle(parent.getMinHeight(), parent.getMinWidth());
        shape.setArcWidth(node_arcWidth);
        shape.setArcHeight(node_arcHeight);
        parent.setShape(shape);

        // close(Rectangle) which acts as a button when pressed, and it closes the application
        Rectangle close = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.RED);
        close.setId("title_bar_items__close");
        close.setOnMouseClicked(mouseEvent -> System.exit(0));
        parent.getChildren().add(0, close);

        // min(Rectangle) which acts as a button when pressed, and it minimizes the stage(native window)
        Rectangle min = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.BLUE);
        min.setId("title_bar_items__min");
        min.setTranslateX(close.getWidth()+close.getHeight());
        min.setOnMouseClicked(mouseEvent -> primaryStage.setIconified(true));
        parent.getChildren().add(1, min);

        // max(Rectangle) which acts as a button when pressed, and it maximizes the stage(native window)
        Rectangle max = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.BLUEVIOLET);
        max.setId("title_bar_items__max");
        max.setTranslateX(min.getTranslateX()+min.getWidth()+min.getHeight());
        max.setOnMouseClicked(mouseEvent -> {
            if(isStageMaximized) {
                // restore
                primaryStage.setWidth(initStageWidth);
                primaryStage.setHeight(initStageHeight);
                // re-position
                primaryStage.setY(initStageSetY);
                primaryStage.setX(initStageSetX);
                // set arc width and height again
                ((Rectangle) parent.getShape()).setArcWidth(node_arcWidth);
                ((Rectangle) parent.getShape()).setArcHeight(node_arcHeight);
                isStageMaximized = false;
            }
            else {
                // store initial width and height
                initStageHeight = primaryStage.getHeight();
                initStageWidth = primaryStage.getWidth();
                initStageSetX = primaryStage.getX();
                initStageSetY = primaryStage.getY();
                // maximize primary stage
                primaryStage.setWidth(screenBounds.getMaxX());
                primaryStage.setHeight(screenBounds.getMaxY());
                // remove arc width and height(set it to zero(0))
                ((Rectangle) parent.getShape()).setArcWidth(0);
                ((Rectangle) parent.getShape()).setArcHeight(0);
                // reposition to (0, 0)
                primaryStage.setX(0);
                primaryStage.setY(0);
                isStageMaximized = true;
            }
        });
        parent.getChildren().add(2, max);

        arcIfy(parent);

        return parent;
    }

    /*
     * A method that sets default arc width and height for rectangles(possibly other nodes as well[Future feature]).
     * This is a recursive method which applies same properties to various nodes.
     */
    private <T> void arcIfy(T which) {
        if(which instanceof Parent) {
            ObservableList<Node> children = ((Parent) which).getChildrenUnmodifiable();
            for(Node in: children)
                arcIfy(in);
        }
        else {
            if(which instanceof Rectangle) {
                ((Rectangle) which).setArcHeight(node_arcHeight);
                ((Rectangle) which).setArcWidth(node_arcWidth);
            }
        }
    }
}
