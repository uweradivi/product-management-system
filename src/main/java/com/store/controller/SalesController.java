package com.store.controller;

import com.store.component.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * SalesController — connects the Sales UI to the backend.
 * Handles sale processing and sales log display.
 * No business logic here — all validation in ProductManager and Customer.
 */
public class SalesController {

    private final ProductManager manager;
    private final HBox view;

    public SalesController(ProductManager manager) {
        this.manager = manager;
        this.view    = buildView();
    }

    private HBox buildView() {
        HBox layout = new HBox(0);
        layout.setStyle("-fx-background-color: #0f1117;");
        layout.getChildren().addAll(buildSaleForm(), buildLogPanel());
        return layout;
    }

    // ── LEFT: Sale form ───────────────────────────────────────

    private VBox buildSaleForm() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28));
        panel.setPrefWidth(480);
        panel.setMinWidth(380);

        Label title    = new Label("Process a Sale");
        Label subtitle = new Label("Select customer and product to complete a sale");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        // Form
        TextField custId  = field("Customer ID (e.g. C001)");
        TextField prodId  = field("Product ID (e.g. P001)");
        Spinner<Integer> qty = new Spinner<>(1, 10000, 1);
        qty.setEditable(true);
        qty.setMaxWidth(Double.MAX_VALUE);

        // Preview area
        VBox previewCard = new VBox(8);
        previewCard.getStyleClass().add("card");
        previewCard.setStyle("-fx-background-color: #1a1d27; -fx-background-radius: 12; " +
                "-fx-border-color: #2d3148; -fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 16;");

        Label previewTitle = new Label("Sale Preview");
        previewTitle.getStyleClass().add("card-title");

        Label previewContent = new Label("Fill in the fields to see a preview.");
        previewContent.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
        previewContent.setWrapText(true);

        previewCard.getChildren().addAll(previewTitle, previewContent);

        // Preview button
        Button btnPreview = new Button("Preview Sale");
        btnPreview.getStyleClass().add("btn-secondary");
        btnPreview.setMaxWidth(Double.MAX_VALUE);

        btnPreview.setOnAction(e -> {
            try {
                Customer c = manager.findCustomerById(custId.getText().trim());
                Product  p = manager.findProductById(prodId.getText().trim());
                int      q = qty.getValue();

                if (c == null) {
                    previewContent.setText("Customer not found: " + custId.getText().trim());
                    previewContent.setStyle("-fx-text-fill: #ef4444;");
                    return;
                }
                if (p == null) {
                    previewContent.setText("Product not found: " + prodId.getText().trim());
                    previewContent.setStyle("-fx-text-fill: #ef4444;");
                    return;
                }

                double finalPrice = p.getPrice() - p.applyDiscount();
                double total      = finalPrice * q;
                boolean canAfford = c.getWalletBalance() >= total;
                boolean inStock   = p.getStockQuantity() >= q;

                StringBuilder sb = new StringBuilder();
                sb.append("Customer : ").append(c.getName()).append("\n");
                sb.append("Product  : ").append(p.getName()).append(" [").append(p.getCategory()).append("]\n");
                sb.append("Qty      : ").append(q).append(" units\n");
                sb.append(String.format("Price    : $%.2f  →  after discount: $%.2f%n", p.getPrice(), finalPrice));
                sb.append(String.format("Total    : $%.2f%n", total));
                sb.append(String.format("Wallet   : $%.2f  →  remaining: $%.2f%n",
                        c.getWalletBalance(), c.getWalletBalance() - total));
                sb.append("Stock    : ").append(p.getStockQuantity()).append(" available\n");

                if (!canAfford) sb.append("\n[!] Insufficient wallet balance!");
                if (!inStock)   sb.append("\n[!] Not enough stock!");

                previewContent.setText(sb.toString());
                previewContent.setStyle(canAfford && inStock
                        ? "-fx-text-fill: #86efac; -fx-font-family: monospace; -fx-font-size: 12px;"
                        : "-fx-text-fill: #fca5a5; -fx-font-family: monospace; -fx-font-size: 12px;");

            } catch (Exception ex) {
                previewContent.setText(ex.getMessage());
                previewContent.setStyle("-fx-text-fill: #ef4444;");
            }
        });

        // Status
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        // Process button
        Button btnProcess = new Button("Process Sale");
        btnProcess.getStyleClass().add("btn-primary");
        btnProcess.setStyle("-fx-pref-height: 44px; -fx-font-size: 14px;");
        btnProcess.setMaxWidth(Double.MAX_VALUE);

        btnProcess.setOnAction(e -> {
            try {
                manager.processSale(custId.getText().trim(), prodId.getText().trim(), qty.getValue());
                statusLabel.setText("Sale processed successfully!");
                statusLabel.getStyleClass().setAll("msg-success");
                previewContent.setText("Sale completed! Check the log for details.");
                previewContent.setStyle("-fx-text-fill: #86efac;");
                custId.clear(); prodId.clear();
            } catch (OutOfStockException ex) {
                statusLabel.setText("Out of stock: " + ex.getMessage());
                statusLabel.getStyleClass().setAll("msg-error");
            } catch (InsufficientBalanceException ex) {
                statusLabel.setText("Insufficient balance: " + ex.getMessage());
                statusLabel.getStyleClass().setAll("msg-error");
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                statusLabel.getStyleClass().setAll("msg-error");
            }
        });

        panel.getChildren().addAll(
                new VBox(4, title, subtitle),
                labeledField("Customer ID", custId),
                labeledField("Product ID", prodId),
                labeledField("Quantity", qty),
                btnPreview,
                previewCard,
                btnProcess,
                statusLabel
        );

        HBox.setHgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ── RIGHT: Sales log ──────────────────────────────────────

    private VBox buildLogPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 28, 28, 16));
        panel.setPrefWidth(520);
        panel.setMinWidth(300);

        Label title    = new Label("Sales Log");
        Label subtitle = new Label("All completed transactions");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        TextArea logArea = new TextArea(manager.getSalesLog());
        logArea.setEditable(false);
        logArea.getStyleClass().add("log-area");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        Button btnRefresh = new Button("Refresh Log");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setMaxWidth(Double.MAX_VALUE);
        btnRefresh.setOnAction(e -> logArea.setText(manager.getSalesLog()));

        panel.getChildren().addAll(
                new VBox(4, title, subtitle),
                logArea,
                btnRefresh
        );

        HBox.setHgrow(panel, Priority.ALWAYS);
        return panel;
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

    public HBox getView() { return view; }
}
