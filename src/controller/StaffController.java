package controller;

import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.DBConnection;
import util.WindowUtil;
import view.LoginView;
import view.StaffDashboardView;

import java.util.List;

public class StaffController {

    private final StaffDashboardView view;
    private final Stage              stage;
    private final DBConnection       db;

    // Constructor: wire events and load initial data
    public StaffController(StaffDashboardView view, Stage stage, DBConnection db) {
        this.view  = view;
        this.stage = stage;
        this.db    = db;
        init();
        loadMembers(); // load all members on open
    }

    private void init() {
        view.getSearchBtn().setOnAction(e -> searchMembers());
        view.getEditBtn().setOnAction(e   -> openEditForm());
        view.getRenewBtn().setOnAction(e  -> renewMembership());
        view.getSaveEditBtn().setOnAction(e   -> saveEdit());
        view.getCancelEditBtn().setOnAction(e -> { view.showEditForm(false); });
        view.getLogoutBtn().setOnAction(e -> logout());
    }

    // Load all members into table (READ)
    private void loadMembers() {
        List<String[]> data = db.getAllMembers();
        view.getMemberTable().setItems(FXCollections.observableArrayList(data));
    }

    // Search members by keyword (READ with filter)
    private void searchMembers() {
        String keyword = view.getSearchField().getText().trim();
        List<String[]> results = keyword.isEmpty() ? db.getAllMembers() : db.searchMembers(keyword);
        view.getMemberTable().setItems(FXCollections.observableArrayList(results));
    }

    // Open edit form with selected row data
    private void openEditForm() {
        String[] selected = view.getMemberTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Member", "Please select a member to edit.", Alert.AlertType.WARNING);
            return;
        }
        view.getEditMemberIdLbl().setText("Editing Member ID: " + selected[0]);
        view.getEditNameField().setText(selected[1]);
        view.getEditContactField().setText(selected[2]);
        view.getEditEmailField().setText(selected[3]);
        view.showEditForm(true);
    }

    // Save updated member info (UPDATE from CRUD)
    private void saveEdit() {
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
            showAlert("Updated", "Member info updated successfully.", Alert.AlertType.INFORMATION);
            view.showEditForm(false);
            loadMembers();
        } else {
            showAlert("Error", "Update failed. Try again.", Alert.AlertType.ERROR);
        }
    }

    // Renew selected member's membership (UPDATE)
    private void renewMembership() {
        String[] selected = view.getMemberTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select Member", "Please select a member to renew.", Alert.AlertType.WARNING);
            return;
        }
        int renewId;
        try { renewId = Integer.parseInt(selected[0]); }
        catch (NumberFormatException e) {
            showAlert("Error", "Invalid member ID.", Alert.AlertType.ERROR); return;
        }
        boolean ok = db.renewMembership(renewId, 1);
        if (ok) showAlert("Renewed", "Membership renewed by 1 month.", Alert.AlertType.INFORMATION);
        else     showAlert("Error",   "Renewal failed.",                Alert.AlertType.ERROR);
        loadMembers();
    }

    // Logout back to login screen
    private void logout() {
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage, db);
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        WindowUtil.applyFullScreen(stage, new Scene(loginView, screen.getWidth(), screen.getHeight()), "GymConnect Login");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}
