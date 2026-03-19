package view;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import util.BaseView;
import util.StyleUtil;

public class StaffDashboardView extends BaseView {

    // Encapsulation: private fields
    private TextField     searchField;
    private Button        searchBtn, logoutBtn;
    private TableView<String[]> memberTable;
    private Button        editBtn, renewBtn;

    // Edit form fields
    private TextField editNameField, editContactField, editEmailField;
    private Label     editMemberIdLbl;
    private Button    saveEditBtn, cancelEditBtn;
    private VBox      editForm;

    public StaffDashboardView() {
        buildUI();
    }

    @Override
    public void buildUI() {
        this.setStyle("-fx-background-color: " + LIGHT_BG + ";");
        this.setTop(buildTopBar());
        this.setCenter(buildMainContent());
    }

    // Top bar with logo + staff name + logout
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:white;-fx-border-color:#eee;-fx-border-width:0 0 1 0;");

        Label logo = new Label("🏋 GymConnect");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logo.setTextFill(Color.web(ORANGE));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label staffLbl = new Label("Staff Dashboard");
        staffLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        logoutBtn = new Button("Logout");
        logoutBtn.setStyle(StyleUtil.BTN_WHITE);

        bar.getChildren().addAll(logo, sp, staffLbl, new Label("   "), logoutBtn);
        return bar;
    }

    // Main content: search + table + edit form
    @SuppressWarnings("unchecked")
    private VBox buildMainContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        // Title
        Label title = new Label("Member Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label sub = new Label("Search, view, update, and renew memberships.");
        sub.setFont(Font.font("Arial", 13)); sub.setTextFill(Color.web(GRAY_TEXT));

        // Search bar (staff can search members)
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(12));
        searchBar.setStyle("-fx-background-color:white;-fx-background-radius:8;");
        searchField = new TextField();
        searchField.setPromptText("🔍  Search by name or contact number...");
        searchField.setPrefWidth(350); searchField.setStyle(StyleUtil.FIELD);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBtn = new Button("Search"); searchBtn.setStyle(StyleUtil.BTN_ORANGE);
        Button refreshBtn = new Button("↺ All Members"); refreshBtn.setStyle(StyleUtil.BTN_WHITE);
        // Refresh shows all members
        refreshBtn.setOnAction(e -> { searchField.clear(); searchBtn.fire(); });
        searchBar.getChildren().addAll(searchField, searchBtn, refreshBtn);

        // Member table (staff: no delete column, has renew)
        memberTable = new TableView<>();
        memberTable.setStyle("-fx-background-color:white;");
        memberTable.setPrefHeight(340);
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] colNames   = {"Name", "Contact", "Membership", "Join Date", "Expiry", "Status"};
        int[]    colIndexes = {1,       2,          4,            5,           6,         7};

        for (int i = 0; i < colNames.length; i++) {
            final int idx = colIndexes[i];
            TableColumn<String[], String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue() != null && p.getValue().length > idx ? p.getValue()[idx] : ""
            ));
            if ("Status".equals(colNames[i])) {
                col.setCellFactory(tc -> new TableCell<>() {
                    @Override protected void updateItem(String val, boolean empty) {
                        super.updateItem(val, empty);
                        if (empty || val == null) { setGraphic(null); return; }
                        Label badge = new Label(val);
                        badge.setStyle("Active".equals(val) ? StyleUtil.BADGE_ACTIVE : StyleUtil.BADGE_EXPIRED);
                        setGraphic(badge);
                    }
                });
            }
            memberTable.getColumns().add(col);
        }

        // Actions column: Edit + Renew (no Delete for staff)
        TableColumn<String[], Void> actCol = new TableColumn<>("Actions");
        actCol.setCellFactory(tc -> new TableCell<>() {
            private final Button edit  = new Button("✏ Edit");
            private final Button renew = new Button("🔄 Renew");
            {
                edit.setStyle(StyleUtil.BTN_WHITE + "-fx-font-size:11;");
                renew.setStyle(StyleUtil.BTN_ORANGE + "-fx-font-size:11;");
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                HBox box = new HBox(6, edit, renew);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
                edit.setOnAction(e -> {
                    memberTable.getSelectionModel().select(getIndex());
                    if (editBtn != null) editBtn.fire();
                });
                renew.setOnAction(e -> {
                    memberTable.getSelectionModel().select(getIndex());
                    if (renewBtn != null) renewBtn.fire();
                });
            }
        });
        memberTable.getColumns().add(actCol);

        // Hidden proxy buttons used by controller
        editBtn  = new Button(); editBtn.setVisible(false);
        renewBtn = new Button(); renewBtn.setVisible(false);

        // Edit form (hidden until row is selected + edit clicked)
        editForm = buildEditForm();
        editForm.setVisible(false); editForm.setManaged(false);

        // Note: staff cannot delete members
        Label note = new Label("ℹ  Staff: You can search, view, update, and renew memberships. Delete is admin-only.");
        note.setFont(Font.font("Arial", 11));
        note.setTextFill(Color.web(GRAY_TEXT));
        note.setStyle("-fx-background-color:#E3F2FD;-fx-background-radius:4;-fx-padding:6 10;");

        content.getChildren().addAll(title, sub, note, searchBar, memberTable, editBtn, renewBtn, editForm);
        return content;
    }

    // Edit member form (staff can edit name, contact, email)
    private VBox buildEditForm() {
        VBox form = new VBox(10);
        form.setStyle(StyleUtil.CARD); form.setPadding(new Insets(16));
        Label formLbl = new Label("Update Member Info");
        formLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        editMemberIdLbl = new Label();
        editNameField    = new TextField(); editNameField.setPromptText("Full Name");
        editContactField = new TextField(); editContactField.setPromptText("+977 9841234567");
        editEmailField   = new TextField(); editEmailField.setPromptText("Email");

        TextField[] fields = {editNameField, editContactField, editEmailField};
        for (TextField f : fields) { f.setStyle(StyleUtil.FIELD); f.setPrefHeight(34); }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.addRow(0, new Label("Full Name:"), editNameField,    new Label("Contact:"), editContactField);
        grid.addRow(1, new Label("Email:"),     editEmailField);
        ColumnConstraints cc = new ColumnConstraints(); cc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), cc, new ColumnConstraints(), cc);

        saveEditBtn   = new Button("Save Changes"); saveEditBtn.setStyle(StyleUtil.BTN_ORANGE);
        cancelEditBtn = new Button("Cancel");       cancelEditBtn.setStyle(StyleUtil.BTN_WHITE);
        HBox btnRow = new HBox(10, saveEditBtn, cancelEditBtn);

        form.getChildren().addAll(formLbl, editMemberIdLbl, grid, btnRow);
        return form;
    }

    // Getters (encapsulation)
    public TextField getSearchField()            { return searchField; }
    public Button    getSearchBtn()              { return searchBtn; }
    public Button    getLogoutBtn()              { return logoutBtn; }
    public TableView<String[]> getMemberTable()  { return memberTable; }
    public Button    getEditBtn()                { return editBtn; }
    public Button    getRenewBtn()               { return renewBtn; }

    public TextField getEditNameField()    { return editNameField; }
    public TextField getEditContactField() { return editContactField; }
    public TextField getEditEmailField()   { return editEmailField; }
    public Label     getEditMemberIdLbl()  { return editMemberIdLbl; }
    public Button    getSaveEditBtn()      { return saveEditBtn; }
    public Button    getCancelEditBtn()    { return cancelEditBtn; }

    // Show/hide edit form
    public void showEditForm(boolean show) {
        editForm.setVisible(show); editForm.setManaged(show);
    }
}
