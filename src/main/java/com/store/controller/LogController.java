package com.store.controller;

import com.store.component.ProductManager;
import com.store.util.AppLogger;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * ============================================================
 *  LOG CONTROLLER  (Bonus Feature)
 * ============================================================
 *  Shows the Application Log (data/app.log) and provides
 *  CSV Export for Products and Customers.
 *
 *  UI only — no business logic.
 *  Capture input → call backend → display output.
 * ============================================================
 */
public class LogController {

    private final ProductManager manager;
    private final HBox view;

    public LogController(ProductManager manager) {
        this.manager = manager;
        this.view    = buildView();
    }

    private HBox buildView() {
        HBox layout = new HBox(0);
        layout.setStyle("-fx-background-color: #0f1117;");
        layout.getChildren().addAll(buildLogPanel(), buildExportPanel());
        return layout;
    }

    // ── LEFT: Application Log viewer ──────────────────────

    private VBox buildLogPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28));
        HBox.setHgrow(panel, Priority.ALWAYS);

        Label title    = new Label("Application Log");
        Label subtitle = new Label("Real-time log of all operations — data/app.log");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        TextArea logArea = new TextArea(AppLogger.readLog());
        logArea.setEditable(false);
        logArea.getStyleClass().add("log-area");
        logArea.setStyle(logArea.getStyle() + " -fx-text-fill: #a5f3fc;");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        HBox btnRow = new HBox(10);
        Button btnRefresh = new Button("Refresh Log");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRefresh, Priority.ALWAYS);

        Button btnClear = new Button("Clear Log");
        btnClear.getStyleClass().add("btn-danger");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnClear, Priority.ALWAYS);

        btnRefresh.setOnAction(e -> logArea.setText(AppLogger.readLog()));

        btnClear.setOnAction(e -> {
            AppLogger.clearLog();
            logArea.setText(AppLogger.readLog());
        });

        btnRow.getChildren().addAll(btnRefresh, btnClear);

        // Log level legend
        HBox legend = new HBox(16);
        legend.getChildren().addAll(
            legendDot("SUCCESS", "#86efac"),
            legendDot("INFO",    "#7dd3fc"),
            legendDot("WARN",    "#fbbf24"),
            legendDot("ERROR",   "#f87171")
        );

        panel.getChildren().addAll(
            new VBox(4, title, subtitle),
            legend,
            logArea,
            btnRow
        );
        return panel;
    }

    private HBox legendDot(String label, String color) {
        HBox box = new HBox(5);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 10px;");
        Label txt = new Label(label);
        txt.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        box.getChildren().addAll(dot, txt);
        return box;
    }

    // ── RIGHT: CSV Export panel ───────────────────────────

    private VBox buildExportPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 28, 28, 16));
        panel.setPrefWidth(460);
        panel.setMinWidth(360);

        Label title    = new Label("Data Export");
        Label subtitle = new Label("Export inventory & customers as CSV");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        // ── Products Export ──────────────────────────────
        VBox prodCard = new VBox(10);
        prodCard.getStyleClass().add("card");

        Label prodTitle = new Label("Export Products Inventory");
        prodTitle.getStyleClass().add("card-title");

        Label prodDesc = new Label("Generates a CSV with all product details:\nType, ID, Name, Category, Price, Discount, Final Price, Stock, Extra Info");
        prodDesc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        prodDesc.setWrapText(true);

        TextArea prodArea = new TextArea();
        prodArea.setEditable(false);
        prodArea.getStyleClass().add("log-area");
        prodArea.setPrefRowCount(6);
        prodArea.setPromptText("Click Export to generate CSV...");

        Button btnExportProd = new Button("Export Products CSV");
        btnExportProd.getStyleClass().add("btn-primary");
        btnExportProd.setMaxWidth(Double.MAX_VALUE);
        btnExportProd.setOnAction(e -> {
            String csv = manager.exportInventoryCsv();
            prodArea.setText(csv);
        });

        prodCard.getChildren().addAll(prodTitle, prodDesc, prodArea, btnExportProd);

        // ── Customers Export ─────────────────────────────
        VBox custCard = new VBox(10);
        custCard.getStyleClass().add("card");

        Label custTitle = new Label("Export Customer Data");
        custTitle.getStyleClass().add("card-title");

        Label custDesc = new Label("Generates a CSV with all customer records:\nID, Name, Email, Wallet Balance");
        custDesc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        custDesc.setWrapText(true);

        TextArea custArea = new TextArea();
        custArea.setEditable(false);
        custArea.getStyleClass().add("log-area");
        custArea.setPrefRowCount(4);
        custArea.setPromptText("Click Export to generate CSV...");

        Button btnExportCust = new Button("Export Customers CSV");
        btnExportCust.getStyleClass().add("btn-success");
        btnExportCust.setMaxWidth(Double.MAX_VALUE);
        btnExportCust.setOnAction(e -> {
            String csv = manager.exportCustomersCsv();
            custArea.setText(csv);
        });

        custCard.getChildren().addAll(custTitle, custDesc, custArea, btnExportCust);

        VBox.setVgrow(prodCard, Priority.ALWAYS);
        panel.getChildren().addAll(
            new VBox(4, title, subtitle),
            prodCard,
            custCard
        );
        return panel;
    }

    public HBox getView() { return view; }
}
