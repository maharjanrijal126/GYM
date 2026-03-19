package controller;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.DBConnection;
import util.WindowUtil;
import view.LoginView;
import view.MemberDashboardView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MemberController {

    private final MemberDashboardView view;
    private final Stage               stage;
    private final DBConnection        db;
    private final int                 userId;
    private final String              username;

    // Constructor: load member data from DB into view
    public MemberController(MemberDashboardView view, Stage stage,
                            DBConnection db, int userId, String username) {
        this.view     = view;
        this.stage    = stage;
        this.db       = db;
        this.userId   = userId;
        this.username = username;
        init();
        loadMemberData(); // READ from DB and populate all fields
    }

    private void init() {
        view.getLogoutBtn().setOnAction(e -> logout());
        view.getRenewBtn().setOnAction(e  -> showRenewInfo());
    }

    // Load real member data from DB and push to view (READ from CRUD)
    private void loadMemberData() {
        // data = [full_name, contact, email, join_date, plan_name, start_date, end_date, status]
        String[] data = db.getMemberDetails(userId);

        if (data == null) {
            // No membership record yet — show username and blanks
            view.setMemberName(username);
            view.setMemberNo(String.valueOf(userId));
            view.setPlan("No Active Plan");
            view.setStatus("Expired");
            view.setJoinDate("N/A");
            view.setExpiryDate("N/A");
            view.setDaysRemaining("N/A");
            view.setProgress(0);
            return;
        }

        // ── Populate name and member number ──────────────────────────────
        String fullName = data[0] != null ? data[0] : username;
        view.setMemberName(fullName);
        view.setMemberNo(String.valueOf(userId));

        // ── Membership plan and status ────────────────────────────────────
        view.setPlan(data[4] != null ? data[4] : "Basic");
        view.setStatus(data[7] != null ? data[7] : "Expired");

        // ── Dates ─────────────────────────────────────────────────────────
        String joinDate  = data[3] != null ? data[3] : "N/A";
        String startDate = data[5] != null ? data[5] : "N/A";
        String endDate   = data[6] != null ? data[6] : "N/A";

        view.setJoinDate(joinDate);
        view.setExpiryDate(endDate);

        // ── Days remaining + progress bar ─────────────────────────────────
        if (data[5] != null && data[6] != null) {
            try {
                LocalDate start  = LocalDate.parse(data[5]);
                LocalDate end    = LocalDate.parse(data[6]);
                LocalDate today  = LocalDate.now();

                long totalDays   = ChronoUnit.DAYS.between(start, end);
                long daysElapsed = ChronoUnit.DAYS.between(start, today);
                long daysLeft    = ChronoUnit.DAYS.between(today, end);

                // Clamp progress between 0 and 1
                double progress = totalDays > 0
                    ? Math.min(1.0, Math.max(0.0, (double) daysElapsed / totalDays))
                    : 0;

                view.setProgress(progress);

                if (daysLeft >= 0) {
                    view.setDaysRemaining(daysLeft + " Days");
                } else {
                    view.setDaysRemaining("Expired");
                    view.setStatus("Expired");
                }

            } catch (Exception e) {
                view.setDaysRemaining("N/A");
                view.setProgress(0);
            }
        } else {
            view.setDaysRemaining("N/A");
            view.setProgress(0);
        }
    }

    // Renew info popup — actual renewal done by staff or admin
    private void showRenewInfo() {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
            "To renew your membership, please visit the front desk or contact staff.",
            ButtonType.OK);
        a.setTitle("Renew Membership");
        a.setHeaderText(null);
        a.showAndWait();
    }

    // Logout back to login screen
    private void logout() {
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage, db);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage,
            new Scene(loginView, screen.getWidth(), screen.getHeight()),
            "GymConnect Login");
    }
}
