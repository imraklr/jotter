package com.swoc_nonnull.jotter;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

        // icon
        File image = new File("src/main/resources/com/swoc_nonnull/jotter/icons/icon_logo_jotter.png");
        primaryStage.getIcons().add(new Image("file:///"+image.getAbsolutePath().replace("\\", "/")));
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(makeUI());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add other functionalities

        // Add stylesheet
        File f = new File("src/main/resources/com/swoc_nonnull/jotter/styles/styles.css");
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        /*
         * Add resizer which will help in the resizing of stage.
         * It may not be stored in any variable(referenced by a variable) as its use is not known
        */
        new Resizer(primaryStage, parent, true, screenBounds.getWidth(), screenBounds.getHeight()) {
            @Override
            void performBasics() {
                // listen to stage changes
                // Add a listener to the stage width property
                primaryStage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
                    // restrict stage resizing beyond certain limit
                    // placeholder
                    // register those nodes which are supposed to move
                    // move card adder button
                    Circle cardAdder = (Circle) parent.getChildrenUnmodifiable().get(3);
                    Rectangle plus_vertical = (Rectangle) parent.getChildrenUnmodifiable().get(4);
                    Rectangle plus_horizontal = (Rectangle) parent.getChildrenUnmodifiable().get(5);
                    if(cardAdder.getUserData() instanceof NodeUserData nodeUserData)
                        if(nodeUserData.isMovable()) {
                            double radius = Math.sqrt(node_arc_value*node_arc_value*8);
                            cardAdder.setTranslateX(newWidth.doubleValue()-
                                    radius-node_arc_value);
                            plus_vertical.setTranslateX(
                                    newWidth.doubleValue()-radius-node_arc_value-
                                            ((Rectangle)parent.getChildrenUnmodifiable().get(4)).getWidth()/2
                            );
                            plus_horizontal.setTranslateX(
                                    newWidth.doubleValue()-radius-node_arc_value-
                                            ((Rectangle)parent.getChildrenUnmodifiable().get(5)).getWidth()/2
                            );
                        }
                });

                // Add a listener to the stage height property
                primaryStage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
                    // placeholder
                });
            }
        };
        arcIfy(parent);
    }

    static double node_arc_value;
    private Parent parent;
    Rectangle2D screenBounds;
    {
        screenBounds = Screen.getPrimary().getBounds();
        node_arc_value = screenBounds.getWidth() > screenBounds.getHeight()?
                20*(screenBounds.getHeight()/screenBounds.getWidth()):
                20*(screenBounds.getWidth()/screenBounds.getHeight());
    }

    private boolean isStageMaximized = false;
    private double initStageWidth, initStageHeight, initStageSetX, initStageSetY;
    private Parent makeUI() {

        Pane parent = new Pane();
        this.parent = parent;

        // minimum width and height for parent
        parent.setMinWidth(1100);
        parent.setMinHeight(800);

        // give shape to the parent
        Rectangle shape = new Rectangle(parent.getMinHeight(), parent.getMinWidth());
        shape.setArcWidth(node_arc_value);
        shape.setArcHeight(node_arc_value);
        parent.setShape(shape);

        // NodeUserData for close, min and max rectangles(buttons)
        NodeUserData min_max_close_Data = new NodeUserData(true, true, false, true);
        // close(Rectangle) which acts as a button when pressed, and it closes the application
        Rectangle close = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.RED);
        close.setUserData(min_max_close_Data);
        close.setId("title_bar_items__close");
        close.setOnMouseClicked(mouseEvent -> System.exit(0));
        parent.getChildren().add(0, close);

        // min(Rectangle) which acts as a button when pressed, and it minimizes the stage(native window)
        Rectangle min = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.BLUE);
        min.setUserData(min_max_close_Data);
        min.setId("title_bar_items__min");
        min.setTranslateX(close.getWidth()+close.getHeight());
        min.setOnMouseClicked(mouseEvent -> primaryStage.setIconified(true));
        parent.getChildren().add(1, min);

        // max(Rectangle) which acts as a button when pressed, and it maximizes the stage(native window)
        Rectangle max = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.BLUEVIOLET);
        max.setUserData(min_max_close_Data);
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
                ((Rectangle) parent.getShape()).setArcWidth(node_arc_value);
                ((Rectangle) parent.getShape()).setArcHeight(node_arc_value);
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

        NodeUserData card_adder = new NodeUserData(true, false, true, true);
        // Initial sample radius and sample color
        double radius = Math.sqrt(node_arc_value*node_arc_value*8);
        Circle cardAdder = new Circle(radius);
        cardAdder.setUserData(card_adder);
        cardAdder.setId("card_adder_circle");
        // no resize translateX value = initial scene size - radius - node_arc_value
        cardAdder.setTranslateX(parent.getMinWidth()-radius-node_arc_value);
        cardAdder.setTranslateY(radius+node_arc_value);

        Rectangle plus_vertical = new Rectangle();
        plus_vertical.setUserData(card_adder);
        plus_vertical.setId("card_adder_plus_vertical");
        plus_vertical.setWidth(cardAdder.getRadius()/5);
        plus_vertical.setHeight(cardAdder.getTranslateY());
        plus_vertical.setTranslateX(cardAdder.getTranslateX()-plus_vertical.getWidth()/2);
        plus_vertical.setTranslateY(cardAdder.getTranslateY()-(cardAdder.getRadius()-cardAdder.getRadius()/3));
        Rectangle plus_horizontal = new Rectangle();
        plus_horizontal.setUserData(card_adder);
        plus_horizontal.setId("card_adder_plus_horizontal");
        plus_horizontal.setWidth(cardAdder.getRadius()/5);
        plus_horizontal.setHeight(cardAdder.getTranslateY());
        plus_horizontal.setTranslateX(cardAdder.getTranslateX()-plus_horizontal.getWidth()/2);
        plus_horizontal.setTranslateY(cardAdder.getTranslateY()-(cardAdder.getRadius()-cardAdder.getRadius()/3));
        plus_horizontal.setRotate(90);
        // attaching actions
        cardAdder.setOnMouseClicked(mouseEvent -> {
            // Add a card on the view
        });
        plus_horizontal.setOnMouseClicked(mouseEvent -> {
            // Add a card on the view
        });
        plus_vertical.setOnMouseClicked(mouseEvent -> {
            // Add a card on the view
        });

        parent.getChildren().add(3, cardAdder);
        parent.getChildren().add(4, plus_vertical);
        parent.getChildren().add(5, plus_horizontal);

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
            if(which instanceof Rectangle)
                if(((Rectangle) which).getUserData()!=null)
                    // Check if this particular nodes' data is an instanceof NodeUserData class
                    if(((Rectangle) which).getUserData() instanceof NodeUserData nodeUserData)
                        // Check if this particular node is arcifiable
                        if(nodeUserData.isArcifiable()) {
                            ((Rectangle) which).setArcHeight(node_arc_value);
                            ((Rectangle) which).setArcWidth(node_arc_value);
                        }
        }
    }
}
