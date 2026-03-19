package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

// Registration screen — same card-on-background design as LoginView
public class RegisterView extends StackPane {

    // Encapsulation: private fields exposed via getters
    private TextField     usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField     fullNameField;
    private TextField     contactField;
    private TextField     emailField;
    private Button        registerBtn;
    private Label         backToLoginLink;
    private Label         messageLabel;

    public RegisterView() {
        buildUI();
    }

    public void buildUI() {
        setBackgroundImage();
        buildCard();
    }

    // Same gym background as login screen
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

    // White registration card — fixed size with rounded corners matching login
    private void buildCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(400); card.setMaxWidth(400); card.setMinWidth(400);
        card.setPrefHeight(660); card.setMaxHeight(660); card.setMinHeight(660);
        card.setPadding(new Insets(28, 40, 28, 40));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 28, 0, 0, 6);"
        );

        card.getChildren().addAll(
            buildLogo(),
            buildTitle(),
            buildSubtitle(),
            buildFormSection(),
            buildRegisterButton(),
            buildMessageLabel(),
            buildBackLink(),
            buildFooter()
        );

        this.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    // Orange dumbbell logo
    private ImageView buildLogo() {
        ImageView logo = new ImageView();
        try { logo.setImage(new Image("file:images/logo.png")); }
        catch (Exception e) { System.out.println("Logo not found"); }
        logo.setFitWidth(55); logo.setFitHeight(55); logo.setPreserveRatio(true);
        return logo;
    }

    private Label buildTitle() {
        Label t = new Label("Create Account");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        t.setTextFill(Color.web("#1a1a1a"));
        return t;
    }

    private Label buildSubtitle() {
        Label s = new Label("Join GymConnect — fill in your details below.");
        s.setFont(Font.font("Arial", 12));
        s.setTextFill(Color.web("#888"));
        return s;
    }

    // All six input fields stacked in a VBox
    private VBox buildFormSection() {
        VBox section = new VBox(6);

        String fldStyle =
            "-fx-background-color:#f5f5f5;-fx-border-color:#e0e0e0;" +
            "-fx-border-radius:5;-fx-background-radius:5;-fx-padding:5 10;";

        // Full Name
        fullNameField = new TextField();
        fullNameField.setPromptText("e.g. John Doe");
        fullNameField.setPrefHeight(36); fullNameField.setStyle(fldStyle);

        // Username
        usernameField = new TextField();
        usernameField.setPromptText("Min. 3 characters");
        usernameField.setPrefHeight(36); usernameField.setStyle(fldStyle);

        // Email
        emailField = new TextField();
        emailField.setPromptText("you@example.com");
        emailField.setPrefHeight(36); emailField.setStyle(fldStyle);

        // Contact
        contactField = new TextField();
        contactField.setPromptText("+977 9841234567");
        contactField.setPrefHeight(36); contactField.setStyle(fldStyle);

        // Password
        passwordField = new PasswordField();
        passwordField.setPromptText("Min. 6 characters");
        passwordField.setPrefHeight(36); passwordField.setStyle(fldStyle);

        // Confirm Password
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefHeight(36); confirmPasswordField.setStyle(fldStyle);

        // Build label+field pairs using a loop over arrays
        String[]   labels = {"Full Name", "Username", "Email",
                             "Contact Number", "Password", "Confirm Password"};
        Control[]  fields = {fullNameField, usernameField, emailField,
                             contactField, passwordField, confirmPasswordField};

        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            lbl.setTextFill(Color.web("#333"));
            section.getChildren().addAll(lbl, fields[i]);
        }
        return section;
    }

    // Orange create account button
    private Button buildRegisterButton() {
        registerBtn = new Button("Create Account  →");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(42);
        registerBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        registerBtn.setTextFill(Color.WHITE);
        String base  = "-fx-background-color:#F57C00;-fx-background-radius:6;-fx-cursor:hand;";
        String hover = "-fx-background-color:#E65100;-fx-background-radius:6;-fx-cursor:hand;";
        registerBtn.setStyle(base);
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(hover));
        registerBtn.setOnMouseExited(e  -> registerBtn.setStyle(base));
        return registerBtn;
    }

    // Feedback label for validation errors / success
    private Label buildMessageLabel() {
        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", 11));
        messageLabel.setWrapText(true);
        return messageLabel;
    }

    // "Already have an account? Sign In" link back to login
    private HBox buildBackLink() {
        Label already = new Label("Already have an account?  ");
        already.setFont(Font.font("Arial", 11));
        already.setTextFill(Color.web("#555"));

        backToLoginLink = new Label("Sign In");
        backToLoginLink.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        backToLoginLink.setTextFill(Color.web("#F57C00"));
        backToLoginLink.setStyle("-fx-cursor: hand;");

        HBox row = new HBox(0, already, backToLoginLink);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private Label buildFooter() {
        Label f = new Label("© 2024 GYMCONNECT MANAGEMENT SOLUTIONS");
        f.setFont(Font.font("Arial", 10));
        f.setTextFill(Color.web("#aaa"));
        return f;
    }

    // ── Getters (encapsulation) ───────────────────────────────────────────────
    public String getUsername()        { return usernameField.getText().trim(); }
    public String getPassword()        { return passwordField.getText(); }
    public String getConfirmPassword() { return confirmPasswordField.getText(); }
    public String getFullName()        { return fullNameField.getText().trim(); }
    public String getContact()         { return contactField.getText().trim(); }
    public String getEmail()           { return emailField.getText().trim(); }
    public Button getRegisterBtn()     { return registerBtn; }
    public Label  getBackToLoginLink() { return backToLoginLink; }

    // Show validation / success message
    public void setMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setTextFill(success ? Color.web("#2E7D32") : Color.web("#C62828"));
    }

    // Clear all fields after successful registration
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        fullNameField.clear();
        contactField.clear();
        emailField.clear();
    }
}
