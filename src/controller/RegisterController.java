package controller;

import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DBConnection;
import util.WindowUtil;
import view.LoginView;
import view.RegisterView;

import java.time.LocalDate;

public class RegisterController {

    private final RegisterView view;
    private final Stage        stage;
    private final DBConnection db;

    // Constructor: wire view, stage, DB
    public RegisterController(RegisterView view, Stage stage, DBConnection db) {
        this.view  = view;
        this.stage = stage;
        this.db    = db;
        init();
    }

    private void init() {
        view.getRegisterBtn().setOnAction(e -> register());
        // "Sign In" link goes back to login
        view.getBackToLoginLink().setOnMouseClicked(e -> goToLogin());
    }

    // Method overloading: no-arg reads from view
    private void register() {
        register(
            view.getUsername(),
            view.getPassword(),
            view.getConfirmPassword(),
            view.getFullName(),
            view.getContact(),
            view.getEmail()
        );
    }

    // Overloaded: validates all fields then saves to DB
    private void register(String username, String password, String confirmPassword,
                          String fullName, String contact, String email) {

        // ── Empty field check via loop ──────────────────────────────────────
        String[] values = {username, password, confirmPassword, fullName, contact, email};
        String[] labels = {"Username", "Password", "Confirm Password",
                           "Full Name", "Contact Number", "Email"};
        for (int i = 0; i < values.length; i++) {
            if (values[i].isEmpty()) {
                view.setMessage(labels[i] + " cannot be empty.", false);
                return;
            }
        }

        // ── Field-level validations ─────────────────────────────────────────
        if (username.length() < 3) {
            view.setMessage("Username must be at least 3 characters.", false);
            return;
        }
        if (password.length() < 6) {
            view.setMessage("Password must be at least 6 characters.", false);
            return;
        }
        if (!password.equals(confirmPassword)) {
            view.setMessage("Passwords do not match. Please re-enter.", false);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            view.setMessage("Enter a valid email address (e.g. you@mail.com).", false);
            return;
        }
        if (contact.replaceAll("[^0-9]", "").length() < 7) {
            view.setMessage("Contact must contain at least 7 digits.", false);
            return;
        }

        // ── Duplicate username check ────────────────────────────────────────
        if (db.usernameExists(username)) {
            view.setMessage("Username '" + username + "' is already taken. Choose another.", false);
            return;
        }

        // ── Save to DB (CREATE) ─────────────────────────────────────────────
        String joinDate = LocalDate.now().toString(); // YYYY-MM-DD auto-set
        boolean ok = db.registerMember(username, password, fullName, contact, email, joinDate);

        if (ok) {
            // Show success then go to login — user must sign in with their new credentials
            view.setMessage("✔  Account created! Please sign in with your new credentials.", true);
            view.getRegisterBtn().setDisable(true); // prevent double-submit

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> goToLogin());
            pause.play();
        } else {
            view.setMessage("Registration failed. Check your details and try again.", false);
        }
    }

    // Navigate back to login screen and show a success hint
    private void goToLogin() {
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        // Show success message on the login page so user knows to sign in
        loginView.setMessage("✔  Account created! Please sign in with your credentials.", true);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(loginView, screen.getWidth(), screen.getHeight()),
            "GymConnect Login");
    }
}
