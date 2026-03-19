package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.DBConnection;
import util.WindowUtil;
import view.AdminDashboardView;
import view.LoginView;

import java.util.List;
import java.util.Optional;

public class AdminController {

    private final AdminDashboardView view;
    private final Stage              stage;
    private final DBConnection       db;

    // Constructor: wire all events
    public AdminController(AdminDashboardView view, Stage stage, DBConnection db) {
        this.view  = view;
        this.stage = stage;
        this.db    = db;
        init();
        loadMembers();    // load members on startup
        loadDashStats();  // update stat cards
    }

    private void init() {
        // Nav buttons
        view.getDashboardNavBtn().setOnAction(e -> view.showPanel("dashboard"));
        view.getMembersNavBtn().setOnAction(e   -> { view.showPanel("members"); loadMembers(); });
        view.getStaffNavBtn().setOnAction(e     -> { view.showPanel("staff");   loadStaff(); });

        // Member actions
        view.getAddMemberBtn().setOnAction(e    -> view.showAddMemberForm(true));
        view.getCancelMemberBtn().setOnAction(e -> view.showAddMemberForm(false));
        view.getSaveNewMemberBtn().setOnAction(e -> saveNewMember());

        view.getSearchBtn().setOnAction(e  -> searchMembers());
        view.getRefreshBtn().setOnAction(e -> loadMembers());

        // Edit / Delete
        view.getEditMemberBtn().setOnAction(e   -> openEditMemberForm());
        view.getDeleteMemberBtn().setOnAction(e -> deleteMember());
        view.getSaveEditBtn().setOnAction(e     -> saveEditMember());
        view.getCancelEditBtn().setOnAction(e   -> view.showEditMemberForm(false));
        view.getRenewBtn().setOnAction(e        -> renewMembership());

        // Staff actions
        view.getAddStaffBtn().setOnAction(e    -> view.showAddStaffForm(true));
        view.getCancelStaffBtn().setOnAction(e -> view.showAddStaffForm(false));
        view.getSaveStaffBtn().setOnAction(e   -> saveNewStaff());
        view.getDeleteStaffBtn().setOnAction(e -> deleteStaff());

        // Logout
        view.getLogoutBtn().setOnAction(e -> logout());
    }

    // ── Dashboard Stats ───────────────────────────────────────────────────────
    private void loadDashStats() {
        int[] stats = db.getDashboardStats();
        view.getTotalMembersLbl().setText(String.valueOf(stats[0]));
        view.getActiveMembersLbl().setText(String.valueOf(stats[1]));
        view.getExpiredMembersLbl().setText(String.valueOf(stats[2]));
    }

    // ── Load Members (READ) ───────────────────────────────────────────────────
    private void loadMembers() {
        List<String[]> data = db.getAllMembers();
        ObservableList<String[]> items = FXCollections.observableArrayList(data);
        view.getMemberTable().setItems(items);
    }

    // ── Search Members ────────────────────────────────────────────────────────
    private void searchMembers() {
        String keyword = view.getSearchField().getText().trim();
        if (keyword.isEmpty()) { loadMembers(); return; }
        List<String[]> results = db.searchMembers(keyword);
        view.getMemberTable().setItems(FXCollections.observableArrayList(results));
    }

    // ── Add New Member (CREATE) ───────────────────────────────────────────────
    private void saveNewMember() {
        String username  = view.getMUsernameField().getText().trim();
        String password  = view.getMPasswordField().getText().trim();
        String fullName  = view.getMNameField().getText().trim();
        String contact   = view.getMContactField().getText().trim();
        String email     = view.getMEmailField().getText().trim();
        String joinDate  = view.getMJoinDateField().getText().trim();
        String typeStr   = view.getMTypeCombo().getValue();

        // ── Validation ────────────────────────────────────────────────
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
            contact.isEmpty()  || email.isEmpty()    || joinDate.isEmpty() || typeStr == null) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
            return;
        }
        if (username.length() < 3) {
            showAlert("Validation Error", "Username must be at least 3 characters.", Alert.AlertType.WARNING);
            return;
        }
        if (password.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters.", Alert.AlertType.WARNING);
            return;
        }
        if (!email.contains("@")) {
            showAlert("Validation Error", "Please enter a valid email address.", Alert.AlertType.WARNING);
            return;
        }
        if (!joinDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showAlert("Validation Error", "Join Date must be in format YYYY-MM-DD.", Alert.AlertType.WARNING);
            return;
        }
        if (db.usernameExists(username)) {
            showAlert("Validation Error", "Username already exists. Choose a different one.", Alert.AlertType.WARNING);
            return;
        }

        int typeId = Integer.parseInt(typeStr.split(" - ")[0]);
        boolean ok = db.addMember(username, password, fullName, contact, email, joinDate, typeId);

        if (ok) {
            showAlert("Success", "Member added successfully!", Alert.AlertType.INFORMATION);
            clearMemberForm();
            view.showAddMemberForm(false);
            loadMembers(); loadDashStats();
        } else {
            showAlert("Error", "Failed to add member. Try again.", Alert.AlertType.ERROR);
        }
    }

    // ── Open Edit Form (READ selected row) ───────────────────────────────────
    private void openEditMemberForm() {
        String[] selected = view.getMemberTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Member", "Please select a member to edit.", Alert.AlertType.WARNING);
            return;
        }
        view.getEditMemberIdLbl().setText("Editing Member ID: " + selected[0]);
        view.getEditNameField().setText(selected[1]);
        view.getEditContactField().setText(selected[2]);
        view.getEditEmailField().setText(selected[3]);
        view.showEditMemberForm(true);
    }

    // ── Save Edit (UPDATE) ────────────────────────────────────────────────────
    private void saveEditMember() {
        String idText = view.getEditMemberIdLbl().getText().replace("Editing Member ID: ", "").trim();
        if (idText.isEmpty()) {
            showAlert("Error", "No member selected for editing.", Alert.AlertType.WARNING);
            return;
        }
        int memberId;
        try { memberId = Integer.parseInt(idText); }
        catch (NumberFormatException e) {
            showAlert("Error", "Invalid member ID. Please re-select the member.", Alert.AlertType.ERROR);
            return;
        }
        String name    = view.getEditNameField().getText().trim();
        String contact = view.getEditContactField().getText().trim();
        String email   = view.getEditEmailField().getText().trim();

        // Validation
        if (name.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
            return;
        }
        if (!email.contains("@")) {
            showAlert("Validation Error", "Enter a valid email address.", Alert.AlertType.WARNING);
            return;
        }

        boolean ok = db.updateMember(memberId, name, contact, email);
        if (ok) {
            showAlert("Success", "Member updated successfully!", Alert.AlertType.INFORMATION);
            view.showEditMemberForm(false);
            loadMembers();
        } else {
            showAlert("Error", "Update failed. Try again.", Alert.AlertType.ERROR);
        }
    }

    // ── Delete Member (DELETE) ────────────────────────────────────────────────
    private void deleteMember() {
        String[] selected = view.getMemberTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Member", "Please select a member to delete.", Alert.AlertType.WARNING);
            return;
        }
        // Confirm before delete
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete member: " + selected[1] + "? This cannot be undone.", ButtonType.YES, ButtonType.NO)
            .showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            int id = safeParseId(selected[0]);
            if (id == -1) { showAlert("Error", "Invalid member ID.", Alert.AlertType.ERROR); return; }
            boolean ok = db.deleteMember(id);
            if (ok) {
                showAlert("Deleted", "Member deleted successfully.", Alert.AlertType.INFORMATION);
                loadMembers(); loadDashStats();
            } else {
                showAlert("Error", "Delete failed. Try again.", Alert.AlertType.ERROR);
            }
        }
    }

    // ── Renew Membership (UPDATE) ─────────────────────────────────────────────
    private void renewMembership() {
        String[] selected = view.getMemberTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Member", "Please select a member to renew.", Alert.AlertType.WARNING);
            return;
        }
        int renewId = safeParseId(selected[0]);
        if (renewId == -1) { showAlert("Error", "Invalid member ID.", Alert.AlertType.ERROR); return; }
        boolean ok = db.renewMembership(renewId, 1);
        if (ok) showAlert("Renewed", "Membership renewed by 1 month.", Alert.AlertType.INFORMATION);
        else     showAlert("Error", "Renewal failed.", Alert.AlertType.ERROR);
        loadMembers();
    }

    // ── Load Staff (READ) ─────────────────────────────────────────────────────
    private void loadStaff() {
        List<String[]> data = db.getAllStaff();
        view.getStaffTable().setItems(FXCollections.observableArrayList(data));
    }

    // ── Add New Staff (CREATE) ────────────────────────────────────────────────
    private void saveNewStaff() {
        String username = view.getSUsernameField().getText().trim();
        String password = view.getSPasswordField().getText().trim();
        String salaryStr = view.getSSalaryField().getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || salaryStr.isEmpty()) {
            showAlert("Validation Error", "All staff fields are required.", Alert.AlertType.WARNING);
            return;
        }
        if (username.length() < 3) {
            showAlert("Validation Error", "Username must be at least 3 characters.", Alert.AlertType.WARNING);
            return;
        }
        if (password.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters.", Alert.AlertType.WARNING);
            return;
        }
        double salary;
        try { salary = Double.parseDouble(salaryStr); }
        catch (NumberFormatException e) {
            showAlert("Validation Error", "Salary must be a valid number.", Alert.AlertType.WARNING);
            return;
        }
        if (salary < 0) {
            showAlert("Validation Error", "Salary cannot be negative.", Alert.AlertType.WARNING);
            return;
        }
        if (db.usernameExists(username)) {
            showAlert("Validation Error", "Username already taken.", Alert.AlertType.WARNING);
            return;
        }

        boolean ok = db.addStaff(username, password, salary);
        if (ok) {
            showAlert("Success", "Staff added successfully!", Alert.AlertType.INFORMATION);
            view.getSUsernameField().clear(); view.getSPasswordField().clear(); view.getSSalaryField().clear();
            view.showAddStaffForm(false); loadStaff();
        } else {
            showAlert("Error", "Failed to add staff.", Alert.AlertType.ERROR);
        }
    }

    // ── Delete Staff (DELETE) ─────────────────────────────────────────────────
    private void deleteStaff() {
        String[] selected = view.getStaffTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Staff", "Please select a staff to delete.", Alert.AlertType.WARNING);
            return;
        }
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete staff: " + selected[1] + "?", ButtonType.YES, ButtonType.NO)
            .showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            int id = safeParseId(selected[0]);
            if (id == -1) { showAlert("Error", "Invalid staff ID.", Alert.AlertType.ERROR); return; }
            boolean ok = db.deleteStaff(id);
            if (ok) { showAlert("Deleted", "Staff deleted.", Alert.AlertType.INFORMATION); loadStaff(); }
            else      showAlert("Error", "Delete failed.", Alert.AlertType.ERROR);
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    private void logout() {
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage, db); // pass existing db — skip re-connection check
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage, new Scene(loginView, screen.getWidth(), screen.getHeight()), "GymConnect Login");
    }

    // Helper: clear add-member form fields
    private void clearMemberForm() {
        view.getMUsernameField().clear(); view.getMPasswordField().clear();
        view.getMNameField().clear();     view.getMContactField().clear();
        view.getMEmailField().clear();    view.getMJoinDateField().clear();
        view.getMTypeCombo().setValue(null);
    }

    // Helper: show an alert dialog
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Helper: safely parse a DB id string — returns -1 if null or not numeric
    private int safeParseId(String val) {
        if (val == null || val.trim().isEmpty()) return -1;
        try { return Integer.parseInt(val.trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
