package com.store.ui;

import com.store.auth.AuthManager;
import com.store.auth.Role;
import com.store.auth.User;
import com.store.component.ProductManager;
import com.store.controller.*;
import com.store.util.AppLogger;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * ============================================================
 *  MAIN VIEW  — Post-login shell
 *
 *  Routes to the correct dashboard based on role:
 *    CUSTOMER → CustomerDashboardController (own shop/wallet/orders)
 *    STAFF    → Staff operations shell
 *    ADMIN    → Full admin shell
 * ============================================================
 */
public class MainView {

    private final BorderPane root;
    private final AuthManager auth;
    private final User        currentUser;
    private final Runnable    onLogout;

    public MainView(AuthManager auth, User currentUser, Runnable onLogout) {
        this.auth        = auth;
        this.currentUser = currentUser;
        this.onLogout    = onLogout;

        AppLogger.info("MainView loading for user: " + currentUser.getUsername()
                + " [" + currentUser.getRole() + "]");

        this.root = new BorderPane();

        if (currentUser.isCustomer()) {
            buildCustomerShell();
        } else {
            buildStaffAdminShell();
        }

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ── Customer shell ────────────────────────────────────────

    private void buildCustomerShell() {
        ProductManager manager = new ProductManager("SmartStore");
        CustomerDashboardController customerCtrl = new CustomerDashboardController(manager, auth, currentUser);

        // Customer gets a thin top bar for logout only
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 16, 10, 16));
        topBar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #21262d; -fx-border-width: 0 0 0 0;");

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setStyle("-fx-background-color: #1e0a0a; -fx-text-fill: #ef4444; " +
                "-fx-border-width: 0; -fx-cursor: hand; -fx-font-size: 12px; " +
                "-fx-background-radius: 8; -fx-padding: 6 14;");
        logoutBtn.setOnAction(e -> { auth.logout(); onLogout.run(); });
        topBar.getChildren().add(logoutBtn);

        root.setCenter(customerCtrl.getRoot());
    }

    // ── Staff / Admin shell ───────────────────────────────────

    private void buildStaffAdminShell() {
        ProductManager manager = new ProductManager("SmartStore");

        DashboardController         dashboardCtrl = new DashboardController(manager, currentUser);
        ProductController           productCtrl   = new ProductController(manager, currentUser);
        CustomerController          customerCtrl  = new CustomerController(manager);
        SalesController             salesCtrl     = new SalesController(manager);
        LogController               logCtrl       = new LogController(manager);
        UserManagementController    userCtrl      = new UserManagementController(auth, currentUser);

        root.setLeft(buildSidebar(dashboardCtrl, productCtrl, customerCtrl, salesCtrl, logCtrl, userCtrl));
        root.setCenter(dashboardCtrl.getView());
    }

    private VBox buildSidebar(DashboardController dashCtrl, ProductController prodCtrl,
                               CustomerController custCtrl, SalesController salesCtrl,
                               LogController logCtrl, UserManagementController userCtrl) {

        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #0d1117; -fx-min-width: 230px; -fx-max-width: 230px; " +
                "-fx-border-color: #21262d; -fx-border-width: 0 1 0 0;");

        // ── Store header ──────────────────────────────────────
        VBox header = new VBox(4);
        header.setPadding(new Insets(20, 16, 16, 16));
        header.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");

        Label storeName = new Label("🛍 SmartStore");
        storeName.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        String roleLabel = currentUser.isAdmin() ? "Admin Panel" : "Staff Panel";
        Label storeSub  = new Label(roleLabel);
        storeSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " +
                (currentUser.isAdmin() ? "#6366f1" : "#f59e0b") + "; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        header.getChildren().addAll(storeName, storeSub);

        // ── User info ─────────────────────────────────────────
        HBox userInfo = new HBox(10);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        userInfo.setPadding(new Insets(14, 16, 14, 16));
        userInfo.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");

        StackPane avatar = new StackPane();
        Circle avatarBg = new Circle(18);
        avatarBg.setFill(currentUser.isAdmin() ? Color.web("#6366f1") : Color.web("#f59e0b"));
        Label initials = new Label(String.valueOf(currentUser.getUsername().charAt(0)).toUpperCase());
        initials.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarBg, initials);

        VBox userMeta = new VBox(2);
        Label uname = new Label(currentUser.getUsername());
        uname.setStyle("-fx-text-fill: #e6edf3; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label roleBadge = new Label(currentUser.isAdmin() ? "● Admin" : "● Staff");
        roleBadge.setStyle("-fx-text-fill: " + (currentUser.isAdmin() ? "#818cf8" : "#fbbf24") + "; -fx-font-size: 11px;");
        userMeta.getChildren().addAll(uname, roleBadge);
        userInfo.getChildren().addAll(avatar, userMeta);

        // ── Navigation ────────────────────────────────────────
        VBox navSection = new VBox(2);
        navSection.setPadding(new Insets(8, 0, 8, 0));

        Label navLabel = new Label("NAVIGATION");
        navLabel.setStyle("-fx-text-fill: #21262d; -fx-font-size: 9px; -fx-font-weight: bold; " +
                "-fx-padding: 8 16 4 16; -fx-letter-spacing: 1.5px;");

        Button[] activeHolder = {null};

        Button btnDash = navBtn("📈  Dashboard", activeHolder);
        Button btnProd = navBtn("📦  Products",  activeHolder);
        setActive(btnDash, activeHolder);

        navSection.getChildren().addAll(navLabel, btnDash, btnProd);

        if (currentUser.isAdmin()) {
            Button btnCust  = navBtn("👤  Customers",    activeHolder);
            Button btnSales = navBtn("💳  Sales",         activeHolder);

            Label adminLabel = new Label("ADMIN");
            adminLabel.setStyle("-fx-text-fill: #21262d; -fx-font-size: 9px; -fx-font-weight: bold; " +
                    "-fx-padding: 12 16 4 16; -fx-letter-spacing: 1.5px;");

            Button btnLog   = navBtn("📋  Logs & Export",   activeHolder);
            Button btnUsers = navBtn("🔐  User Management", activeHolder);

            navSection.getChildren().addAll(btnCust, btnSales, adminLabel, btnLog, btnUsers);

            btnDash.setOnAction(e  -> { setActive(btnDash, activeHolder);  root.setCenter(dashCtrl.getView()); });
            btnProd.setOnAction(e  -> { setActive(btnProd, activeHolder);  root.setCenter(prodCtrl.getView()); });
            btnCust.setOnAction(e  -> { setActive(btnCust, activeHolder);  root.setCenter(custCtrl.getView()); });
            btnSales.setOnAction(e -> { setActive(btnSales, activeHolder); root.setCenter(salesCtrl.getView()); });
            btnLog.setOnAction(e   -> { setActive(btnLog, activeHolder);   root.setCenter(logCtrl.getView()); });
            btnUsers.setOnAction(e -> { setActive(btnUsers, activeHolder); root.setCenter(userCtrl.getView()); });
        } else {
            // Staff: Dashboard + Products + Sales
            Button btnSales = navBtn("💳  Sales", activeHolder);

            Label limitLabel = new Label("STAFF ACCESS");
            limitLabel.setStyle("-fx-text-fill: #21262d; -fx-font-size: 9px; -fx-font-weight: bold; " +
                    "-fx-padding: 12 16 4 16; -fx-letter-spacing: 1.5px;");
            Label limitNote = new Label("Contact admin for\nfull system access");
            limitNote.setStyle("-fx-text-fill: #21262d; -fx-font-size: 11px; -fx-padding: 4 16;");

            navSection.getChildren().addAll(btnSales, limitLabel, limitNote);

            btnDash.setOnAction(e  -> { setActive(btnDash, activeHolder);  root.setCenter(dashCtrl.getView()); });
            btnProd.setOnAction(e  -> { setActive(btnProd, activeHolder);  root.setCenter(prodCtrl.getView()); });
            btnSales.setOnAction(e -> { setActive(btnSales, activeHolder); root.setCenter(salesCtrl.getView()); });
        }

        // ── Logout ────────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Sign Out");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-background-color: #1a0a0a; -fx-text-fill: #ef4444; " +
                "-fx-border-width: 0; -fx-padding: 12 20; -fx-alignment: CENTER_LEFT; " +
                "-fx-cursor: hand; -fx-font-size: 13px;");
        btnLogout.setOnAction(e -> { auth.logout(); onLogout.run(); });

        Label version = new Label("v2.1.0  ·  3-Role System");
        version.setStyle("-fx-text-fill: #21262d; -fx-font-size: 10px; -fx-padding: 4 16 14 16;");

        sidebar.getChildren().addAll(header, userInfo, new Separator(), navSection, spacer, btnLogout, version);
        return sidebar;
    }

    private Button navBtn(String text, Button[] activeHolder) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; " +
                "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 20; " +
                "-fx-border-width: 0; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> {
            if (btn != activeHolder[0])
                btn.setStyle(btn.getStyle()
                        .replace("-fx-text-fill: #8b949e", "-fx-text-fill: #e6edf3")
                        .replace("-fx-background-color: transparent", "-fx-background-color: #161b24"));
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeHolder[0])
                btn.setStyle(btn.getStyle()
                        .replace("-fx-text-fill: #e6edf3", "-fx-text-fill: #8b949e")
                        .replace("-fx-background-color: #161b24", "-fx-background-color: transparent"));
        });
        return btn;
    }

    private void setActive(Button btn, Button[] activeHolder) {
        if (activeHolder[0] != null) {
            activeHolder[0].setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; " +
                    "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 20; " +
                    "-fx-border-width: 0; -fx-cursor: hand;");
        }
        activeHolder[0] = btn;
        btn.setStyle("-fx-background-color: #1e2030; -fx-text-fill: #818cf8; " +
                "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 17 11 17; " +
                "-fx-border-color: #6366f1; -fx-border-width: 0 0 0 3; -fx-cursor: hand;");
    }

    public BorderPane getRoot() { return root; }
}
