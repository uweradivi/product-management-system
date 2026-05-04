package com.store.controller;

import com.store.component.Customer;
import com.store.component.ProductManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * CustomerController — connects Customer UI to the ProductManager backend.
 * UI only: captures input → calls backend → displays output.
 */
public class CustomerController {

    private final ProductManager manager;
    private final SplitPane view;

    private TableView<Customer> table;
    private ObservableList<Customer> tableData;
    private Label statusLabel;

    public CustomerController(ProductManager manager) {
        this.manager   = manager;
        this.tableData = FXCollections.observableArrayList(manager.getCustomers());
        this.view      = buildView();
    }

    private SplitPane buildView() {
        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color: #0f1117; -fx-border-width: 0;");
        split.setDividerPositions(0.62);
        split.getItems().addAll(buildLeftPanel(), buildRightPanel());
        return split;
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 20, 28, 28));

        Label title    = new Label("Customers");
        Label subtitle = new Label("View and manage registered customers");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        // Search toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, ID or email...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button btnRemove = new Button("Remove Selected");
        btnRemove.getStyleClass().add("btn-danger");

        toolbar.getChildren().addAll(searchField, btnRemove);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        panel.getChildren().addAll(
                new VBox(4, title, subtitle),
                toolbar,
                statusLabel,
                table
        );

        // Events
        searchField.textProperty().addListener((obs, old, val) -> {
            tableData.setAll(manager.searchCustomers(val));
        });

        btnRemove.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showStatus("Please select a customer to remove.", "error");
                return;
            }
            boolean ok = manager.removeCustomer(selected.getCustomerId());
            if (ok) {
                tableData.remove(selected);
                showStatus("Removed: " + selected.getName(), "success");
            } else {
                showStatus("Could not remove customer.", "error");
            }
        });

        return panel;
    }

    private TableView<Customer> buildTable() {
        TableView<Customer> tv = new TableView<>(tableData);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No customers found."));

        TableColumn<Customer, String> colId     = new TableColumn<>("ID");
        TableColumn<Customer, String> colName   = new TableColumn<>("Name");
        TableColumn<Customer, String> colEmail  = new TableColumn<>("Email");
        TableColumn<Customer, String> colWallet = new TableColumn<>("Wallet Balance");

        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerId()));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colWallet.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%.2f", d.getValue().getWalletBalance())));

        // Color wallet balance
        colWallet.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                double amount = Double.parseDouble(item.replace("$", "").replace(",", ""));
                if (amount < 10)   setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                else if (amount < 50) setStyle("-fx-text-fill: #f59e0b;");
                else               setStyle("-fx-text-fill: #86efac;");
            }
        });

        tv.getColumns().addAll(colId, colName, colEmail, colWallet);
        return tv;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 28, 28, 16));

        Label title = new Label("Customer Actions");
        title.getStyleClass().add("page-title");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(buildRegisterTab(), buildTopUpTab());
        VBox.setVgrow(tabs, Priority.ALWAYS);

        panel.getChildren().addAll(title, tabs);
        return panel;
    }

    private Tab buildRegisterTab() {
        Tab tab = new Tab("Register Customer");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id     = field("Customer ID (e.g. C001)");
        TextField name   = field("Full Name");
        TextField email  = field("Email Address");
        TextField wallet = field("Initial Wallet Balance ($)");

        Button btn = new Button("Register Customer");
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                if (id.getText().trim().isEmpty() || name.getText().trim().isEmpty()
                        || email.getText().trim().isEmpty()) {
                    msg.setText("All fields are required.");
                    msg.getStyleClass().setAll("msg-error");
                    return;
                }
                double walletAmt = Double.parseDouble(wallet.getText().trim());
                Customer c = new Customer(
                        id.getText().trim(), name.getText().trim(),
                        email.getText().trim(), walletAmt);
                boolean ok = manager.addCustomer(c);
                if (ok) {
                    tableData.setAll(manager.getCustomers());
                    showStatus("Registered: " + c.getName(), "success");
                    msg.setText("Customer registered successfully!");
                    msg.getStyleClass().setAll("msg-success");
                    clearFields(id, name, email, wallet);
                } else {
                    msg.setText("Customer ID already exists.");
                    msg.getStyleClass().setAll("msg-error");
                }
            } catch (NumberFormatException ex) {
                msg.setText("Invalid wallet amount.");
                msg.getStyleClass().setAll("msg-error");
            } catch (Exception ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Customer ID", id),
                labeledField("Full Name", name),
                labeledField("Email Address", email),
                labeledField("Initial Wallet ($)", wallet),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    private Tab buildTopUpTab() {
        Tab tab = new Tab("Top Up Wallet");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id = field("Customer ID");
        Spinner<Double> amount = new Spinner<>(1.0, 100000.0, 50.0, 10.0);
        amount.setEditable(true);
        amount.setMaxWidth(Double.MAX_VALUE);

        Button btn = new Button("Top Up Wallet");
        btn.getStyleClass().add("btn-success");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                manager.topUpCustomerWallet(id.getText().trim(), amount.getValue());
                tableData.setAll(manager.getCustomers());
                showStatus("Wallet topped up successfully!", "success");
                msg.setText("Wallet topped up!");
                msg.getStyleClass().setAll("msg-success");
                clearFields(id);
            } catch (Exception ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Customer ID", id),
                labeledField("Amount to Add ($)", amount),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    // Helpers
    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        return tf;
    }

    private VBox labeledField(String label, javafx.scene.Node input) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("input-label");
        if (input instanceof TextField) ((TextField)input).setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, input);
        return box;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    private void showStatus(String msg, String type) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().setAll(type.equals("success") ? "msg-success" :
                                            type.equals("error")   ? "msg-error"   : "msg-info");
    }

    public SplitPane getView() { return view; }
}
