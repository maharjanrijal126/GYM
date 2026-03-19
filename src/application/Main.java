package application;

import controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Get the full usable screen size (excludes taskbar)
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        LoginView view = new LoginView();
        new LoginController(view, stage);

        // Scene fills the entire screen size exactly
        Scene scene = new Scene(view, screen.getWidth(), screen.getHeight());
        stage.setTitle("GymConnect Login");
        stage.setScene(scene);

        // Position window at top-left corner of screen
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());

        // Set window to full screen size — no min size restriction
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
