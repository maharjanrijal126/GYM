package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

// LoginView extends StackPane directly (not BaseView which is BorderPane)
public class LoginView extends StackPane {

    // Encapsulation: private fields
    private TextField     usernameField;
    private PasswordField passwordField;
    private Button        loginBtn;
    private Button        registerBtn;   // "Create Account" link
    private Label         messageLabel;

    public LoginView() {
        buildUI();
    }

    // Build full login UI
    public void buildUI() {
        setBackgroundImage();
        buildCard();
    }

    // Gym background image
    private void setBackgroundImage() {
        try {
            Image bg = new Image("file:images/background.png");
            BackgroundImage bgImg = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
            );
            this.setBackground(new Background(bgImg));
        } catch (Exception e) {
            this.setStyle("-fx-background-color: #1a1a1a;");
        }
    }

    // White centered card — fixed compact size matching original design
    private void buildCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);

        // Fixed width and height — card stays compact regardless of window size
        card.setPrefWidth(370);
        card.setMaxWidth(370);
        card.setMinWidth(370);
        card.setPrefHeight(560);
        card.setMaxHeight(560);
        card.setMinHeight(560);

        card.setPadding(new Insets(30, 40, 30, 40));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +           // rounded corners
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 28, 0, 0, 6);"
        );

        card.getChildren().addAll(
            buildLogo(), buildTitle(), buildSubtitle(),
            buildUsernameSection(), buildPasswordSection(),
            buildLoginButton(), buildInfoBox(),
            buildRegisterLink(),
            buildFooter()
        );

        this.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private ImageView buildLogo() {
        ImageView logo = new ImageView();
        try { logo.setImage(new Image("file:images/logo.png")); }
        catch (Exception e) { System.out.println("Logo not found"); }
        logo.setFitWidth(60); logo.setFitHeight(60); logo.setPreserveRatio(true);
        return logo;
    }

    private Label buildTitle() {
        Label t = new Label("GymConnect");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        t.setTextFill(Color.web("#1a1a1a"));
        return t;
    }

    private Label buildSubtitle() {
        Label s = new Label("Welcome back! Please enter your details.");
        s.setFont(Font.font("Arial", 12));
        s.setTextFill(Color.web("#888"));
        return s;
    }

    private VBox buildUsernameSection() {
        Label lbl = new Label("Username or Email");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        usernameField = new TextField();
        usernameField.setPrefHeight(38);
        usernameField.setStyle("-fx-background-color:#f5f5f5;-fx-border-color:#e0e0e0;-fx-border-radius:5;-fx-background-radius:5;-fx-padding:5 10;");
        return new VBox(5, lbl, usernameField);
    }

    private VBox buildPasswordSection() {
        Label lbl    = new Label("Password");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label forgot = new Label("Forgot Password?");
        forgot.setFont(Font.font("Arial", 11));
        forgot.setTextFill(Color.web("#F57C00"));
        forgot.setStyle("-fx-cursor: hand;");

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // pushes forgot to right
        row.getChildren().addAll(lbl, spacer, forgot);

        passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.setPrefHeight(38);
        passwordField.setStyle("-fx-background-color:#f5f5f5;-fx-border-color:#e0e0e0;-fx-border-radius:5;-fx-background-radius:5;-fx-padding:5 10;");

        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", 11));

        return new VBox(5, row, passwordField, messageLabel);
    }

    private Button buildLoginButton() {
        loginBtn = new Button("Sign In  →");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(42);
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginBtn.setTextFill(Color.WHITE);
        String base  = "-fx-background-color:#F57C00;-fx-background-radius:6;-fx-cursor:hand;";
        String hover = "-fx-background-color:#E65100;-fx-background-radius:6;-fx-cursor:hand;";
        loginBtn.setStyle(base);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(hover));
        loginBtn.setOnMouseExited(e  -> loginBtn.setStyle(base));
        return loginBtn;
    }

    private HBox buildInfoBox() {
        String[] roles = {"Admins", "Staff", "Members"};
        TextFlow tf = new TextFlow();
        Text prefix = new Text("ⓘ  This portal provides unified access for ");
        prefix.setFont(Font.font("Arial", 11)); prefix.setFill(Color.web("#555"));
        tf.getChildren().add(prefix);

        // Loop through role names to bold each one
        for (int i = 0; i < roles.length; i++) {
            Text bold = new Text(roles[i]);
            bold.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            bold.setFill(Color.web("#333"));
            tf.getChildren().add(bold);
            if (i < roles.length - 1) {
                Text sep = new Text(i == roles.length - 2 ? ", and " : ", ");
                sep.setFont(Font.font("Arial", 11)); sep.setFill(Color.web("#555"));
                tf.getChildren().add(sep);
            }
        }
        Text suffix = new Text(". Your dashboard will load based on your account role.");
        suffix.setFont(Font.font("Arial", 11)); suffix.setFill(Color.web("#555"));
        tf.getChildren().add(suffix);
        tf.setMaxWidth(290);

        HBox box = new HBox(tf);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color:#FFF3E0;-fx-background-radius:6;-fx-border-color:#FFE0B2;-fx-border-radius:6;");
        return box;
    }

    // "Don't have an account? Create Account" link row
    private HBox buildRegisterLink() {
        HBox row = new HBox(4);
        row.setAlignment(Pos.CENTER);
        Label noAccount = new Label("Don't have an account?");
        noAccount.setFont(Font.font("Arial", 12));
        noAccount.setTextFill(Color.web("#555"));
        registerBtn = new Button("Create Account");
        registerBtn.setStyle(
            "-fx-background-color:transparent;-fx-text-fill:#F57C00;" +
            "-fx-font-size:12;-fx-cursor:hand;-fx-font-weight:bold;-fx-padding:0;"
        );
        row.getChildren().addAll(noAccount, registerBtn);
        return row;
    }

    private Label buildFooter() {
        Label f = new Label("© 2024 GYMCONNECT MANAGEMENT SOLUTIONS");
        f.setFont(Font.font("Arial", 10)); f.setTextFill(Color.web("#aaa"));
        return f;
    }

    // Getters (encapsulation)
    public String getUsername()      { return usernameField.getText().trim(); }
    public String getPassword()      { return passwordField.getText().trim(); }
    public Button getLoginBtn()      { return loginBtn; }
    public Button getRegisterBtn()   { return registerBtn; }

    // Show feedback message
    public void setMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setTextFill(success ? Color.GREEN : Color.RED);
    }
}
