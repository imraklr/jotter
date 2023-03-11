package com.swoc_nonnull.jotter;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileWriter;
import java.util.List;

class MenuStage extends Stage {
    private Stage preceedingStage;
    static Stage toolStage;
    private Circle menuButton;
    static MenuStage menuStage;

    private MenuStage() {
        menuStage = this;
        // Any Stage defaults prior to manual configuration goes here
        setResizable(false);
        initModality(Modality.NONE);
        initStyle(StageStyle.TRANSPARENT);
    }
    MenuStage(Stage preceedingStage, Circle menuButton) {
        this();
        // setting the instance variable(s)
        this.preceedingStage = preceedingStage;
        this.menuButton = menuButton;
        // Stage configurations
        setIconified(false);
        initOwner(preceedingStage);
        // Setting the width and height of this stage
        setWidth(menuButton.getRadius()*9);
        setHeight(menuButton.getRadius()*9);
        // translating to the center of menu button(Circle)
        setX(preceedingStage.getX()+preceedingStage.getWidth()-getWidth()-menuButton.getTranslateY()*0.5);
        setY(preceedingStage.getY()+menuButton.getTranslateY()*0.5);

        // Create a scene
        Scene scene = new Scene(makeUI());
        scene.setFill(Color.TRANSPARENT);
        // Set this scene on the stage
        setScene(scene);

        createToolStage();

        // Attach necessary listeners
        attachActions();
    }

    private StackPane parent;
    private Parent makeUI() {
        parent = new StackPane();

        // Giving shape to the parent(Pane)
        Circle shape = new Circle(getWidth()); // getWidth() or getHeight()
        parent.setShape(shape);

        // Set the linear gradient to the parent(StackPane)
        parent.setStyle("-fx-background-color: linear-gradient(to bottom right, #ff0000, #ff4000, #ff8000, #ffbf00, " +
                "#ffff00, #bfff00, #00ff00, #00ffbf, #0080ff, #bf00ff)");
        parent.setEffect(new Glow(2.0));

        // Make the contents

        Circle obstruction = new Circle((shape.getRadius() - shape.getRadius() * 0.1) / 2);

        Group colorWheel = new Group(obstruction);
        parent.getChildren().add(colorWheel);

        return parent;
    }

    private void attachActions() {
        setOnShowing(event -> {
            // Add fade appearance and to the parent
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(getScene().getRoot());
            fadeTransition.setRate(0.4);
            fadeTransition.setFromValue(.0);
            fadeTransition.setToValue(1.);
            fadeTransition.play();
        });
        toolStage.setOnShowing(event -> {
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setNode(toolStage.getScene().getRoot());
            fadeTransition.setRate(0.4);
            fadeTransition.setFromValue(.0);
            fadeTransition.setToValue(1.);
            fadeTransition.play();
        });
        parent.setOnMouseDragged(event->{
            double x = event.getX();
            double y = event.getY();
            colorUtility(x, y);
        });
        parent.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();
            colorUtility(x, y);
        });
        preceedingStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setX(preceedingStage.getX() + preceedingStage.getWidth() - getWidth() - menuButton.getRadius() * 0.5);
                setY(preceedingStage.getY() + menuButton.getRadius() * 0.5);
            }
        });
        preceedingStage.widthProperty().addListener((observableValue, number, t1) -> {
            if (!preceedingStage.isMaximized())
                setX(preceedingStage.getX()+t1.doubleValue() - getWidth() - menuButton.getRadius() * 0.5);
        });
        preceedingStage.heightProperty().addListener((observableValue, number, t1) -> {
            if (!preceedingStage.isMaximized())
                setY(preceedingStage.getY() + menuButton.getRadius() * 0.5);
        });
        menuStage.xProperty().addListener((obsVal, oldVal, newVal) -> toolStage.setX(menuStage.getX()));
        menuStage.yProperty().addListener((obsVal, oldVal, newVal) -> toolStage.setY(menuStage.getY()+getHeight()));
    }

    private void createToolStage() {
        toolStage = new Stage(StageStyle.TRANSPARENT);
        toolStage.setResizable(false);
        toolStage.initModality(Modality.NONE);
        toolStage.initOwner(preceedingStage);
        toolStage.setWidth(menuStage.getWidth());
        toolStage.setHeight(preceedingStage.getHeight()- menuStage.getHeight()-menuStage.getY()*0.6); // set up to
        // current height reach of the stage

        // Parent
        Pane toolBox = new Pane();
        Rectangle shapeTo_toolBox = new Rectangle(toolStage.getWidth(), toolStage.getHeight());
        shapeTo_toolBox.setArcWidth(16);
        shapeTo_toolBox.setArcHeight(16);
        toolBox.setShape(shapeTo_toolBox);
        toolBox.setBackground(Background.fill(Color.BLACK));
        // Elements placement information
        double xGap = getWidth()*0.1; // 10% of menuStage's width
        double yGap = getHeight()*0.1; // 10% of menuStage's height

        // For renaming the note name, use TextField
        TextField renameField = new TextField();

        Button clear = new Button();
        clear.setTranslateY(yGap);
        clear.setText("Clear");
        clear.setTranslateX(4.*xGap);
        clear.setStyle("-fx-background-color: #1E1E1E; -fx-font-family: Consolas; -fx-text-fill: white; -fx-padding: " +
                "8px 12px");
        clear.setOnAction(mouseEvent -> {
            // clear the Text Area
            Entry.jotArea.clear();
        });

        renameField.setPromptText("Note without name!!");
        renameField.setTranslateY(2.5*yGap);
        renameField.setTranslateX(xGap*1.5);
        renameField.setStyle("-fx-background-color: #1E1E1E; -fx-text-fill: white; -fx-prompt-text-fill: white;");
        renameField.focusedProperty().addListener((obsVal, oldVal, newVal) -> {
            if(newVal)
                renameField.setStyle("-fx-background-color: #1E1E1E; -fx-text-fill: white; -fx-prompt-text-fill: " +
                        "white;");
        });

        toolBox.getChildren().addAll(clear, renameField);

        Scene scene = new Scene(toolBox);
        scene.setFill(Color.TRANSPARENT);
        toolStage.setScene(scene);

        toolBox.setEffect(new Glow(2.0));
    }

    private void colorUtility(double x, double y) {
        Color color;
        try {
            WritableImage snapshot = parent.snapshot(null, null);
            PixelReader pixelReader = snapshot.getPixelReader();
            color = pixelReader.getColor((int) x, (int) y);
            // set primaryStage's background
            if(preceedingStage.getScene().getRoot() instanceof Pane pane)
                pane.setBackground(Background.fill(color.darker()));
            // Generate strings for colors with line number and make necessary CSS edits
            String line2 = "    -fx-text-fill: rgb(" + color.getRed() * 255 + ", " + color.getGreen() * 255 + ", "
                    + color.getBlue() * 255 + ");";
            String line3 = "    -fx-highlight-fill: rgb(" + color.brighter().getRed() * 255 + ", " + color.
                    brighter().getGreen() * 255 + ", " + color.brighter().getBlue() * 255 + ");";
            String line5 = "    text-area-background: rgb(" + color.darker().getRed() * 255 + ", " + color.darker()
                    .getGreen() * 255 + ", " + color.darker().getBlue() * 255 + ");";
            String line20_26 =
                    "    -fx-background-color: linear-gradient(to bottom, #"+color.darker().toString().substring(2, 8)+", " +
                            "#"+color.toString().substring(2, 8)+");";
            String line32 =
                    "    -fx-background-color: linear-gradient(to bottom, #"+color.darker().darker().toString().substring(2, 8)+", " +
                            "#"+color.toString().substring(2, 8)+");";
            String line38 =
                    "    -fx-background-color: linear-gradient(to right, #"+color.darker().darker().toString().substring(2,
                            8)+", " +
                            "#"+color.toString().substring(2, 8)+");";
            String line45_55 = "    -fx-background-color: #"+color.darker().toString().substring(2, 8)+";";
            fileUtility(new String[]{line2, line3, line5, line20_26, line20_26, line32, line38, line45_55,
                            line45_55}
                    , 2, 3, 5, 20, 26, 32, 38, 45, 55);
        } catch (Exception ignored) {}
    }

    private void fileUtility(String[] newLine, int ...lineNumber) {
        // Since TextArea cannot override the external application of CSS, the CSS must be edited and added everytime
        // to the TextArea.
        try (FileWriter fileWriter = new FileWriter(JotArea.filePath.substring(6))) {
            // Write all lines
            List<String> lines = JotArea.getDefaultCssStrings();
            int lN_idx = 0;
            for(int i=1;i<=lines.size();i++)
                if (lN_idx < lineNumber.length && i == lineNumber[lN_idx])
                    fileWriter.write(newLine[lN_idx++] + System.lineSeparator());
                else
                    fileWriter.write(lines.get(i - 1) + System.lineSeparator());
        }catch (Exception ignored) {}
        finally {
            // clear the stylesheets
            Entry.jotArea.getStylesheets().clear();
            // Add fresh stylesheet
            Entry.jotArea.getStylesheets().add(JotArea.filePath);
        }
    }
}
