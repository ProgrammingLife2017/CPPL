package screens.scenes;

import javafx.scene.layout.VBox;
import services.ServiceLocator;

/**
 * Implementation of container that holds other scenes on the sides of the main pane.
 */
public class InteractionScene extends VBox {
    /**
     * Controller object for interacting with the graph.
     */
    private Controller controller;

    /**
     * Screen to display graph information.
     */
    private InfoScreen infoScreen;

    /**
     * The maximum width of this container.
     */
    private static final double MAXIMUM_WIDTH = 175d;

    /**
     * Constructor.
     * @param serviceLocator ServiceLocator for locating services registered in that object.
     */
    public InteractionScene(ServiceLocator serviceLocator) {
        infoScreen = new InfoScreen(serviceLocator);
        controller = new Controller(serviceLocator);
        setSettings();
    }

    /**
     * Register a reference of this object in the service locator.
     * @param sL container of references to other services
     */
    public static void register(ServiceLocator sL) {
        if (sL == null) {
            throw new IllegalArgumentException("The service locator can not be null");
        }
        sL.setInteractionScene(new InteractionScene(sL));
    }

    /**
     * Container settings.
     */
    private void setSettings() {
        this.setMaxWidth(MAXIMUM_WIDTH);
        this.getChildren().addAll(controller, infoScreen);
        this.getStyleClass().add("vbox");
    }

}
