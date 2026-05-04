package com.store.ui;

import com.store.auth.AuthManager;
import com.store.auth.Role;
import com.store.auth.User;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * ============================================================
 *  LOGIN VIEW  — 3-mode auth screen
 *
 *  ┌──────────────────────┬──────────────────────┐
 *  │   Animated Brand     │  Sign In / Register   │
 *  │   Panel (left)       │  Form Panel (right)   │
 *  └──────────────────────┴──────────────────────┘
 *
 *  Modes:
 *    SIGN_IN           — everyone
 *    REGISTER_CUSTOMER — customers self-register
 *    REGISTER_STAFF    — admin creates staff (redirected from admin panel)
 * ============================================================
 */
public class LoginView {

    private enum Mode { SIGN_IN, REGISTER_CUSTOMER }

    private final AuthManager    auth;
    private final Consumer<User> onLoginSuccess;
    private final BorderPane     root;
    private Mode                 mode = Mode.SIGN_IN;

    // Form fields
    private TextField     usernameField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private VBox          confirmRow;
    private Label         formMsg;
    private Label         formTitle;
    private Label         formSubtitle;
    private Button        mainBtn;
    private HBox          tabBar;

    // Tab buttons
    private Button tabSignIn;
    private Button tabRegister;

    public LoginView(AuthManager auth, Consumer<User> onLoginSuccess) {
        this.auth           = auth;
        this.onLoginSuccess = onLoginSuccess;
        this.root           = new BorderPane();
        buildView();
    }

    private void buildView() {
        root.setLeft(buildBrandPanel());
        root.setCenter(buildFormPanel());
    }

    // ── LEFT: Brand / Hero panel ──────────────────────────────

    private StackPane buildBrandPanel() {
        StackPane panel = new StackPane();
        panel.setMinWidth(440);
        panel.setMaxWidth(440);
        panel.setStyle("-fx-background-color: #080b14;");

        // Animated gradient circles
        Circle c1 = glow(300, "#4f46e5", 0.18);
        Circle c2 = glow(200, "#7c3aed", 0.12);
        Circle c3 = glow(140, "#06b6d4", 0.10);
        Circle c4 = glow(90,  "#10b981", 0.14);

        StackPane.setAlignment(c1, Pos.TOP_LEFT);
        StackPane.setMargin(c1, new Insets(-100, 0, 0, -100));
        StackPane.setAlignment(c2, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(c2, new Insets(0, -80, -80, 0));
        StackPane.setAlignment(c3, Pos.CENTER_RIGHT);
        StackPane.setMargin(c3, new Insets(60, -50, 0, 0));
        StackPane.setAlignment(c4, Pos.BOTTOM_LEFT);
        StackPane.setMargin(c4, new Insets(0, 0, 80, 20));

        animatePulse(c1, 4.0);
        animatePulse(c2, 5.5);
        animatePulse(c3, 3.5);
        animatePulse(c4, 6.0);

        // Brand content
        VBox content = new VBox(0);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60, 48, 60, 48));

        // Logo
        Label icon = new Label("🛍");
        icon.setStyle("-fx-font-size: 64px;");

        Label spacer1 = new Label(); spacer1.setPrefHeight(16);

        Label name = new Label("SmartStore");
        name.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #f8fafc; " +
                "-fx-letter-spacing: -0.5px;");

        Label tagline = new Label("Online Shopping — Reimagined");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: #6366f1; -fx-font-weight: bold; " +
                "-fx-letter-spacing: 1px;");

        Label spacer2 = new Label(); spacer2.setPrefHeight(36);

        // Role cards
        VBox roleCards = new VBox(10);
        roleCards.setMaxWidth(320);
        roleCards.getChildren().addAll(
            roleCard("🔴", "ADMIN", "Full store control & management", "#ef4444", "#2d0a0a"),
            roleCard("🟡", "STAFF", "Process sales & manage inventory", "#eab308", "#2a1f00"),
            roleCard("🟢", "CUSTOMER", "Browse, buy & track your orders", "#22c55e", "#052e16")
        );

        Label spacer3 = new Label(); spacer3.setPrefHeight(40);

        // Version badge
        Label version = new Label("v2.1  ·  Role-Based Access");
        version.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 11px;");

        content.getChildren().addAll(icon, spacer1, name, tagline, spacer2, roleCards, spacer3, version);

        // Entry animation
        content.setOpacity(0);
        content.setTranslateY(24);
        FadeTransition ft = new FadeTransition(Duration.millis(900), content);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), content);
        tt.setFromY(24); tt.setToY(0); tt.play();

        panel.getChildren().addAll(c1, c2, c3, c4, content);
        return panel;
    }

    private HBox roleCard(String dot, String role, String desc, String color, String bg) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 12; " +
                "-fx-border-color: " + color + "22; -fx-border-width: 1; -fx-border-radius: 12;");

        Label dotLbl = new Label(dot);
        dotLbl.setStyle("-fx-font-size: 16px;");

        VBox text = new VBox(2);
        Label roleLbl = new Label(role);
        roleLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-letter-spacing: 1.5px;");
        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        text.getChildren().addAll(roleLbl, descLbl);

        card.getChildren().addAll(dotLbl, text);
        return card;
    }

    private Circle glow(double r, String color, double opacity) {
        Circle c = new Circle(r);
        c.setFill(Color.web(color, opacity));
        return c;
    }

    private void animatePulse(Circle c, double secs) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(secs), c);
        st.setFromX(1); st.setToX(1.12);
        st.setFromY(1); st.setToY(1.12);
        st.setCycleCount(Animation.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }

    // ── RIGHT: Form panel ─────────────────────────────────────

    private ScrollPane buildFormPanel() {
        VBox outer = new VBox();
        outer.setAlignment(Pos.CENTER);
        outer.setFillWidth(true);
        outer.setStyle("-fx-background-color: #0d1117;");
        VBox.setVgrow(outer, Priority.ALWAYS);

        VBox card = new VBox(0);
        card.setMaxWidth(420);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(40, 40, 40, 40));
        card.setStyle("-fx-background-color: #161b24; -fx-background-radius: 20; " +
                "-fx-border-color: #21262d; -fx-border-radius: 20; -fx-border-width: 1;");

        // ── Header ────────────────────────────────────────────
        formTitle = new Label("Welcome back");
        formTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        formSubtitle = new Label("Sign in to your SmartStore account");
        formSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #484f58;");

        VBox header = new VBox(4, formTitle, formSubtitle);
        header.setPadding(new Insets(0, 0, 24, 0));

        // ── Tab bar ───────────────────────────────────────────
        tabBar = new HBox(0);
        tabBar.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 10; -fx-padding: 4;");
        tabBar.setMaxWidth(Double.MAX_VALUE);

        tabSignIn  = tabBtn("Sign In",           true);
        tabRegister = tabBtn("Register as Customer", false);

        tabSignIn.setMaxWidth(Double.MAX_VALUE);
        tabRegister.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tabSignIn,   Priority.ALWAYS);
        HBox.setHgrow(tabRegister, Priority.ALWAYS);

        tabBar.getChildren().addAll(tabSignIn, tabRegister);
        VBox tabWrapper = new VBox(tabBar);
        tabWrapper.setPadding(new Insets(0, 0, 20, 0));

        // ── Fields ────────────────────────────────────────────
        usernameField = styledField("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(inputStyle());

        confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setStyle(inputStyle());
        confirmRow = fieldBlock("Confirm Password", confirmField);
        confirmRow.setVisible(false);
        confirmRow.setManaged(false);

        // ── Status ────────────────────────────────────────────
        formMsg = new Label();
        formMsg.setWrapText(true);
        formMsg.setMaxWidth(Double.MAX_VALUE);
        formMsg.setVisible(false);
        formMsg.setManaged(false);

        // ── Main Button ───────────────────────────────────────
        mainBtn = new Button("Sign In");
        mainBtn.setMaxWidth(Double.MAX_VALUE);
        mainBtn.setPrefHeight(46);
        mainBtn.setStyle(primaryBtnStyle("#6366f1"));

        // ── Hint ──────────────────────────────────────────────
        Label hint = new Label("Default admin: admin / admin123");
        hint.setStyle("-fx-text-fill: #21262d; -fx-font-size: 11px;");
        hint.setPadding(new Insets(8, 0, 0, 0));

        // ── Staff info box ────────────────────────────────────
        HBox staffInfo = new HBox(8);
        staffInfo.setAlignment(Pos.CENTER_LEFT);
        staffInfo.setPadding(new Insets(12, 14, 12, 14));
        staffInfo.setStyle("-fx-background-color: #0d2137; -fx-background-radius: 10; " +
                "-fx-border-color: #1d4ed8; -fx-border-width: 1; -fx-border-radius: 10;");
        Label infoIcon = new Label("ℹ");
        infoIcon.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 14px;");
        Label infoTxt = new Label("Staff accounts are created by the Admin only.");
        infoTxt.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 12px;");
        infoTxt.setWrapText(true);
        staffInfo.getChildren().addAll(infoIcon, infoTxt);
        staffInfo.setVisible(false);
        staffInfo.setManaged(false);

        // ── Assembly ──────────────────────────────────────────
        card.getChildren().addAll(
            header,
            tabWrapper,
            fieldBlock("Username", usernameField),
            fieldBlock("Password", passwordField),
            confirmRow,
            formMsg,
            staffInfo,
            mainBtn,
            hint
        );

        // Spacing between fields
        for (int i = 2; i <= 4; i++) {
            VBox.setMargin(card.getChildren().get(i), new Insets(0, 0, 12, 0));
        }
        VBox.setMargin(mainBtn, new Insets(8, 0, 0, 0));

        // ── Wire events ───────────────────────────────────────
        tabSignIn.setOnAction(e   -> switchMode(Mode.SIGN_IN,           tabSignIn,  "#6366f1", staffInfo));
        tabRegister.setOnAction(e -> switchMode(Mode.REGISTER_CUSTOMER, tabRegister,"#22c55e", staffInfo));
        mainBtn.setOnAction(e -> handleSubmit());
        usernameField.setOnAction(e -> handleSubmit());
        passwordField.setOnAction(e -> handleSubmit());
        confirmField.setOnAction(e  -> handleSubmit());

        // Card entry animation
        card.setOpacity(0);
        card.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
        tt.setFromY(20); tt.setToY(0); tt.play();

        outer.getChildren().add(card);
        VBox.setMargin(card, new Insets(40, 48, 40, 48));

        ScrollPane sp = new ScrollPane(outer);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: #0d1117; -fx-border-width: 0;");
        return sp;
    }

    private void switchMode(Mode newMode, Button activeTab, String accentColor, HBox staffInfo) {
        mode = newMode;
        clearMessages();
        usernameField.clear();
        passwordField.clear();
        confirmField.clear();

        // Tab styles
        String active   = "-fx-background-color: " + accentColor + "22; -fx-text-fill: " + accentColor + "; " +
                          "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                          "-fx-border-width: 0; -fx-cursor: hand; -fx-padding: 8 16;";
        String inactive = "-fx-background-color: transparent; -fx-text-fill: #484f58; " +
                          "-fx-font-size: 12px; -fx-font-weight: normal; -fx-background-radius: 8; " +
                          "-fx-border-width: 0; -fx-cursor: hand; -fx-padding: 8 16;";

        tabSignIn.setStyle(   activeTab == tabSignIn   ? active : inactive);
        tabRegister.setStyle( activeTab == tabRegister ? active : inactive);

        if (newMode == Mode.SIGN_IN) {
            formTitle.setText("Welcome back");
            formSubtitle.setText("Sign in to your SmartStore account");
            mainBtn.setText("Sign In");
            mainBtn.setStyle(primaryBtnStyle("#6366f1"));
            confirmRow.setVisible(false); confirmRow.setManaged(false);
            staffInfo.setVisible(false);  staffInfo.setManaged(false);
        } else {
            formTitle.setText("Create Your Account");
            formSubtitle.setText("Register as a customer — free & instant");
            mainBtn.setText("Create Account");
            mainBtn.setStyle(primaryBtnStyle("#22c55e"));
            confirmRow.setVisible(true);  confirmRow.setManaged(true);
            staffInfo.setVisible(true);   staffInfo.setManaged(true);
        }
    }

    // ── Form submission ───────────────────────────────────────

    private void handleSubmit() {
        clearMessages();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (mode == Mode.SIGN_IN) {
            if (username.isEmpty() || password.isEmpty()) {
                showMsg("Please enter your username and password.", "error"); return;
            }
            User user = auth.login(username, password);
            if (user != null) {
                showMsg("Welcome back, " + user.getUsername() + "! ✓", "success");
                PauseTransition pause = new PauseTransition(Duration.millis(650));
                pause.setOnFinished(e -> onLoginSuccess.accept(user));
                pause.play();
            } else {
                showMsg("Invalid username or password.", "error");
                shake(usernameField);
            }
        } else {
            // REGISTER_CUSTOMER
            String confirm = confirmField.getText();
            String error = auth.registerCustomer(username, password, confirm);
            if (error == null) {
                showMsg("Account created! You can now sign in. ✓", "success");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
                pause.setOnFinished(e -> switchMode(Mode.SIGN_IN, tabSignIn, "#6366f1",
                        findStaffInfo()));
                pause.play();
            } else {
                showMsg(error, "error");
                shake(usernameField);
            }
        }
    }

    private HBox findStaffInfo() {
        // Dummy — staffInfo is hidden in SIGN_IN mode already
        return new HBox();
    }

    // ── Helpers ───────────────────────────────────────────────

    private void showMsg(String msg, String type) {
        formMsg.setText(type.equals("error") ? "⚠  " + msg : "✓  " + msg);
        if (type.equals("error")) {
            formMsg.setStyle("-fx-background-color: #3b0d0d; -fx-text-fill: #f87171; " +
                    "-fx-padding: 10 14; -fx-background-radius: 8; -fx-font-size: 12px;");
        } else {
            formMsg.setStyle("-fx-background-color: #052e16; -fx-text-fill: #4ade80; " +
                    "-fx-padding: 10 14; -fx-background-radius: 8; -fx-font-size: 12px;");
        }
        formMsg.setVisible(true);
        formMsg.setManaged(true);
    }

    private void clearMessages() {
        formMsg.setVisible(false);
        formMsg.setManaged(false);
    }

    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), node);
        tt.setByX(9); tt.setCycleCount(6); tt.setAutoReverse(true); tt.play();
    }

    private Button tabBtn(String text, boolean active) {
        Button btn = new Button(text);
        String style = active
            ? "-fx-background-color: #6366f122; -fx-text-fill: #6366f1; -fx-font-size: 12px; " +
              "-fx-font-weight: bold; -fx-background-radius: 8; -fx-border-width: 0; -fx-cursor: hand; -fx-padding: 8 16;"
            : "-fx-background-color: transparent; -fx-text-fill: #484f58; -fx-font-size: 12px; " +
              "-fx-font-weight: normal; -fx-background-radius: 8; -fx-border-width: 0; -fx-cursor: hand; -fx-padding: 8 16;";
        btn.setStyle(style);
        return btn;
    }

    private VBox fieldBlock(String label, javafx.scene.Node field) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px; -fx-font-weight: bold;");
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(inputStyle());
        return tf;
    }

    private String inputStyle() {
        return "-fx-background-color: #0d1117; -fx-text-fill: #e6edf3; " +
               "-fx-prompt-text-fill: #30363d; -fx-border-color: #30363d; " +
               "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1; " +
               "-fx-padding: 11 14; -fx-font-size: 13px;";
    }

    private String primaryBtnStyle(String color) {
        return "-fx-background-color: " + color + "; -fx-text-fill: white; " +
               "-fx-font-size: 14px; -fx-font-weight: bold; " +
               "-fx-background-radius: 10; -fx-cursor: hand; -fx-border-width: 0;";
    }

    public BorderPane getRoot() { return root; }
}
