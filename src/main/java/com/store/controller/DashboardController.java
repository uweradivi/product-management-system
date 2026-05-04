package com.store.controller;

import com.store.component.Product;
import com.store.component.ProductManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Dashboard Controller.
 * Displays summary stats, inventory value, and low-stock alerts.
 * Calls ProductManager to get data — no business logic here.
 */
public class DashboardController {

    private final ProductManager manager;
    private final ScrollPane view;

    public DashboardController(ProductManager manager, com.store.auth.User user) { this(manager); }
    public DashboardController(ProductManager manager) {
        this.manager = manager;
        this.view    = buildView();
    }

    private ScrollPane buildView() {
        VBox content = new VBox(24);
        content.getStyleClass().add("content-area");
        content.setPadding(new Insets(28));

        // Page header
        VBox header = new VBox(4);
        Label title    = new Label("Dashboard");
        Label subtitle = new Label("Welcome back! Here's your store overview.");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        // Stat cards row
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("Total Products",  String.valueOf(manager.getTotalProducts()),   "#6366f1"),
            statCard("Customers",       String.valueOf(manager.getTotalCustomers()),   "#22c55e"),
            statCard("Electronics",     String.valueOf(manager.countByCategory("Electronics")), "#818cf8"),
            statCard("Food & Grocery",  String.valueOf(manager.countByCategory("Food & Grocery")), "#34d399"),
            statCard("Clothing",        String.valueOf(manager.countByCategory("Clothing & Apparel")), "#ec4899")
        );
        for (Node n : statsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // Inventory value card
        HBox valueRow = new HBox(16);
        VBox valueCard = new VBox(8);
        valueCard.getStyleClass().add("card");
        Label valueTitle = new Label("Total Inventory Value");
        valueTitle.getStyleClass().add("card-title");
        Label valueAmount = new Label(String.format("$%,.2f", manager.getTotalInventoryValue()));
        valueAmount.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");
        Label valueSub = new Label("Calculated from stock quantity × price per unit");
        valueSub.getStyleClass().add("stat-label");
        valueCard.getChildren().addAll(valueTitle, valueAmount, valueSub);
        HBox.setHgrow(valueCard, Priority.ALWAYS);
        valueRow.getChildren().add(valueCard);

        // Low stock alerts
        VBox alertCard = new VBox(12);
        alertCard.getStyleClass().add("card");

        Label alertTitle = new Label("Low Stock Alerts  (< 5 units)");
        alertTitle.getStyleClass().add("card-title");

        List<Product> lowStock = manager.getLowStockProducts(5);
        if (lowStock.isEmpty()) {
            Label ok = new Label("All products have sufficient stock.");
            ok.getStyleClass().add("msg-success");
            ok.setMaxWidth(Double.MAX_VALUE);
            alertCard.getChildren().addAll(alertTitle, ok);
        } else {
            alertCard.getChildren().add(alertTitle);
            for (Product p : lowStock) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setStyle("-fx-background-color: #450a0a; -fx-background-radius: 8;");

                Label name = new Label(p.getName());
                name.setStyle("-fx-text-fill: #fca5a5; -fx-font-weight: bold;");

                Label id = new Label("[" + p.getProductId() + "]");
                id.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label stock = new Label("Only " + p.getStockQuantity() + " left!");
                stock.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

                row.getChildren().addAll(name, id, spacer, stock);
                alertCard.getChildren().add(row);
            }
        }

        // Sales log preview
        VBox logCard = new VBox(12);
        logCard.getStyleClass().add("card");
        Label logTitle = new Label("Recent Sales Log");
        logTitle.getStyleClass().add("card-title");

        TextArea logArea = new TextArea(manager.getSalesLog());
        logArea.setEditable(false);
        logArea.getStyleClass().add("log-area");
        logArea.setPrefRowCount(6);
        logCard.getChildren().addAll(logTitle, logArea);

        content.getChildren().addAll(header, statsRow, valueRow, alertCard, logCard);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
        return sp;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.TOP_LEFT);

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(val, lbl);
        return card;
    }

    public ScrollPane getView() { return view; }
}
