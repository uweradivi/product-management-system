package com.store.ui;

import com.store.auth.AuthManager;
import com.store.auth.User;
import com.store.util.AppLogger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ============================================================
 *  MAIN APPLICATION ENTRY POINT
 *
 *  Flow:
 *    1. Show LoginView  → Sign In OR Register as Customer
 *    2. On success:
 *       CUSTOMER → CustomerDashboard (browse, buy, wallet)
 *       STAFF    → Staff operations shell
 *       ADMIN    → Full admin shell
 *    3. On logout → back to LoginView
 * ============================================================
 */
public class MainApp extends Application {

    private Stage       primaryStage;
    private AuthManager auth;
    private String      css;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.auth         = new AuthManager();
        this.css          = getClass().getResource("/com/store/css/style.css").toExternalForm();

        AppLogger.info("=== SmartStore v2.1 Starting ===");
        showLogin();

        stage.setTitle("SmartStore — Sign In");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(auth, this::onLoginSuccess);
        Scene scene = new Scene(loginView.getRoot(), 1100, 680);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SmartStore — Sign In");
    }

    private void onLoginSuccess(User user) {
        String roleLabel = user.isAdmin() ? "Admin" : user.isStaff() ? "Staff" : "Customer";
        MainView mainView = new MainView(auth, user, this::showLogin);
        Scene scene = new Scene(mainView.getRoot(), 1200, 760);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SmartStore — " + user.getUsername() + " [" + roleLabel + "]");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
