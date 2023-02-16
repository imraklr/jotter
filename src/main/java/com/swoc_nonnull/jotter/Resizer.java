package com.swoc_nonnull.jotter;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class Resizer {
    private Group resizer;
    private final NodeUserData resizerUI_data;
    private final Stage stage;
    private final double stage_minWidth, stage_minHeight;
    private final Parent parent;
    double lastX, lastY;

    Resizer(Stage stage, Parent parent, boolean performForStage, double xLimit, double yLimit) {
        this.stage = stage;
        stage_minWidth = Screen.getPrimary().getBounds().getWidth()*0.2;
        stage_minHeight = Screen.getPrimary().getBounds().getHeight()*0.4;
        if(xLimit==-1)
            xLimit = stage_minWidth;
        else
            xLimit *= 0.2;
        if(yLimit==-1)
            yLimit = stage_minHeight;
        else
            yLimit *= 0.4;
        resizerUI_data = new NodeUserData(false, false, true, true);
        this.parent = parent;
        makeUI();
        performActions(performForStage, xLimit, yLimit);
    }
    @SuppressWarnings("unused")
    Resizer(NodeUserData resizerUI_data, Stage stage, Parent parent, boolean performForStage, double xLimit,
            double yLimit) {
        this.stage = stage;
        stage_minWidth = Screen.getPrimary().getBounds().getWidth()*0.2;
        stage_minHeight = Screen.getPrimary().getBounds().getHeight()*0.4;
        if(xLimit==-1)
            xLimit = stage_minWidth;
        else
            xLimit *= 0.2;
        if(yLimit==-1)
            yLimit = stage_minHeight;
        else
            yLimit *= 0.4;
        this.resizerUI_data = resizerUI_data;
        this.parent = parent;
        makeUI();
        performActions(performForStage, xLimit, yLimit);
    }

    void makeUI() {
        Rectangle resizerUI = new Rectangle();
        NodeUserData resizerUI_Data = new NodeUserData(false, false, true, true);
        resizerUI.setUserData(resizerUI_Data);

        // setting size
        resizerUI.setWidth(stage.getScene().getWidth()*0.03);
        resizerUI.setHeight(stage.getScene().getHeight()*0.03);

        // positioning
        resizerUI.setTranslateX(stage.getScene().getWidth()-resizerUI.getWidth());
        resizerUI.setTranslateY(stage.getScene().getHeight()-resizerUI.getHeight());

        // making resizerUI transparent
        resizerUI.setOpacity(.0);

        // Adding circles
        Circle[] circles = {
                new Circle(stage.getScene().getWidth()*0.002, Color.WHITE),
                new Circle(stage.getScene().getWidth()*0.002, Color.YELLOW),
                new Circle(stage.getScene().getWidth()*0.002, Color.PEACHPUFF),
                new Circle(stage.getScene().getWidth()*0.002, Color.CORNFLOWERBLUE),
                new Circle(stage.getScene().getWidth()*0.002, Color.FIREBRICK),
                new Circle(stage.getScene().getWidth()*0.002, Color.PALEGREEN)
        };

        // positioning circles
        double twentyPercent_X =  resizerUI.getWidth()*0.2;
        double twentyPercent_Y =  resizerUI.getHeight()*0.2;
        double resizerUI_endX = resizerUI.getTranslateX()+resizerUI.getWidth()-twentyPercent_X;
        double resizerUI_endY = resizerUI.getTranslateY()+resizerUI.getHeight()-twentyPercent_Y;

        // baseline 3 circles
        {
            circles[0].setTranslateX(resizerUI_endX);
            circles[0].setTranslateY(resizerUI_endY);
            resizerUI_endX -= twentyPercent_X;
            circles[1].setTranslateX(resizerUI_endX);
            circles[1].setTranslateY(resizerUI_endY);
            resizerUI_endX -= twentyPercent_X;
            circles[2].setTranslateX(resizerUI_endX);
            circles[2].setTranslateY(resizerUI_endY);
            resizerUI_endX += 2 * twentyPercent_X;
        }

        // one step above baseline(2 circles)
        {
            resizerUI_endY -= twentyPercent_Y;
            circles[3].setTranslateY(resizerUI_endY);
            circles[3].setTranslateX(resizerUI_endX);
            resizerUI_endX -= twentyPercent_X;
            circles[4].setTranslateX(resizerUI_endX);
            circles[4].setTranslateY(resizerUI_endY);
            resizerUI_endX += twentyPercent_X;
        }

        // top circle
        {
            resizerUI_endY -= twentyPercent_Y;
            circles[5].setTranslateX(resizerUI_endX);
            circles[5].setTranslateY(resizerUI_endY);
        }

        resizer = new Group();
        resizer.getChildren().addAll(circles);
        resizer.getChildren().add(resizerUI);

        if(parent instanceof Pane)
            ((Pane) parent).getChildren().add(resizer);
    }

    private void performActions(boolean performForStage, double xLimit, double yLimit) {
        performBasics();

        AtomicBoolean wasPressed = new AtomicBoolean(false);
        resizer.setOnMouseDragExited(mouseDragEvent -> stage.getScene().setCursor(Cursor.DEFAULT));
        resizer.setOnMouseReleased(mouseEvent -> stage.getScene().setCursor(Cursor.DEFAULT));
        resizer.setOnMouseEntered(mouseEvent -> stage.getScene().setCursor(Cursor.SE_RESIZE));
        resizer.setOnMousePressed(mouseEvent -> {
            stage.getScene().setCursor(Cursor.SE_RESIZE);
            if(!wasPressed.get()) {
                lastX = mouseEvent.getX();
                lastY = mouseEvent.getY();
                wasPressed.set(true);
            }
            else {
                lastX = mouseEvent.getSceneX();
                lastY = mouseEvent.getSceneY();
            }
        });
        resizer.setOnMouseDragged(mouseEvent -> {
            double deltaX = mouseEvent.getSceneX() - lastX;
            double deltaY = mouseEvent.getSceneY() - lastY;
            double newX = resizer.getTranslateX() + deltaX;
            double newY = resizer.getTranslateY() + deltaY;
            if(resizerUI_data.isMovable()) {
                if(performForStage) {
                    // then look for change in size of stage along-with limits
                    if (stage_minWidth < stage.getWidth() + deltaX)
                        resizer.setTranslateX(newX);
                    if (stage_minHeight < stage.getHeight() + deltaY)
                        resizer.setTranslateY(newY);
                }
                else {
                    // look for change in size of parent node alon-with limits
                    // placeholder
                    // use xLimit and yLimit here
                }
            }
            if(performForStage) {
                if(stage_minWidth<stage.getWidth()+deltaX)
                    stage.setWidth(stage.getWidth() + deltaX);
                if(stage_minHeight<stage.getHeight()+deltaY)
                    stage.setHeight(stage.getHeight() + deltaY);
            }
            lastX = mouseEvent.getSceneX();
            lastY = mouseEvent.getSceneY();

            // Re-calibrate corners(arcs) and set it for parent
            if(parent instanceof Pane) {
                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                double ratio = screenBounds.getWidth() > screenBounds.getHeight()?
                        20*(screenBounds.getHeight()/screenBounds.getWidth()):
                        20*(screenBounds.getWidth()/screenBounds.getHeight());
                ((Rectangle)((Pane) parent).getShape()).setArcWidth(ratio);
                ((Rectangle)((Pane) parent).getShape()).setArcHeight(ratio);
            }
        });
    }

    /*
     * performBasics method gives flexibility to node movements.
     * For example card view adder button has to be moved in accordance with stage resize. So listening to stage
     * width and height changes becomes necessary here OR a card view which may require its outlines to be reshaped
     * accordingly. So this method is abstract for this reason
     * NOTE: This method is the first method call in makeUI method as outlines may require
     * more attention
    */
    abstract void performBasics();
}