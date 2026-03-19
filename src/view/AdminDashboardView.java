package view;

import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import util.BaseView;
import util.StyleUtil;

public class AdminDashboardView extends BaseView {

    // Encapsulation: private UI fields exposed via getters
    private Label  totalMembersLbl, activeMembersLbl, expiredMembersLbl;
    private Button membersNavBtn, staffNavBtn, dashboardNavBtn;
    private Button logoutBtn;

    // Member Management section
    private TableView<String[]> memberTable;
    private TextField           searchField;
    private Button              searchBtn, addMemberBtn, refreshBtn;

    // Member form fields
    private TextField     mUsernameField, mPasswordField, mNameField;
    private TextField     mContactField, mEmailField, mJoinDateField;
    private ComboBox<String> mTypeCombo;
    private Button        saveNewMemberBtn, cancelMemberBtn;

    // Edit/Delete controls
    private Button editMemberBtn, deleteMemberBtn, renewBtn;
    private Label  memberFormTitle;

    // Edit form fields
    private TextField editNameField, editContactField, editEmailField;
    private Label     editMemberIdLbl;
    private Button    saveEditBtn, cancelEditBtn;

    // Staff management fields
    private TableView<String[]> staffTable;
    private TextField sUsernameField, sPasswordField, sSalaryField;
    private Button    addStaffBtn, deleteStaffBtn, saveStaffBtn, cancelStaffBtn;
    private Label     staffFormTitle;

    // Stored form references (safe - no index-based lookup)
    private VBox addMemberFormNode, editMemberFormNode, addStaffFormNode;

    // Main content area (swapped by nav)
    private BorderPane contentArea;
    private VBox       dashboardContent, membersContent, staffContent;

    public AdminDashboardView() {
        buildUI(); // inherited method override
    }

    @Override
    public void buildUI() {
        this.setStyle("-fx-background-color: " + LIGHT_BG + ";");
        this.setLeft(buildSidebar());
        this.setTop(buildTopBar());
        buildAllPanels();
        contentArea = new BorderPane(dashboardContent);
        this.setCenter(contentArea);
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(180);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 1 0 0;");

        // Logo area
        HBox logoBox = new HBox(8);
        logoBox.setPadding(new Insets(18, 16, 18, 16));
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logo = new Label("🏋 GymConnect");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ORANGE));
        logoBox.getChildren().add(logo);

        Separator sep = new Separator();

        // Nav buttons (loop through labels and create buttons)
        String[] navLabels = {"📊  Dashboard", "👥  Members", "👤  Staff", "⚙  Settings"};
        dashboardNavBtn = new Button(navLabels[0]);
        membersNavBtn   = new Button(navLabels[1]);
        staffNavBtn     = new Button(navLabels[2]);
        Button settingsBtn = new Button(navLabels[3]);

        Button[] navBtns = {dashboardNavBtn, membersNavBtn, staffNavBtn, settingsBtn};
        for (Button btn : navBtns) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle(StyleUtil.NAV_BTN);
        }
        dashboardNavBtn.setStyle(StyleUtil.NAV_BTN_ACTIVE); // default active

        // Spacer to push logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Admin info + logout at bottom
        VBox bottomBox = new VBox(8);
        bottomBox.setPadding(new Insets(12));
        Label adminLbl = new Label("👤 Admin Panel");
        adminLbl.setFont(Font.font("Arial", 12));
        adminLbl.setTextFill(Color.web(GRAY_TEXT));
        logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(StyleUtil.BTN_WHITE);
        bottomBox.getChildren().addAll(adminLbl, logoutBtn);

        sidebar.getChildren().addAll(logoBox, sep, dashboardNavBtn, membersNavBtn, staffNavBtn, settingsBtn, spacer, bottomBox);
        return sidebar;
    }

    // ── Top Bar ──────────────────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        TextField search = new TextField();
        search.setPromptText("🔍  Search members, classes, or reports...");
        search.setPrefWidth(320);
        search.setStyle(StyleUtil.FIELD);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminLabel = new Label("🔔    Admin Panel  ▾");
        adminLabel.setFont(Font.font("Arial", 12));

        bar.getChildren().addAll(search, spacer, adminLabel);
        return bar;
    }

    // ── Build all content panels ─────────────────────────────────────────────
    private void buildAllPanels() {
        buildDashboardPanel();
        buildMembersPanel();
        buildStaffPanel();
    }

    // ── Dashboard Overview Panel ──────────────────────────────────────────────
    private void buildDashboardPanel() {
        dashboardContent = new VBox(16);
        dashboardContent.setPadding(new Insets(24));

        Label title = new Label("Dashboard Overview");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label sub = new Label("Welcome back, Alex. Here's what's happening at GymConnect today.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web(GRAY_TEXT));

        // Stat cards row
        HBox statsRow = new HBox(16);

        // Each stat card: [icon, label, percent, value]
        String[][] stats = {
            {"👥", "Total Members",   "+12%", "1,240", "#5C6BC0"},
            {"✅", "Active Members",  "+5%",  "1,102", "#43A047"},
            {"❌", "Expired Members", "-2%",  "138",   "#E53935"},
            {"💰", "Monthly Revenue", "+8%",  "$45,200","#FB8C00"}
        };

        totalMembersLbl   = new Label("1,240");
        activeMembersLbl  = new Label("1,102");
        expiredMembersLbl = new Label("138");
        Label[] valueLbls = {totalMembersLbl, activeMembersLbl, expiredMembersLbl, new Label("$45,200")};

        for (int i = 0; i < stats.length; i++) {
            VBox card = buildStatCard(stats[i][0], stats[i][1], stats[i][2], valueLbls[i], stats[i][4]);
            HBox.setHgrow(card, Priority.ALWAYS);
            statsRow.getChildren().add(card);
        }

        // Recent Activity panel
        VBox activityCard = new VBox(10);
        activityCard.setStyle(StyleUtil.CARD);
        activityCard.setPrefWidth(260);
        Label actTitle = new Label("Recent Activity");
        actTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        String[][] activities = {
            {"John Doe",     "renewed Pro membership",        "2 mins ago",  "#F57C00"},
            {"Sarah Miller", "signed up for Basic",           "15 mins ago", "#F57C00"},
            {"Michael Chen", "upgraded to Elite tier",        "1 hour ago",  "#F57C00"},
            {"Emily Watson", "checked in to Gym-1",           "3 hours ago", DARK_TEXT},
            {"Robert Fox",   "membership expired",            "5 hours ago", "#E53935"}
        };
        activityCard.getChildren().add(actTitle);

        // Loop to build activity entries
        for (String[] act : activities) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label avatar = new Label("👤");
            VBox info = new VBox(2);
            Label name = new Label(act[0]);
            name.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            Label desc = new Label(act[1]);
            desc.setFont(Font.font("Arial", 11));
            desc.setTextFill(Color.web(act[3]));
            Label time = new Label(act[2]);
            time.setFont(Font.font("Arial", 10));
            time.setTextFill(Color.web(GRAY_TEXT));
            info.getChildren().addAll(name, desc, time);
            row.getChildren().addAll(avatar, info);
            activityCard.getChildren().add(row);
        }
        Button viewAllBtn = new Button("View All Activity");
        viewAllBtn.setMaxWidth(Double.MAX_VALUE);
        viewAllBtn.setStyle(StyleUtil.BTN_WHITE);
        activityCard.getChildren().add(viewAllBtn);

        // Membership distribution placeholder
        VBox distCard = new VBox(10);
        distCard.setStyle(StyleUtil.CARD);
        HBox.setHgrow(distCard, Priority.ALWAYS);
        Label distTitle = new Label("Membership Distribution");
        distTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label distSub = new Label("Breakdown by plan tier");
        distSub.setFont(Font.font("Arial", 11));
        distSub.setTextFill(Color.web(GRAY_TEXT));

        // Simple bar chart using colored rectangles
        HBox bars = new HBox(20);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.setPadding(new Insets(20, 0, 0, 0));
        String[][] barData = {{"Basic (412)", "150", "#9E9E9E"}, {"Pro (580)", "200", ORANGE}, {"Elite (248)", "130", "#37474F"}};
        for (String[] b : barData) {
            VBox col = new VBox(4);
            col.setAlignment(Pos.BOTTOM_CENTER);
            Region bar = new Region();
            bar.setMinHeight(Double.parseDouble(b[1]));
            bar.setPrefWidth(80);
            bar.setStyle("-fx-background-color:" + b[2] + ";");
            Label lbl = new Label(b[0]);
            lbl.setFont(Font.font("Arial", 11));
            col.getChildren().addAll(bar, lbl);
            bars.getChildren().add(col);
        }
        distCard.getChildren().addAll(distTitle, distSub, bars);

        HBox middleRow = new HBox(16, distCard, activityCard);
        dashboardContent.getChildren().addAll(title, sub, statsRow, middleRow);
    }

    // Helper to build a stat card
    private VBox buildStatCard(String icon, String label, String pct, Label valueLbl, String iconColor) {
        VBox card = new VBox(8);
        card.setStyle(StyleUtil.CARD);
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        boolean positive = pct.startsWith("+");
        Label pctLbl = new Label(pct);
        pctLbl.setFont(Font.font("Arial", 11));
        pctLbl.setTextFill(positive ? Color.GREEN : Color.RED);
        top.getChildren().addAll(iconLbl, sp, pctLbl);

        Label nameLbl = new Label(label);
        nameLbl.setFont(Font.font("Arial", 12));
        nameLbl.setTextFill(Color.web(GRAY_TEXT));
        valueLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        card.getChildren().addAll(top, nameLbl, valueLbl);
        return card;
    }

    // ── Member Management Panel ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void buildMembersPanel() {
        membersContent = new VBox(16);
        membersContent.setPadding(new Insets(24));

        // Header row with title and Add button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(2);
        Label title = new Label("Member Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label sub = new Label("Track, filter, and manage your gym's growing community.");
        sub.setFont(Font.font("Arial", 12)); sub.setTextFill(Color.web(GRAY_TEXT));
        titleBox.getChildren().addAll(title, sub);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        addMemberBtn = new Button("➕  Add New Member");
        addMemberBtn.setStyle(StyleUtil.BTN_ORANGE);
        addMemberBtn.setPrefHeight(36);
        header.getChildren().addAll(titleBox, sp, addMemberBtn);

        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(12));
        searchBar.setStyle("-fx-background-color:white;-fx-background-radius:8;");
        searchField = new TextField();
        searchField.setPromptText("🔍  Search by name or contact number...");
        searchField.setPrefWidth(350); searchField.setStyle(StyleUtil.FIELD);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBtn = new Button("Search"); searchBtn.setStyle(StyleUtil.BTN_ORANGE);
        refreshBtn = new Button("↺ Refresh"); refreshBtn.setStyle(StyleUtil.BTN_WHITE);
        searchBar.getChildren().addAll(searchField, searchBtn, refreshBtn);

        // Member table
        memberTable = new TableView<>();
        memberTable.setStyle("-fx-background-color:white;");
        memberTable.setPrefHeight(320);
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Table columns definition
        String[] colNames   = {"Name", "Contact", "Membership", "Join Date", "Expiry Date", "Status"};
        int[]    colIndexes = {1,       2,          4,            5,           6,              7};

        for (int i = 0; i < colNames.length; i++) {
            final int idx = colIndexes[i];
            TableColumn<String[], String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue() != null && p.getValue().length > idx ? p.getValue()[idx] : ""
            ));
            // Status column: colored badge
            if (colNames[i].equals("Status")) {
                col.setCellFactory(tc -> new TableCell<>() {
                    @Override
                    protected void updateItem(String val, boolean empty) {
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

        // Action column with Edit/Delete/Renew buttons
        TableColumn<String[], Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(tc -> new TableCell<>() {
            private final Button edit   = new Button("✏");
            private final Button delete = new Button("🗑");
            {
                edit.setStyle("-fx-cursor:hand;-fx-background-color:transparent;");
                delete.setStyle("-fx-cursor:hand;-fx-background-color:transparent;-fx-text-fill:#E53935;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                HBox box = new HBox(4, edit, delete);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
                // Fire external controller events via stored buttons
                edit.setOnAction(e -> {
                    memberTable.getSelectionModel().select(getIndex());
                    if (editMemberBtn != null) editMemberBtn.fire();
                });
                delete.setOnAction(e -> {
                    memberTable.getSelectionModel().select(getIndex());
                    if (deleteMemberBtn != null) deleteMemberBtn.fire();
                });
            }
        });
        memberTable.getColumns().add(actionCol);

        // Hidden proxy buttons used by controller
        editMemberBtn   = new Button(); editMemberBtn.setVisible(false);
        deleteMemberBtn = new Button(); deleteMemberBtn.setVisible(false);
        renewBtn        = new Button("Renew Membership");
        renewBtn.setStyle(StyleUtil.BTN_ORANGE); renewBtn.setVisible(false);

        // Stat summary bar
        HBox summaryBar = new HBox(16);
        summaryBar.setPadding(new Insets(12, 0, 0, 0));
        String[][] summaryData = {
            {"👥", "TOTAL MEMBERS",   "1,284"},
            {"✅", "ACTIVE MEMBERS",  "1,150"},
            {"❌", "EXPIRED SOON",    "34"}
        };
        for (String[] s : summaryData) {
            HBox card = new HBox(10);
            card.setPadding(new Insets(12)); card.setStyle(StyleUtil.CARD);
            HBox.setHgrow(card, Priority.ALWAYS);
            Label icon = new Label(s[0]); icon.setFont(Font.font(22));
            VBox info = new VBox(2);
            Label lbl = new Label(s[1]); lbl.setFont(Font.font("Arial", 10)); lbl.setTextFill(Color.web(GRAY_TEXT));
            Label val = new Label(s[2]); val.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            info.getChildren().addAll(lbl, val);
            card.getChildren().addAll(icon, info);
            summaryBar.getChildren().add(card);
        }

        // Add-member form (hidden by default, shown when addMemberBtn is clicked)
        addMemberFormNode = buildAddMemberForm();
        addMemberFormNode.setVisible(false); addMemberFormNode.setManaged(false);

        // Edit form (hidden by default)
        editMemberFormNode = buildEditMemberForm();
        editMemberFormNode.setVisible(false); editMemberFormNode.setManaged(false);

        membersContent.getChildren().addAll(
            header, searchBar, memberTable, summaryBar,
            editMemberBtn, deleteMemberBtn, renewBtn,
            addMemberFormNode, editMemberFormNode
        );
    }

    // Add-member form panel
    private VBox buildAddMemberForm() {
        VBox form = new VBox(10);
        form.setStyle(StyleUtil.CARD); form.setPadding(new Insets(16));
        memberFormTitle = new Label("Add New Member");
        memberFormTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        mUsernameField = new TextField(); mUsernameField.setPromptText("Username");
        mPasswordField = new TextField(); mPasswordField.setPromptText("Password");
        mNameField     = new TextField(); mNameField.setPromptText("Full Name");
        mContactField  = new TextField(); mContactField.setPromptText("+977 9841234567");
        mEmailField    = new TextField(); mEmailField.setPromptText("Email");
        mJoinDateField = new TextField(); mJoinDateField.setPromptText("Join Date (YYYY-MM-DD)");

        mTypeCombo = new ComboBox<>(FXCollections.observableArrayList("1 - Basic", "2 - Pro", "3 - Elite"));
        mTypeCombo.setPromptText("Select Membership"); mTypeCombo.setMaxWidth(Double.MAX_VALUE);

        // Apply field style in a loop
        TextField[] fields = {mUsernameField, mPasswordField, mNameField, mContactField, mEmailField, mJoinDateField};
        for (TextField f : fields) { f.setStyle(StyleUtil.FIELD); f.setPrefHeight(34); }

        HBox btnRow = new HBox(10);
        saveNewMemberBtn = new Button("Save Member"); saveNewMemberBtn.setStyle(StyleUtil.BTN_ORANGE);
        cancelMemberBtn  = new Button("Cancel");      cancelMemberBtn.setStyle(StyleUtil.BTN_WHITE);
        btnRow.getChildren().addAll(saveNewMemberBtn, cancelMemberBtn);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.addRow(0, new Label("Username:"), mUsernameField, new Label("Password:"),   mPasswordField);
        grid.addRow(1, new Label("Full Name:"), mNameField,    new Label("Contact:"),     mContactField);
        grid.addRow(2, new Label("Email:"),     mEmailField,   new Label("Join Date:"),   mJoinDateField);
        grid.addRow(3, new Label("Membership:"), mTypeCombo);
        ColumnConstraints cc = new ColumnConstraints(); cc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), cc, new ColumnConstraints(), cc);

        form.getChildren().addAll(memberFormTitle, grid, btnRow);
        return form;
    }

    // Edit-member form panel
    private VBox buildEditMemberForm() {
        VBox form = new VBox(10);
        form.setStyle(StyleUtil.CARD); form.setPadding(new Insets(16));
        Label formLbl = new Label("Edit Member");
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

    // ── Staff Management Panel ────────────────────────────────────────────────
    @SuppressWarnings("deprecation")
    private void buildStaffPanel() {
        staffContent = new VBox(16);
        staffContent.setPadding(new Insets(24));

        Label title = new Label("Staff Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Label sub = new Label("Manage staff accounts and schedules.");
        sub.setFont(Font.font("Arial", 12)); sub.setTextFill(Color.web(GRAY_TEXT));

        addStaffBtn = new Button("➕  Add New Staff"); addStaffBtn.setStyle(StyleUtil.BTN_ORANGE);
        HBox header = new HBox(); header.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        VBox titleBox = new VBox(2, title, sub);
        header.getChildren().addAll(titleBox, sp, addStaffBtn);

        // Staff table
        staffTable = new TableView<>();
        staffTable.setStyle("-fx-background-color:white;");
        staffTable.setPrefHeight(300);
        staffTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] sCols = {"Staff ID", "Username", "Salary"};
        int[]    sIdx  = {0,           1,          2};
        for (int i = 0; i < sCols.length; i++) {
            final int idx = sIdx[i];
            TableColumn<String[], String> col = new TableColumn<>(sCols[i]);
            col.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue() != null && p.getValue().length > idx ? p.getValue()[idx] : ""
            ));
            staffTable.getColumns().add(col);
        }

        // Delete action column
        TableColumn<String[], Void> actCol = new TableColumn<>("Actions");
        actCol.setCellFactory(tc -> new TableCell<>() {
            private final Button del = new Button("🗑");
            { del.setStyle("-fx-cursor:hand;-fx-background-color:transparent;-fx-text-fill:#E53935;"); }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                setGraphic(del);
                del.setOnAction(e -> {
                    staffTable.getSelectionModel().select(getIndex());
                    if (deleteStaffBtn != null) deleteStaffBtn.fire();
                });
            }
        });
        staffTable.getColumns().add(actCol);

        deleteStaffBtn = new Button(); deleteStaffBtn.setVisible(false);

        // Add staff form — stored as field for safe toggling
        addStaffFormNode = buildAddStaffForm();
        addStaffFormNode.setVisible(false); addStaffFormNode.setManaged(false);

        staffContent.getChildren().addAll(header, staffTable, deleteStaffBtn, addStaffFormNode);
    }

    private VBox buildAddStaffForm() {
        VBox form = new VBox(10);
        form.setStyle(StyleUtil.CARD); form.setPadding(new Insets(16));
        staffFormTitle = new Label("Add New Staff");
        staffFormTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        sUsernameField = new TextField(); sUsernameField.setPromptText("Username");
        sPasswordField = new TextField(); sPasswordField.setPromptText("Password");
        sSalaryField   = new TextField(); sSalaryField.setPromptText("Salary");

        TextField[] fields = {sUsernameField, sPasswordField, sSalaryField};
        for (TextField f : fields) { f.setStyle(StyleUtil.FIELD); f.setPrefHeight(34); }

        saveStaffBtn   = new Button("Save Staff"); saveStaffBtn.setStyle(StyleUtil.BTN_ORANGE);
        cancelStaffBtn = new Button("Cancel");     cancelStaffBtn.setStyle(StyleUtil.BTN_WHITE);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.addRow(0, new Label("Username:"), sUsernameField, new Label("Password:"), sPasswordField);
        grid.addRow(1, new Label("Salary:"),   sSalaryField);
        ColumnConstraints cc = new ColumnConstraints(); cc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), cc, new ColumnConstraints(), cc);

        form.getChildren().addAll(staffFormTitle, grid, new HBox(10, saveStaffBtn, cancelStaffBtn));
        return form;
    }

    // ── Getters (encapsulation) ───────────────────────────────────────────────
    public Button getDashboardNavBtn()   { return dashboardNavBtn; }
    public Button getMembersNavBtn()     { return membersNavBtn; }
    public Button getStaffNavBtn()       { return staffNavBtn; }
    public Button getLogoutBtn()         { return logoutBtn; }

    public TableView<String[]> getMemberTable()  { return memberTable; }
    public TextField getSearchField()            { return searchField; }
    public Button    getSearchBtn()              { return searchBtn; }
    public Button    getAddMemberBtn()           { return addMemberBtn; }
    public Button    getRefreshBtn()             { return refreshBtn; }
    public Button    getEditMemberBtn()          { return editMemberBtn; }
    public Button    getDeleteMemberBtn()        { return deleteMemberBtn; }
    public Button    getRenewBtn()               { return renewBtn; }

    public TextField getMUsernameField() { return mUsernameField; }
    public TextField getMPasswordField() { return mPasswordField; }
    public TextField getMNameField()     { return mNameField; }
    public TextField getMContactField()  { return mContactField; }
    public TextField getMEmailField()    { return mEmailField; }
    public TextField getMJoinDateField() { return mJoinDateField; }
    public ComboBox<String> getMTypeCombo() { return mTypeCombo; }
    public Button getSaveNewMemberBtn()  { return saveNewMemberBtn; }
    public Button getCancelMemberBtn()   { return cancelMemberBtn; }

    public TextField getEditNameField()    { return editNameField; }
    public TextField getEditContactField() { return editContactField; }
    public TextField getEditEmailField()   { return editEmailField; }
    public Label     getEditMemberIdLbl()  { return editMemberIdLbl; }
    public Button    getSaveEditBtn()      { return saveEditBtn; }
    public Button    getCancelEditBtn()    { return cancelEditBtn; }

    public TableView<String[]> getStaffTable()   { return staffTable; }
    public TextField getSUsernameField()  { return sUsernameField; }
    public TextField getSPasswordField()  { return sPasswordField; }
    public TextField getSSalaryField()    { return sSalaryField; }
    public Button    getAddStaffBtn()     { return addStaffBtn; }
    public Button    getDeleteStaffBtn()  { return deleteStaffBtn; }
    public Button    getSaveStaffBtn()    { return saveStaffBtn; }
    public Button    getCancelStaffBtn()  { return cancelStaffBtn; }

    public Label getTotalMembersLbl()   { return totalMembersLbl; }
    public Label getActiveMembersLbl()  { return activeMembersLbl; }
    public Label getExpiredMembersLbl() { return expiredMembersLbl; }

    // Switch content panel by nav selection
    public void showPanel(String panel) {
        switch (panel) {
            case "dashboard" -> { contentArea.setCenter(dashboardContent); dashboardNavBtn.setStyle(StyleUtil.NAV_BTN_ACTIVE); membersNavBtn.setStyle(StyleUtil.NAV_BTN); staffNavBtn.setStyle(StyleUtil.NAV_BTN); }
            case "members"   -> { contentArea.setCenter(membersContent);   membersNavBtn.setStyle(StyleUtil.NAV_BTN_ACTIVE);   dashboardNavBtn.setStyle(StyleUtil.NAV_BTN); staffNavBtn.setStyle(StyleUtil.NAV_BTN); }
            case "staff"     -> { contentArea.setCenter(staffContent);     staffNavBtn.setStyle(StyleUtil.NAV_BTN_ACTIVE);     dashboardNavBtn.setStyle(StyleUtil.NAV_BTN); membersNavBtn.setStyle(StyleUtil.NAV_BTN); }
        }
    }

    // Show/hide add member form — uses stored reference (safe)
    public void showAddMemberForm(boolean show) {
        addMemberFormNode.setVisible(show);
        addMemberFormNode.setManaged(show);
    }

    // Show/hide edit member form — uses stored reference (safe)
    public void showEditMemberForm(boolean show) {
        editMemberFormNode.setVisible(show);
        editMemberFormNode.setManaged(show);
    }

    // Show/hide add staff form — uses stored reference (safe)
    public void showAddStaffForm(boolean show) {
        addStaffFormNode.setVisible(show);
        addStaffFormNode.setManaged(show);
    }
}
