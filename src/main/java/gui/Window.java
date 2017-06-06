package gui;

import datastructure.DrawNode;
import datastructure.NodeGraph;
import filesystem.FileSystem;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logging.Logger;
import logging.LoggerFactory;
import parsing.Parser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Main application.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Window extends Application {
    /**
     * Logger that keeps track of actions executed by this class.
     */
    private static Logger logger;

    /**
     * Factory that creates all loggers.
     */
    private static LoggerFactory loggerFactory;

    /**
     * Backlog window to print all actions.
     */
    private static Backlog backLog;

    /**
     * Pane used for displaying graphs.
     */
    private static GraphScene graphScene;

    /**
     * Window to print information of nodes or edges.
     */
    private static InfoScreen infoScreen;

    /**
     * Controller to initiate center queries.
     */
    private Controller controller;

    /**
     * The load bar which shows how far the parser is.
     */
    private static ProgressBar pB;

    /**
     * A rectangle that shows where the user is in the the graph.
     */
    private static Rectangle indicator;

    /**
     * Factor for creating javaFX components.
     */
    private FXElementsFactory fxElementsFactory;

    /**
     * Minimum width of the application window.
     */
    private static double MIN_WIDTH = 1200d;

    /**
     * Minimum height of the application window.
     */
    private static double MIN_HEIGHT = 700d;

    /**
     * Starts the frame.
     * @param stage Main stage where the content is placed.
     * @throws Exception Thrown when application can't be started.
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.setupService();
        Pane mainPane = createMainPane(stage);
        setScrolling(mainPane);
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("layoutstyles.css");
        stage.setScene(scene);
        setStageSettings(stage);
        stage.show();
        logger.info("the main application has started");
    }

    /**
     * Sets up the necessary services.
     */
    private void setupService() {
        backLog = new Backlog();
        loggerFactory = new LoggerFactory(new FileSystem());
        logger = loggerFactory.createLogger(this.getClass());
        fxElementsFactory = new FXElementsFactory();
        graphScene = new GraphScene(fxElementsFactory);
        graphScene.toBack();
        infoScreen = new InfoScreen(fxElementsFactory);
        controller = new Controller(fxElementsFactory, graphScene);
    }

    /**
     * Creates the main pane for the application window where all content is placed.
     * @param stage Stage object that is the container of all content.
     * @return Pane object.
     */
    private BorderPane createMainPane(Stage stage) {
        BorderPane pane = new BorderPane();
        pane.setTop(createMenuBar(stage));
        pane.setCenter(graphScene);
        pane.setLeft(createSidePane(controller, infoScreen));
        pane.setBottom(createProgressBar());
        Rectangle indicatorBar = new Rectangle();
        indicator = new Rectangle();
        pane.getChildren().add(indicatorBar);
        pane.getChildren().add(indicator);
        indicatorBar.setWidth(pane.getWidth() - 20);
        indicatorBar.setX(10);
        indicatorBar.setY(pane.getHeight() - 25);
        indicatorBar.setHeight(10);
        indicatorBar.setFill(Color.GRAY);
        return pane;
    }

    /**
     * Creates the pane that's on the left side of the main window.
     * @param info InfoScreen object.
     * @param control Controller object.
     * @return Pane object.
     */
    private Pane createSidePane(Pane info, Pane control) {
        VBox box = new VBox();
        box.setMaxWidth(175);
        box.getChildren().addAll(info, control);
        box.getStyleClass().add("vbox");
        return box;
    }

    /**
     * Creates a progress bar.
     * @return ProgressBar object.
     */
    private ProgressBar createProgressBar() {
        pB = new ProgressBar();
        pB.setVisible(false);
        pB.setMaxWidth(1212);
        pB.setPrefHeight(30.0);
        pB.setMinHeight(10.0);
        pB.setProgress(0.0);
        return pB;
    }

    /**
     * Initiate settings for the main stage.
     * @param stage
     */
    private void setStageSettings(Stage stage) {
        stage.setTitle("Main window");
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.setOnCloseRequest(event -> {
            try {
                Window.loggerFactory.getFileSystem().closeWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the backlog of this class.
     * @return BackLog object.
     */
    public static Backlog getBackLog() {
        if (backLog == null) {
            return new Backlog();
        }
        return backLog;
    }

    /**
     * Creates instance of InfoScreen.
     * @return InfoScreen object.
     */
    public static InfoScreen getInfoScreen() {
        return infoScreen;
    }


    /**
     * Creates the menu bar with its items.
     * @param stage Main stage.
     * @return The menu bar object.
     */
    private MenuBar createMenuBar(final Stage stage) {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(addFileSelector(stage));
        menuBar.getMenus().add(addController());
        menuBar.getMenus().add(addClear());
        return menuBar;
    }

    /**
     * Sets a scroll event to the pane that handles the zooming of the graph.
     */
    private void setScrolling(Pane pane) {
        pane.setOnScroll((ScrollEvent event) -> {
            if (NodeGraph.getCurrentInstance() != null) {
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    graphScene.zoomOut(event.getX(), event.getY());
                } else {
                    graphScene.zoomIn(event.getX(), event.getY());
                }
                graphScene.toBack();
            }
        });
    }

    /**
     * Creates a menu for navigating through the file directory.
     * @param stage The container for these GUI nodes.
     * @return Menu object.
     */
    private Menu addFileSelector(Stage stage) {
        Menu menu = new Menu("File");
        MenuItem item = new MenuItem("New file");
        item.setOnAction(
                event -> {
                    File file = FileSelector.showOpenDialog(stage);
                    if (file != null && file.exists()) {
                        pB.setVisible(true);
                        NodeGraph.setCurrentInstance(Parser.getInstance().parse(file));

                        new Thread() {
                            public void run () {
                                try {
                                    Parser.getThread().join();

                                    this.join(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                pB.setVisible(false);
                                pB.setProgress(0.0);
                            }
                        }.start();

                        Thread drawing = graphScene.drawGraph(0, 200);

                        new Thread(() -> {
                            try {
                                drawing.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            graphScene.setTranslateX(-NodeGraph.getCurrentInstance().getDrawNodes().getLast().getX());
                            graphScene.setScaleX(graphScene.getWidth() / (NodeGraph.getCurrentInstance().getDrawNodes().getFirst().getBoundsInLocal().getMaxX() - NodeGraph.getCurrentInstance().getDrawNodes().getLast().getX()));
                            LinkedList<DrawNode> drawNodes = NodeGraph.getCurrentInstance().getDrawNodes();
                            graphScene.setTranslateX((-drawNodes.getLast().getX() + graphScene.getWidth() / 2) * graphScene.getScaleX() - graphScene.getWidth() / 2);
                            logger.info("file has been selected");
                        }).start();
                    }
                }
        );
        menu.getItems().add(item);
        return menu;
    }

    /**
     * Creates a menu that allows interaction with the graph.
     * @return Menu object.
     */
    private Menu addController() {
        Menu menu = new Menu("Tools");
        MenuItem item2 = new MenuItem("Console log");
        item2.setOnAction(
                event -> {
                    getBackLog().show();
                    logger.info("console window has been opened");
                }
        );
        menu.getItems().addAll(item2);
        return menu;
    }

    /**
     * Adds a menu that clears the info screen and returns the graph to the original view.
     * @return Menu object.
     */
    private Menu addClear() {
        Menu menu = new Menu("Reset");
        MenuItem item = new MenuItem("Graph");
        item.setOnAction(
                event -> {
                    if (NodeGraph.getCurrentInstance() != null) {
                        graphScene.drawGraph(0, 200);
                        logger.info("drawing returned to original");
                    } else {
                        errorPopup("Please load a graph.");
                    }
                }
        );
        menu.getItems().addAll(item);
        return menu;
    }

    /**
     * Creates a popup containing an error message if the user gives invalid input.
     * @param message The error message.
     */
    public static void errorPopup(String message) {
        Stage newStage = new Stage();
        Label label = new Label(message);
        Group group = new Group();
        group.getChildren().add(label);
        newStage.setWidth(label.getWidth());
        newStage.setResizable(false);
        newStage.setTitle("Error");
        newStage.initStyle(StageStyle.UTILITY);
        newStage.setAlwaysOnTop(true);
        Scene scene = new Scene(group, label.getMaxWidth(), Math.max(label.getMaxHeight(), 40));
        newStage.centerOnScreen();
        newStage.setScene(scene);
        newStage.show();
    }

    /**
     * Sets the progress of the ProgressBar to the given value.
     * @param progress the new progress to be shown.
     */
    public static void setProgress(double progress) {
        pB.setProgress(progress);
    }

    /**
     * The initialization of the game.
     * @param args the arguments to run.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
