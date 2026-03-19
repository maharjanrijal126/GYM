package util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Utility: apply full laptop screen size to any Stage + Scene
public class WindowUtil {

    // Fills the stage to the full usable screen area (respects taskbar)
    public static void applyFullScreen(Stage stage, Scene scene, String title) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        stage.setTitle(title);
        stage.setScene(scene);

        // Position at screen origin and size to full screen
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
    }
}
