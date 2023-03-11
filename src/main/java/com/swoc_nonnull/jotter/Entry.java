package com.swoc_nonnull.jotter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;

public class Entry extends Application {
    private Stage primaryStage;

    private Rectangle close;
    static Rectangle plus_vertical;
    static Rectangle plus_horizontal;
    static Circle menu;
    private MenuStage menuStage; // This MenuStage is single and created at the start of this application. This holds
    // the menu when the menu button(Circle) is clicked.

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(screenBounds.getWidth()/2);
        primaryStage.setHeight(screenBounds.getHeight()/2);

        // icon
        File image = new File("src/main/resources/com/swoc_nonnull/jotter/icons/icon_logo_jotter.png");
        primaryStage.getIcons().add(new Image("file:///"+image.getAbsolutePath().replace("\\", "/")));
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(makeUI());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add stylesheet
        File f = new File("src/main/resources/com/swoc_nonnull/jotter/styles/styles.css");
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        /*
         * Add resizer which will help in the resizing of stage.
         * It may not be stored in any variable(referenced by a variable) as its use is not known
        */
        // listen to stage changes
        // Add a listener to the stage width property
        // register those nodes which are supposed to move
        // move menu button and group 'resizer'
        // mainResizer is the resizer that positions resizer for the stage
        // listen to stage changes
        // Add a listener to the stage width property
        // register those nodes which are supposed to move
        // move menu button and group 'resizer'
        @SuppressWarnings("unused")
        Resizer mainResizer = new Resizer(primaryStage, parent, null) {
            @Override
            void performBasics() {
                // listen to stage changes
                // Add a listener to the stage width property
                primaryStage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
                    // register those nodes which are supposed to move
                    // move menu button and group 'resizer'
                    if (menu.getUserData() instanceof NodeUserData nodeUserData)
                        if (nodeUserData.isMovable()) {
                            double radius = Math.sqrt(node_arc_value * node_arc_value * 8);
                            menu.setTranslateX(newWidth.doubleValue() - radius - node_arc_value);
                            plus_vertical.setTranslateX(
                                    newWidth.doubleValue() - radius - node_arc_value - plus_vertical.getWidth() / 2
                            );
                            plus_horizontal.setTranslateX(
                                    newWidth.doubleValue() - radius - node_arc_value - plus_horizontal.getWidth() / 2
                            );
                        }
                });
            }
        };
        arcIfy(parent);
        finalizeUILayoutWithEvents();
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

    private static boolean isStageMaximized = false;
    private double initStageWidth, initStageHeight, initStageSetX, initStageSetY;
    private static int currentParentsChildIndex;
    static { currentParentsChildIndex = 0; }
    static JotArea jotArea;
    private Parent makeUI() {

        Pane parent = new Pane();
        this.parent = parent;

        // minimum width and height for parent
        parent.setMinWidth(screenBounds.getWidth()/2);
        parent.setMinHeight(screenBounds.getHeight()/2);

        // give shape to the parent
        Rectangle shape = new Rectangle(parent.getMinHeight(), parent.getMinWidth());
        shape.setArcWidth(node_arc_value);
        shape.setArcHeight(node_arc_value);
        parent.setShape(shape);

        // Have a stack pane in the background in which TextArea is kept
        StackPane jotAreaHolder = new StackPane();
        // Setting jotAreaHolder's preferred width and height
        jotAreaHolder.setPrefWidth(primaryStage.getWidth());
        jotAreaHolder.setPrefHeight(primaryStage.getHeight()-node_arc_value);
        // Setting jotAreaHolder's translateX and translateY
        jotAreaHolder.setTranslateY(node_arc_value/2);
        jotArea = new JotArea(primaryStage, jotAreaHolder);
        jotArea.setId("textArea");
        parent.getChildren().add(currentParentsChildIndex++, jotAreaHolder);

        parent.setStyle("-fx-background-fill: rgba(.0, .0, .0, .0)");

        // NodeUserData for close, min and max rectangles(buttons)
        NodeUserData min_max_close_Data = new NodeUserData(true, true, false, true);
        // close(Rectangle) which acts as a button when pressed, and it closes the application
        close = new Rectangle(screenBounds.getWidth()/40, screenBounds.getHeight()/90, Color.RED);
        close.setUserData(min_max_close_Data);
        close.setId("title_bar_items__close");
        parent.getChildren().add(currentParentsChildIndex++, close);

        // min(Rectangle) which acts as a button when pressed, and it minimizes the stage(native window)
        Rectangle min = new Rectangle(screenBounds.getWidth() / 40, screenBounds.getHeight() / 90, Color.BLUE);
        min.setUserData(min_max_close_Data);
        min.setId("title_bar_items__min");
        min.setTranslateX(close.getWidth()+close.getHeight());
        min.setOnMouseClicked(mouseEvent -> primaryStage.setIconified(true));
        parent.getChildren().add(currentParentsChildIndex++, min);

        // max(Rectangle) which acts as a button when pressed, and it maximizes the stage(native window)
        Rectangle max = new Rectangle(screenBounds.getWidth() / 40, screenBounds.getHeight() / 90, Color.BLUEVIOLET);
        max.setUserData(min_max_close_Data);
        max.setId("title_bar_items__max");
        max.setTranslateX(min.getTranslateX()+ min.getWidth()+ min.getHeight());
        max.setOnMouseClicked(mouseEvent -> {
            if(isStageMaximized) {
                // restore
                primaryStage.setMaximized(false);
                primaryStage.setWidth(initStageWidth);
                primaryStage.setHeight(initStageHeight);
                // re-position
                primaryStage.setY(initStageSetY);
                primaryStage.setX(initStageSetX);
                // set arc width and height again
                ((Rectangle) parent.getShape()).setArcWidth(node_arc_value);
                ((Rectangle) parent.getShape()).setArcHeight(node_arc_value);
                isStageMaximized = false;
                primaryStage.setMaximized(false);
            } else {
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
                primaryStage.setMaximized(true);
            }
        });
        parent.getChildren().add(currentParentsChildIndex++, max);

        NodeUserData menu_nodeUserData = new NodeUserData(true, false, true, true);
        // Initial sample radius
        double radius = Math.sqrt(node_arc_value*node_arc_value*8);
        menu = new Circle(radius);
        menu.setUserData(menu_nodeUserData);
        menu.setId("menu_circle");
        // no resize translateX value = initial scene size - radius - node_arc_value
        menu.setTranslateX(parent.getMinWidth()-radius-node_arc_value);
        menu.setTranslateY(radius+node_arc_value);

        plus_vertical = new Rectangle();
        plus_vertical.setUserData(menu_nodeUserData);
        plus_vertical.setId("menu_plus_vertical");
        plus_vertical.setWidth(menu.getRadius()/5);
        plus_vertical.setHeight(menu.getTranslateY());
        plus_vertical.setTranslateX(menu.getTranslateX()-plus_vertical.getWidth()/2);
        plus_vertical.setTranslateY(menu.getTranslateY()-(menu.getRadius()-menu.getRadius()/3));
        plus_horizontal = new Rectangle();
        plus_horizontal.setUserData(menu_nodeUserData);
        plus_horizontal.setId("menu_plus_horizontal");
        plus_horizontal.setWidth(menu.getRadius()/5);
        plus_horizontal.setHeight(menu.getTranslateY());
        plus_horizontal.setTranslateX(menu.getTranslateX()-plus_horizontal.getWidth()/2);
        plus_horizontal.setTranslateY(menu.getTranslateY()-(menu.getRadius()-menu.getRadius()/3));
        plus_horizontal.setRotate(90);

        parent.getChildren().add(currentParentsChildIndex++, menu);
        parent.getChildren().add(currentParentsChildIndex++, plus_vertical);
        parent.getChildren().add(currentParentsChildIndex++, plus_horizontal);

        return parent;
    }

    private void finalizeUILayoutWithEvents() {
        // Create a new stage for menu
        menuStage = new MenuStage(
                primaryStage // The current stage(primaryStage) is provided as a constructor parameter
                , menu // The menu button(Circle) also in the constructor parameter
        );
        // attaching actions
        menu.setOnMouseClicked(mouseEvent -> {
            // Display menu
            if(!menuStage.isShowing()) {
                menu.setVisible(false);
                plus_vertical.setVisible(false);
                plus_horizontal.setVisible(false);
                MenuStage.toolStage.show();
                menuStage.show();
            }
        });
        plus_horizontal.setOnMouseClicked(mouseEvent -> {
            // Display menu
            if(!menuStage.isShowing()) {
                menu.setVisible(false);
                plus_vertical.setVisible(false);
                plus_horizontal.setVisible(false);
                MenuStage.toolStage.show();
                menuStage.show();
            }
        });
        plus_vertical.setOnMouseClicked(mouseEvent -> {
            // Display menu
            if(!menuStage.isShowing()) {
                menu.setVisible(false);
                plus_vertical.setVisible(false);
                plus_horizontal.setVisible(false);
                MenuStage.toolStage.show();
                menuStage.show();
            }
        });
        // Stage closing events
        close.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
            // Perform clean-ups
            // Restore jot_Area.css(Re-Write)
            try(FileWriter fileWriter = new FileWriter(JotArea.filePath.substring(6))) {
                for(String line: JotArea.getDefaultCssStrings())
                    fileWriter.write(line+System.lineSeparator());
            }catch (Exception ignored) {}
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setOnCloseRequest(e-> {
            e.consume();
            // Perform clean-ups
            // Restore jot_Area.css(Re-Write)
            try(FileWriter fileWriter = new FileWriter(JotArea.filePath.substring(6))) {
                for(String line: JotArea.getDefaultCssStrings())
                    fileWriter.write(line+System.lineSeparator());
            }catch (Exception ignored) {}
            Platform.exit();
            System.exit(0);
        });
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