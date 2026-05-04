package com.store.controller;

import com.store.auth.AuthManager;
import com.store.auth.Role;
import com.store.auth.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * UserManagementController — Admin-only screen.
 * Allows admin to view, register, and delete system users.
 * Role-based: only accessible when currentUser.isAdmin() == true.
 */
public class UserManagementController {

    private final AuthManager auth;
    private final User        currentUser;
    private final SplitPane   view;

    private TableView<User>      table;
    private ObservableList<User> tableData;
    private Label                statusLabel;

    public UserManagementController(AuthManager auth, User currentUser) {
        this.auth        = auth;
        this.currentUser = currentUser;
        this.tableData   = FXCollections.observableArrayList(auth.getAllUsers());
        this.view        = buildView();
    }

    private SplitPane buildView() {
        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color: #0f1117; -fx-border-width: 0;");
        split.setDividerPositions(0.62);
        split.getItems().addAll(buildLeftPanel(), buildRightPanel());
        return split;
    }

    // ── LEFT: User Table ──────────────────────────────────────

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.setStyle("-fx-background-color: #0f1117; -fx-padding: 28 20 28 28;");

        Label title    = new Label("User Management");
        Label subtitle = new Label("Admin-only: manage system accounts and roles");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");
        subtitle.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        // Admin warning badge
        HBox badge = new HBox(8);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(8, 14, 8, 14));
        badge.setStyle("-fx-background-color: #1e1b4b; -fx-background-radius: 8;");
        Label badgeIcon = new Label("🔐");
        Label badgeText = new Label("This section is restricted to Administrators only");
        badgeText.setStyle("-fx-text-fill: #a5b4fc; -fx-font-size: 12px;");
        badge.getChildren().addAll(badgeIcon, badgeText);

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setStyle("-fx-background-color: #22263a; -fx-text-fill: #e2e8f0; " +
                "-fx-prompt-text-fill: #475569; -fx-border-color: #2d3148; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1; -fx-padding: 8 12;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button btnDelete = new Button("Delete User");
        btnDelete.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: #fca5a5; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");

        toolbar.getChildren().addAll(searchField, btnDelete);

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        panel.getChildren().addAll(new VBox(4, title, subtitle), badge, toolbar, statusLabel, table);

        // Events
        searchField.textProperty().addListener((obs, old, val) -> {
            String q = val.trim().toLowerCase();
            if (q.isEmpty()) { tableData.setAll(auth.getAllUsers()); return; }
            tableData.setAll(auth.getAllUsers().stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(q)
                              || u.getRole().name().toLowerCase().contains(q))
                    .toList());
        });

        btnDelete.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showStatus("Select a user to delete.", "error");
                return;
            }
            if (selected.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
                showStatus("You cannot delete your own account.", "error");
                return;
            }
            boolean ok = auth.deleteUser(selected.getUsername());
            if (ok) {
                tableData.setAll(auth.getAllUsers());
                showStatus("User deleted: " + selected.getUsername(), "success");
            } else {
                showStatus("Failed to delete user.", "error");
            }
        });

        return panel;
    }

    private TableView<User> buildTable() {
        TableView<User> tv = new TableView<>(tableData);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setStyle("-fx-background-color: #1a1d27; -fx-border-color: #2d3148; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        tv.setPlaceholder(new Label("No users found."));

        TableColumn<User, String> colUser    = col("Username",   d -> new SimpleStringProperty(d.getValue().getUsername()));
        TableColumn<User, String> colRole    = col("Role",       d -> new SimpleStringProperty(d.getValue().getRole().name()));
        TableColumn<User, String> colCreated = col("Created At", d -> new SimpleStringProperty(d.getValue().getCreatedAt()));

        // Color-code role
        colRole.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String style;
                switch (item) {
                    case "ADMIN":    style = "-fx-text-fill: #818cf8; -fx-font-weight: bold;"; break;
                    case "STAFF":    style = "-fx-text-fill: #fbbf24; -fx-font-weight: bold;"; break;
                    case "CUSTOMER": style = "-fx-text-fill: #4ade80; -fx-font-weight: bold;"; break;
                    default:         style = "-fx-text-fill: #8b949e;";
                }
                setStyle(style);
            }
        });

        tv.getColumns().addAll(colUser, colRole, colCreated);
        return tv;
    }

    // ── RIGHT: Register new user form ─────────────────────────

    private VBox buildRightPanel() {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: #0f1117; -fx-padding: 28;");
        panel.setMinWidth(300);

        Label title = new Label("Register New User");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");

        VBox card = new VBox(16);
        card.setStyle("-fx-background-color: #1a1d27; -fx-background-radius: 12; " +
                "-fx-border-color: #2d3148; -fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 20;");

        TextField usernameF = input("Username (min 3 chars)");
        PasswordField passwordF = new PasswordField();
        passwordF.setPromptText("Password (min 6 chars)");
        passwordF.setStyle(inputStyle());
        PasswordField confirmF = new PasswordField();
        confirmF.setPromptText("Confirm Password");
        confirmF.setStyle(inputStyle());

        // Admin panel only creates Staff or Admin accounts.
        // Customers self-register on the Login screen.
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Staff", "Admin");
        roleBox.setValue("Staff");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        HBox noteBox = new HBox(8);
        noteBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        noteBox.setPadding(new Insets(8, 12, 8, 12));
        noteBox.setStyle("-fx-background-color: #0d2137; -fx-background-radius: 8; " +
                "-fx-border-color: #1d4ed8; -fx-border-width: 1; -fx-border-radius: 8;");
        Label noteIcon = new Label("ℹ");
        noteIcon.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 13px;");
        Label noteTxt = new Label("Customers self-register on the Login screen.");
        noteTxt.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 12px;");
        noteBox.getChildren().addAll(noteIcon, noteTxt);

        Button btnRegister = new Button("Create Account");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        Label msg = new Label();
        msg.setWrapText(true);
        msg.setMaxWidth(Double.MAX_VALUE);

        btnRegister.setOnAction(e -> {
            Role role = roleBox.getValue().equalsIgnoreCase("Admin") ? Role.ADMIN : Role.STAFF;
            String error = auth.register(usernameF.getText().trim(),
                    passwordF.getText(), confirmF.getText(), role);
            if (error == null) {
                tableData.setAll(auth.getAllUsers());
                ok(msg, "User registered: " + usernameF.getText().trim());
                usernameF.clear(); passwordF.clear(); confirmF.clear();
            } else {
                err(msg, error);
            }
        });

        card.getChildren().addAll(
            lf("Username", usernameF),
            lf("Password", passwordF),
            lf("Confirm Password", confirmF),
            lf("Role", roleBox),
            noteBox,
            btnRegister, msg
        );

        panel.getChildren().addAll(title, card);
        HBox.setHgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────

    private TableColumn<User, String> col(String title,
            javafx.util.Callback<TableColumn.CellDataFeatures<User, String>,
            javafx.beans.value.ObservableValue<String>> fn) {
        TableColumn<User, String> c = new TableColumn<>(title);
        c.setCellValueFactory(fn);
        c.setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold;");
        return c;
    }

    private TextField input(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(inputStyle());
        return tf;
    }

    private String inputStyle() {
        return "-fx-background-color: #22263a; -fx-text-fill: #e2e8f0; " +
               "-fx-prompt-text-fill: #475569; -fx-border-color: #2d3148; " +
               "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1; -fx-padding: 9 12;";
    }

    private VBox lf(String label, javafx.scene.Node input) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        if (input instanceof TextField) ((TextField)input).setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, input);
        return box;
    }

    private void ok(Label lbl, String msg) {
        lbl.setText("✓  " + msg);
        lbl.setStyle("-fx-background-color: #052e16; -fx-text-fill: #86efac; " +
                "-fx-padding: 8 12; -fx-background-radius: 8;");
    }

    private void err(Label lbl, String msg) {
        lbl.setText("⚠  " + msg);
        lbl.setStyle("-fx-background-color: #450a0a; -fx-text-fill: #fca5a5; " +
                "-fx-padding: 8 12; -fx-background-radius: 8;");
    }

    private void showStatus(String msg, String type) {
        if (type.equals("success")) ok(statusLabel, msg);
        else err(statusLabel, msg);
    }

    public SplitPane getView() { return view; }
}
