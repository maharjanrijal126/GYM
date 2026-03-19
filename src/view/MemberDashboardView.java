package view;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import util.BaseView;
import util.StyleUtil;

public class MemberDashboardView extends BaseView {

    // Encapsulation: private fields, all set by controller
    private Label       welcomeLabel;
    private Label       nameLabel;
    private Label       memberNoLabel;
    private Label       planLabel;
    private Label       statusLabel;
    private Label       joinDateLabel;
    private Label       expiryDateLabel;
    private Label       daysRemainingLabel;
    private ProgressBar progressBar;
    private Label       progressLabel;
    private Button      renewBtn;
    private Button      logoutBtn;

    public MemberDashboardView() {
        buildUI();
    }

    @Override
    public void buildUI() {
        this.setStyle("-fx-background-color: #F5F5F5;");
        this.setTop(buildTopBar());
        this.setCenter(buildMainContent());
        this.setBottom(buildFooter());
    }

    // ── Top bar: logo | bell | name+number | logout ───────────────────────────
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white;" +
                     "-fx-border-color: #e8e8e8; -fx-border-width: 0 0 1 0;");

        // Logo
        Label logo = new Label("🏋 GymConnect");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ORANGE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bell icon
        Label bell = new Label("🔔");
        bell.setFont(Font.font(15));
        bell.setPadding(new Insets(0, 12, 0, 0));

        // Member name + number — filled by controller
        nameLabel = new Label("");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.web("#1a1a1a"));

        memberNoLabel = new Label("");
        memberNoLabel.setFont(Font.font("Arial", 11));
        memberNoLabel.setTextFill(Color.web("#888"));

        VBox nameBox = new VBox(1, nameLabel, memberNoLabel);
        nameBox.setAlignment(Pos.CENTER_LEFT);

        // Logout button
        logoutBtn = new Button("Logout");
        logoutBtn.setStyle(StyleUtil.BTN_WHITE);
        logoutBtn.setPadding(new Insets(6, 14, 6, 14));

        bar.getChildren().addAll(logo, spacer, bell, nameBox, new Label("  "), logoutBtn);
        return bar;
    }

    // ── Main scrollable content ───────────────────────────────────────────────
    private ScrollPane buildMainContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(22, 22, 22, 22));

        // Welcome heading — updated by controller with real name
        welcomeLabel = new Label("Welcome back!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setTextFill(Color.web("#1a1a1a"));

        Label sub = new Label("Your fitness journey is on track. Ready for today's workout?");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#666"));

        VBox headerBox = new VBox(4, welcomeLabel, sub);

        // Middle row: membership card (left, grows) + right panel (fixed width)
        HBox midRow = new HBox(16);
        VBox membershipCard = buildMembershipCard();
        HBox.setHgrow(membershipCard, Priority.ALWAYS);

        VBox rightPanel = buildRightPanel();
        rightPanel.setPrefWidth(260);
        rightPanel.setMinWidth(260);
        rightPanel.setMaxWidth(260);

        midRow.getChildren().addAll(membershipCard, rightPanel);

        // Info cards row: Join Date | Expiry Date | Days Remaining
        HBox infoRow = buildInfoRow();

        content.getChildren().addAll(headerBox, midRow, infoRow);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;" +
                        "-fx-border-color: transparent;");
        return scroll;
    }

    // ── Membership status card (left side) ────────────────────────────────────
    private VBox buildMembershipCard() {
        HBox card = new HBox(16);
        card.setStyle("-fx-background-color: white;" +
                      "-fx-background-radius: 10;" +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);" +
                      "-fx-padding: 14;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Gym image box (left)
        VBox imgBox = new VBox();
        imgBox.setPrefWidth(150); imgBox.setPrefHeight(120);
        imgBox.setMinWidth(150);  imgBox.setMinHeight(120);
        imgBox.setMaxWidth(150);  imgBox.setMaxHeight(120);
        imgBox.setStyle("-fx-background-color: #c8b89a;" +
                        "-fx-background-radius: 8;");
        imgBox.setAlignment(Pos.CENTER);
        Label gymIcon = new Label("🏋");
        gymIcon.setFont(Font.font(38));
        imgBox.getChildren().add(gymIcon);

        // Details (right of image)
        VBox details = new VBox(8);
        HBox.setHgrow(details, Priority.ALWAYS);

        // "MEMBERSHIP STATUS" small label
        Label statusTag = new Label("MEMBERSHIP STATUS");
        statusTag.setFont(Font.font("Arial", 9));
        statusTag.setTextFill(Color.web("#999"));

        // Status badge (ACTIVE / EXPIRED) — aligned to the right
        statusLabel = new Label("● ACTIVE");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        statusLabel.setTextFill(Color.web("#2E7D32"));
        statusLabel.setStyle("-fx-background-color: #E8F5E9;" +
                             "-fx-background-radius: 4; -fx-padding: 2 8;");

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label statusTagLeft = new Label("MEMBERSHIP STATUS");
        statusTagLeft.setFont(Font.font("Arial", 9));
        statusTagLeft.setTextFill(Color.web("#999"));
        Region statusSpacer = new Region();
        HBox.setHgrow(statusSpacer, Priority.ALWAYS);
        statusRow.getChildren().addAll(statusTagLeft, statusSpacer, statusLabel);

        // Plan name (large bold)
        planLabel = new Label("—");
        planLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        planLabel.setTextFill(Color.web("#1a1a1a"));

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(8);
        progressBar.setStyle("-fx-accent: #F57C00; -fx-background-color: #f0f0f0;" +
                             "-fx-background-radius: 4; -fx-border-radius: 4;");

        // Progress text
        progressLabel = new Label("Loading membership data...");
        progressLabel.setFont(Font.font("Arial", 11));
        progressLabel.setTextFill(Color.web("#888"));

        details.getChildren().addAll(statusRow, planLabel, progressBar, progressLabel);
        card.getChildren().addAll(imgBox, details);

        VBox wrapper = new VBox(card);
        return wrapper;
    }

    // ── Right panel: Quick Actions + Upcoming Class ───────────────────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox(12);

        // Quick Actions card
        VBox actCard = new VBox(10);
        actCard.setPadding(new Insets(14));
        actCard.setStyle("-fx-background-color: white;" +
                         "-fx-background-radius: 10;" +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        HBox qTitle = new HBox(6);
        qTitle.setAlignment(Pos.CENTER_LEFT);
        Label thunder = new Label("⚡");
        thunder.setFont(Font.font(13));
        thunder.setTextFill(Color.web(ORANGE));
        Label qLbl = new Label("Quick Actions");
        qLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        qLbl.setTextFill(Color.web("#1a1a1a"));
        qTitle.getChildren().addAll(thunder, qLbl);

        // Renew Membership — orange filled button
        renewBtn = new Button("🔄  Renew Membership   ›");
        renewBtn.setMaxWidth(Double.MAX_VALUE);
        renewBtn.setPrefHeight(40);
        renewBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        renewBtn.setTextFill(Color.WHITE);
        String base  = "-fx-background-color:#F57C00;-fx-background-radius:6;-fx-cursor:hand;";
        String hover = "-fx-background-color:#E65100;-fx-background-radius:6;-fx-cursor:hand;";
        renewBtn.setStyle(base);
        renewBtn.setOnMouseEntered(e -> renewBtn.setStyle(hover));
        renewBtn.setOnMouseExited(e  -> renewBtn.setStyle(base));

        // Class Schedule — outlined button
        Button scheduleBtn = new Button("📅  Class Schedule   ›");
        scheduleBtn.setMaxWidth(Double.MAX_VALUE);
        scheduleBtn.setPrefHeight(40);
        scheduleBtn.setFont(Font.font("Arial", 12));
        scheduleBtn.setStyle("-fx-background-color: white;" +
                             "-fx-border-color: #e0e0e0; -fx-border-radius: 6;" +
                             "-fx-background-radius: 6; -fx-cursor: hand;");
        scheduleBtn.setOnAction(e -> showScheduleInfo());

        actCard.getChildren().addAll(qTitle, renewBtn, scheduleBtn);

        // Upcoming Class card (matches original design)
        VBox classCard = new VBox(6);
        classCard.setPadding(new Insets(14));
        classCard.setStyle("-fx-background-color: white;" +
                           "-fx-background-radius: 10;" +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label classTag = new Label("UPCOMING CLASS");
        classTag.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        classTag.setTextFill(Color.web(ORANGE));

        Label className = new Label("HIIT Warriors");
        className.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        className.setTextFill(Color.web("#1a1a1a"));

        Label classTime = new Label("Today at 6:30 PM  •  Studio B");
        classTime.setFont(Font.font("Arial", 11));
        classTime.setTextFill(Color.web("#888"));

        Label manageLink = new Label("Manage Booking →");
        manageLink.setFont(Font.font("Arial", 11));
        manageLink.setTextFill(Color.web(ORANGE));
        manageLink.setStyle("-fx-cursor: hand;");

        classCard.getChildren().addAll(classTag, className, classTime, manageLink);

        panel.getChildren().addAll(actCard, classCard);
        return panel;
    }

    // ── Info cards row: Join Date | Expiry Date | Time Remaining ─────────────
    private HBox buildInfoRow() {
        HBox row = new HBox(14);

        // Initialise — controller fills with real data
        joinDateLabel      = new Label("—");
        expiryDateLabel    = new Label("—");
        daysRemainingLabel = new Label("—");

        // Loop over the three card definitions
        Object[][] defs = {
            {"📅", "Join Date",      joinDateLabel,      "#F57C00", false},
            {"📅", "Expiry Date",    expiryDateLabel,    "#F57C00", false},
            {"⏳", "Time Remaining", daysRemainingLabel, "#F57C00", true}
        };

        for (Object[] d : defs) {
            VBox card = new VBox(6);
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setPadding(new Insets(16));
            boolean isOrange = (boolean) d[4];

            if (isOrange) {
                card.setStyle("-fx-background-color: #F57C00;" +
                              "-fx-background-radius: 10;" +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
            } else {
                card.setStyle("-fx-background-color: white;" +
                              "-fx-background-radius: 10;" +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
            }

            // Calendar/clock icon
            Label icon = new Label((String) d[0]);
            icon.setFont(Font.font(16));
            if (isOrange) icon.setTextFill(Color.WHITE);
            else          icon.setTextFill(Color.web(ORANGE));

            // Title label
            Label title = new Label((String) d[1]);
            title.setFont(Font.font("Arial", 11));
            title.setTextFill(isOrange ? Color.WHITE : Color.web("#888"));

            // Value label (the actual date or days)
            Label value = (Label) d[2];
            if (isOrange) {
                value.setFont(Font.font("Arial", FontWeight.BOLD, 30));
                value.setTextFill(Color.WHITE);
                Label actionLbl = new Label("ACTION REQUIRED SOON");
                actionLbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
                actionLbl.setTextFill(Color.WHITE);
                card.getChildren().addAll(icon, title, value, actionLbl);
            } else {
                value.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                value.setTextFill(Color.web("#1a1a1a"));
                card.getChildren().addAll(icon, title, value);
            }
            row.getChildren().add(card);
        }
        return row;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(10, 22, 10, 22));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: white;" +
                        "-fx-border-color: #eee; -fx-border-width: 1 0 0 0;");

        Label logo = new Label("🏋 GymConnect");
        logo.setFont(Font.font("Arial", 11));
        logo.setTextFill(Color.web("#999"));

        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);

        Label copy = new Label("© 2023 GymConnect Systems. All rights reserved.");
        copy.setFont(Font.font("Arial", 11));
        copy.setTextFill(Color.web("#999"));

        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);

        // Loop to build footer links
        String[] linkNames = {"Support", "Privacy", "Terms"};
        HBox linkBox = new HBox(14);
        linkBox.setAlignment(Pos.CENTER_RIGHT);
        for (String name : linkNames) {
            Label link = new Label(name);
            link.setFont(Font.font("Arial", 11));
            link.setTextFill(Color.web("#555"));
            link.setStyle("-fx-cursor: hand;");
            linkBox.getChildren().add(link);
        }

        footer.getChildren().addAll(logo, sp1, copy, sp2, linkBox);
        return footer;
    }

    // Show class schedule info popup
    private void showScheduleInfo() {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
            "Class Schedule:\n\nMon / Wed / Fri — HIIT Warriors  6:30 PM  Studio B\n" +
            "Tue / Thu       — Yoga Flow        7:00 AM  Studio A\n" +
            "Saturday        — Strength Camp    9:00 AM  Main Gym\n\n" +
            "Contact the front desk to book a spot.",
            javafx.scene.control.ButtonType.OK);
        a.setTitle("Class Schedule");
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ── Setters — all called by MemberController with real DB data ────────────

    public void setMemberName(String name) {
        welcomeLabel.setText("Welcome back, " + name + "!");
        nameLabel.setText(name);
    }

    public void setMemberNo(String no) {
        memberNoLabel.setText("Member #" + no);
    }

    public void setPlan(String plan) {
        planLabel.setText(plan != null ? plan.toUpperCase() : "—");
    }

    public void setStatus(String status) {
        boolean active = "Active".equalsIgnoreCase(status);
        statusLabel.setText(active ? "● ACTIVE" : "● EXPIRED");
        statusLabel.setTextFill(active ? Color.web("#2E7D32") : Color.web("#C62828"));
        statusLabel.setStyle(active
            ? "-fx-background-color:#E8F5E9;-fx-background-radius:4;-fx-padding:2 8;"
            : "-fx-background-color:#FFEBEE;-fx-background-radius:4;-fx-padding:2 8;");
    }

    public void setJoinDate(String date) {
        joinDateLabel.setText(date != null ? date : "—");
    }

    public void setExpiryDate(String date) {
        expiryDateLabel.setText(date != null ? date : "—");
    }

    public void setDaysRemaining(String days) {
        daysRemainingLabel.setText(days != null ? days : "—");
    }

    public void setProgress(double percent) {
        progressBar.setProgress(percent);
        int pct = (int) Math.round(percent * 100);
        progressLabel.setText(pct + "% of your billing cycle completed");
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Button getLogoutBtn() { return logoutBtn; }
    public Button getRenewBtn()  { return renewBtn; }
}
