package controller;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.DBConnection;
import util.WindowUtil;
import view.*;

public class LoginController {

    private final LoginView    view;
    private final Stage        stage;
    private final DBConnection db;

    // Constructor: wire view, stage, and DB — test connection immediately
    public LoginController(LoginView view, Stage stage) {
        this.view  = view;
        this.stage = stage;
        this.db    = new DBConnection();
        init();
        checkDBConnection(); // warn user right away if DB is not reachable
    }

    // Constructor used by other controllers passing existing DB instance
    public LoginController(LoginView view, Stage stage, DBConnection db) {
        this.view  = view;
        this.stage = stage;
        this.db    = db;
        init();
    }

    private void init() {
        view.getLoginBtn().setOnAction(e -> login());
        view.getRegisterBtn().setOnAction(e -> openRegister());
    }

    // Warn user if DB is not reachable
    private void checkDBConnection() {
        if (!db.testConnection()) {
            view.setMessage("⚠  Cannot connect to database. Check MySQL is running.", false);
        }
    }

    // Method overloading: no-arg reads from view
    private void login() {
        login(view.getUsername(), view.getPassword());
    }

    // Overloaded: validates then routes by role
    private void login(String username, String password) {

        // ── Validation ──────────────────────────────────────────────────
        if (username.isEmpty()) {
            view.setMessage("Username cannot be empty.", false);
            return;
        }
        if (password.isEmpty()) {
            view.setMessage("Password cannot be empty.", false);
            return;
        }
        if (username.length() < 3) {
            view.setMessage("Username must be at least 3 characters.", false);
            return;
        }

        // ── DB login check ───────────────────────────────────────────────
        view.setMessage("Signing in...", true);
        String role = db.validateLogin(username, password);

        if (role == null) {
            view.setMessage("Invalid username or password. Please try again.", false);
            return;
        }

        // ── Route by role ────────────────────────────────────────────────
        int userId = db.getUserId(username);
        switch (role) {
            case "admin"  -> openAdminDashboard();
            case "staff"  -> openStaffDashboard();
            case "member" -> openMemberDashboard(userId, username);
            default       -> view.setMessage("Unknown role. Contact administrator.", false);
        }
    }

    // Open registration screen
    private void openRegister() {
        RegisterView regView = new RegisterView();
        new RegisterController(regView, stage, db);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(regView, screen.getWidth(), screen.getHeight()),
            "GymConnect - Create Account");
    }

    // Open admin dashboard
    private void openAdminDashboard() {
        AdminDashboardView adminView = new AdminDashboardView();
        new AdminController(adminView, stage, db);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(adminView, screen.getWidth(), screen.getHeight()),
            "GymConnect - Admin Dashboard");
    }

    // Open staff dashboard
    private void openStaffDashboard() {
        StaffDashboardView staffView = new StaffDashboardView();
        new StaffController(staffView, stage, db);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(staffView, screen.getWidth(), screen.getHeight()),
            "GymConnect - Staff Dashboard");
    }

    // Open member dashboard
    private void openMemberDashboard(int userId, String username) {
        MemberDashboardView memberView = new MemberDashboardView();
        new MemberController(memberView, stage, db, userId, username);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(memberView, screen.getWidth(), screen.getHeight()),
            "GymConnect - Member Dashboard");
    }
}
