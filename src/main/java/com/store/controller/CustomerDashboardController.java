package com.store.controller;

import com.store.auth.AuthManager;
import com.store.auth.User;
import com.store.component.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  CUSTOMER DASHBOARD CONTROLLER
 *
 *  The online-shopper experience:
 *    • Browse / search products
 *    • Buy products (deducts from wallet)
 *    • View wallet balance
 *    • View own order history
 *    • Cannot see other customers' data
 *    • Cannot modify products or system settings
 * ============================================================
 */
public class CustomerDashboardController {

    private final ProductManager manager;
    private final AuthManager    auth;
    private final User           currentUser;
    private Customer             myCustomer;

    // UI state
    private final VBox  root;
    private VBox        contentArea;
    private Button      activeNav;

    // Order history (session-based)
    private final List<String> orderHistory = new ArrayList<>();

    public CustomerDashboardController(ProductManager manager, AuthManager auth, User currentUser) {
        this.manager     = manager;
        this.auth        = auth;
        this.currentUser = currentUser;
        this.myCustomer  = resolveCustomer();
        this.root        = buildShell();
    }

    // ── Resolve or create Customer record ────────────────────

    private Customer resolveCustomer() {
        String linkedId = currentUser.getLinkedCustomerId();
        if (linkedId != null) {
            Customer c = manager.findCustomerById(linkedId);
            if (c != null) return c;
        }
        // Create a new Customer record linked to this user account
        String newId   = "C" + String.format("%03d", (int)(Math.random() * 900) + 100);
        Customer fresh = new Customer(newId, currentUser.getUsername(),
                currentUser.getUsername() + "@smartstore.com", 500.00);
        manager.addCustomer(fresh);
        auth.linkCustomerRecord(currentUser.getUsername(), newId);
        return fresh;
    }

    // ── Shell (sidebar + content) ─────────────────────────────

    private VBox buildShell() {
        HBox shell = new HBox(0);

        VBox sidebar = buildSidebar(shell);
        contentArea  = new VBox();
        contentArea.setFillWidth(true);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        shell.getChildren().addAll(sidebar, contentArea);

        VBox outer = new VBox(shell);
        outer.setFillWidth(true);
        VBox.setVgrow(shell, Priority.ALWAYS);

        showShop();   // default view

        outer.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), outer);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        return outer;
    }

    private VBox buildSidebar(HBox shell) {
        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: #0d1117; -fx-min-width: 220px; -fx-max-width: 220px; " +
                "-fx-border-color: #21262d; -fx-border-width: 0 1 0 0;");

        // ── Header ────────────────────────────────────────────
        VBox header = new VBox(4);
        header.setPadding(new Insets(20, 16, 16, 16));
        header.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");

        Label storeName = new Label("🛍 SmartStore");
        storeName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        Label storeSub = new Label("Online Shop");
        storeSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #22c55e; -fx-font-weight: bold; " +
                "-fx-letter-spacing: 1px;");
        header.getChildren().addAll(storeName, storeSub);

        // ── User info ─────────────────────────────────────────
        HBox userCard = new HBox(10);
        userCard.setAlignment(Pos.CENTER_LEFT);
        userCard.setPadding(new Insets(14, 16, 14, 16));
        userCard.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");

        StackPane avatar = new StackPane();
        Circle bg = new Circle(18);
        bg.setFill(Color.web("#22c55e"));
        Label initials = new Label(String.valueOf(currentUser.getUsername().charAt(0)).toUpperCase());
        initials.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(bg, initials);

        VBox userMeta = new VBox(2);
        Label uname = new Label(currentUser.getUsername());
        uname.setStyle("-fx-text-fill: #e6edf3; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label role = new Label("● Customer");
        role.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 11px;");
        userMeta.getChildren().addAll(uname, role);

        userCard.getChildren().addAll(avatar, userMeta);

        // ── Wallet quick-view ─────────────────────────────────
        VBox walletCard = new VBox(4);
        walletCard.setPadding(new Insets(12, 16, 12, 16));
        walletCard.setStyle("-fx-background-color: #0a1a0a; -fx-border-color: #21262d; " +
                "-fx-border-width: 0 0 1 0;");
        Label wLabel = new Label("WALLET BALANCE");
        wLabel.setStyle("-fx-text-fill: #30363d; -fx-font-size: 9px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        Label wAmount = new Label(String.format("$%.2f", myCustomer.getWalletBalance()));
        wAmount.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 22px; -fx-font-weight: bold;");
        walletCard.getChildren().addAll(wLabel, wAmount);

        // ── Nav items ─────────────────────────────────────────
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(8, 0, 8, 0));

        Label navLbl = new Label("MENU");
        navLbl.setStyle("-fx-text-fill: #30363d; -fx-font-size: 9px; -fx-font-weight: bold; " +
                "-fx-padding: 8 16 4 16; -fx-letter-spacing: 1px;");

        Button btnShop    = navBtn("🏪  Browse Shop");
        Button btnWallet  = navBtn("💰  My Wallet");
        Button btnOrders  = navBtn("📦  My Orders");

        activeNav = btnShop;
        setNavActive(btnShop);

        btnShop.setOnAction(e   -> { setNavActive(btnShop);   showShop(); });
        btnWallet.setOnAction(e -> { setNavActive(btnWallet); showWallet(wAmount); });
        btnOrders.setOnAction(e -> { setNavActive(btnOrders); showOrders(); });

        nav.getChildren().addAll(navLbl, btnShop, btnWallet, btnOrders);

        // ── Spacer + Logout ───────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, userCard, walletCard, nav, spacer);
        return sidebar;
    }

    // ── Nav helpers ───────────────────────────────────────────

    private Button navBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; " +
                "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 20; " +
                "-fx-border-width: 0; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> {
            if (btn != activeNav)
                btn.setStyle(btn.getStyle()
                        .replace("-fx-text-fill: #8b949e", "-fx-text-fill: #e6edf3")
                        .replace("-fx-background-color: transparent", "-fx-background-color: #161b24"));
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeNav)
                btn.setStyle(btn.getStyle()
                        .replace("-fx-text-fill: #e6edf3", "-fx-text-fill: #8b949e")
                        .replace("-fx-background-color: #161b24", "-fx-background-color: transparent"));
        });
        return btn;
    }

    private void setNavActive(Button btn) {
        if (activeNav != null) {
            activeNav.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; " +
                    "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 20; " +
                    "-fx-border-width: 0; -fx-cursor: hand;");
        }
        activeNav = btn;
        btn.setStyle("-fx-background-color: #162014; -fx-text-fill: #4ade80; " +
                "-fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 11 17 11 17; " +
                "-fx-border-color: #22c55e; -fx-border-width: 0 0 0 3; -fx-cursor: hand;");
    }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
        VBox.setVgrow(node, Priority.ALWAYS);
    }

    // ═══════════════════════════════════════════════════════════
    //  SHOP VIEW
    // ═══════════════════════════════════════════════════════════

    private void showShop() {
        VBox page = new VBox(0);
        page.setStyle("-fx-background-color: #0d1117;");
        page.setFillWidth(true);

        // ── Top bar ───────────────────────────────────────────
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(24, 28, 16, 28));
        topBar.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");

        VBox titles = new VBox(2);
        Label title    = new Label("Browse Products");
        Label subtitle = new Label("Discover and purchase products from our store");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #484f58;");
        titles.getChildren().addAll(title, subtitle);
        HBox.setHgrow(titles, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("🔍  Search products...");
        search.setPrefWidth(240);
        search.setStyle("-fx-background-color: #161b24; -fx-text-fill: #e6edf3; " +
                "-fx-prompt-text-fill: #30363d; -fx-border-color: #30363d; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-border-width: 1; -fx-padding: 9 12; -fx-font-size: 13px;");

        topBar.getChildren().addAll(titles, search);

        // ── Category tabs ─────────────────────────────────────
        HBox catTabs = new HBox(8);
        catTabs.setPadding(new Insets(12, 28, 12, 28));
        catTabs.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");
        String[] cats = {"All", "Electronics", "Food & Grocery", "Clothing & Apparel"};
        String[] catColors = {"#6366f1", "#818cf8", "#34d399", "#ec4899"};
        Button[] catBtns = new Button[cats.length];
        for (int i = 0; i < cats.length; i++) {
            final int idx = i;
            catBtns[i] = catTab(cats[i], catColors[i], i == 0);
            catBtns[i].setOnAction(e -> {
                for (Button b : catBtns)
                    b.setStyle(b.getStyle()
                            .replace("-fx-background-color: " + catColors[idx] + "22;", "-fx-background-color: transparent;")
                            .replace("-fx-text-fill: " + catColors[idx], "-fx-text-fill: #484f58"));
            });
            catTabs.getChildren().add(catBtns[i]);
        }

        // ── Product grid ──────────────────────────────────────
        ScrollPane gridScroll = new ScrollPane();
        gridScroll.setFitToWidth(true);
        gridScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
        VBox.setVgrow(gridScroll, Priority.ALWAYS);

        FlowPane grid = new FlowPane();
        grid.setHgap(16); grid.setVgap(16);
        grid.setPadding(new Insets(20, 28, 28, 28));
        grid.setStyle("-fx-background-color: #0d1117;");

        Label statusLbl = new Label();
        statusLbl.setWrapText(true);
        statusLbl.setMaxWidth(Double.MAX_VALUE);
        statusLbl.setPadding(new Insets(0, 28, 8, 28));

        Runnable refreshGrid = () -> {
            String q = search.getText().trim();
            List<Product> products = q.isEmpty() ? manager.getAllProducts() : manager.searchProducts(q);
            grid.getChildren().clear();
            if (products.isEmpty()) {
                Label empty = new Label("No products found.");
                empty.setStyle("-fx-text-fill: #484f58; -fx-font-size: 14px;");
                grid.getChildren().add(empty);
            } else {
                for (Product p : products) {
                    grid.getChildren().add(productCard(p, statusLbl));
                }
            }
        };

        refreshGrid.run();
        search.textProperty().addListener((obs, o, n) -> refreshGrid.run());

        gridScroll.setContent(grid);
        page.getChildren().addAll(topBar, catTabs, statusLbl, gridScroll);
        VBox.setVgrow(gridScroll, Priority.ALWAYS);
        setContent(page);
    }

    private VBox productCard(Product product, Label statusLbl) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #161b24; -fx-background-radius: 14; " +
                "-fx-border-color: #21262d; -fx-border-width: 1; -fx-border-radius: 14; -fx-cursor: hand;");

        // Category badge
        String catColor = categoryColor(product.getCategory());
        Label catBadge = new Label(categoryEmoji(product.getCategory()) + " " + product.getCategory());
        catBadge.setStyle("-fx-background-color: " + catColor + "22; -fx-text-fill: " + catColor + "; " +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; " +
                "-fx-background-radius: 20; -fx-letter-spacing: 0.5px;");

        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label pid = new Label("ID: " + product.getProductId());
        pid.setStyle("-fx-text-fill: #30363d; -fx-font-size: 11px;");

        Label price = new Label(String.format("$%.2f", product.getPrice()));
        price.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label stock = new Label(product.getStockQuantity() + " in stock");
        stock.setStyle("-fx-text-fill: " +
                (product.getStockQuantity() < 5 ? "#ef4444" : "#484f58") +
                "; -fx-font-size: 11px;");

        Button buyBtn = new Button(product.getStockQuantity() > 0 ? "Add to Cart" : "Out of Stock");
        buyBtn.setMaxWidth(Double.MAX_VALUE);
        buyBtn.setDisable(product.getStockQuantity() == 0);
        buyBtn.setStyle("-fx-background-color: " + (product.getStockQuantity() > 0 ? "#22c55e" : "#21262d") +
                "; -fx-text-fill: " + (product.getStockQuantity() > 0 ? "#0d1117" : "#484f58") +
                "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                "-fx-border-width: 0; -fx-cursor: hand; -fx-padding: 8;");

        buyBtn.setOnAction(e -> handlePurchase(product, 1, statusLbl));

        card.getChildren().addAll(catBadge, name, pid, price, stock, buyBtn);

        // Hover effect
        card.setOnMouseEntered(ev -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #21262d", "-fx-border-color: #30363d")
                .replace("-fx-background-color: #161b24", "-fx-background-color: #1c2230")));
        card.setOnMouseExited(ev -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #30363d", "-fx-border-color: #21262d")
                .replace("-fx-background-color: #1c2230", "-fx-background-color: #161b24")));

        return card;
    }

    private void handlePurchase(Product product, int quantity, Label statusLbl) {
        // Quick-buy dialog
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Purchase — " + product.getName());
        dialog.setHeaderText(null);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #161b24;");

        Label prodName = new Label(product.getName());
        prodName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        Label priceInfo = new Label(String.format("Price: $%.2f  •  Stock: %d", product.getPrice(), product.getStockQuantity()));
        priceInfo.setStyle("-fx-text-fill: #484f58; -fx-font-size: 12px;");
        Label walletInfo = new Label(String.format("Your wallet: $%.2f", myCustomer.getWalletBalance()));
        walletInfo.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 13px;");

        Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(1, product.getStockQuantity()), 1);
        qtySpinner.setEditable(true);
        qtySpinner.setPrefWidth(120);
        qtySpinner.setStyle("-fx-background-color: #0d1117;");

        Label total = new Label(String.format("Total: $%.2f", product.getPrice()));
        total.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");
        qtySpinner.valueProperty().addListener((obs, o, n) ->
            total.setText(String.format("Total: $%.2f", product.getPrice() * n))
        );

        content.getChildren().addAll(prodName, priceInfo, walletInfo,
                new Separator(), fieldRow("Quantity", qtySpinner), total);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: #161b24;");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> bt == ButtonType.OK ? qtySpinner.getValue() : null);
        dialog.showAndWait().ifPresent(qty -> {
            try {
                double totalCost = product.getPrice() * qty;
                if (myCustomer.getWalletBalance() < totalCost)
                    throw new InsufficientBalanceException(myCustomer.getName(), totalCost, myCustomer.getWalletBalance());
                myCustomer.purchase(product, qty);
                manager.saveState();
                String entry = String.format("✓ Bought %dx %s for $%.2f", qty, product.getName(), totalCost);
                orderHistory.add(0, entry);
                showStatusMsg(statusLbl, entry, true);
            } catch (InsufficientBalanceException ex) {
                showStatusMsg(statusLbl, "Insufficient balance! Need $" +
                        String.format("%.2f", ex.getRequired()) + ", have $" +
                        String.format("%.2f", myCustomer.getWalletBalance()), false);
            } catch (OutOfStockException ex) {
                showStatusMsg(statusLbl, "Out of stock!", false);
            } catch (Exception ex) {
                showStatusMsg(statusLbl, "Purchase failed: " + ex.getMessage(), false);
            }
        });
    }

    private HBox fieldRow(String label, javafx.scene.Node field) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
        lbl.setPrefWidth(80);
        row.getChildren().addAll(lbl, field);
        return row;
    }

    private void showStatusMsg(Label lbl, String msg, boolean ok) {
        lbl.setText(ok ? "✓  " + msg : "⚠  " + msg);
        lbl.setStyle("-fx-background-color: " + (ok ? "#052e16" : "#3b0d0d") + "; " +
                "-fx-text-fill: " + (ok ? "#4ade80" : "#f87171") + "; " +
                "-fx-padding: 8 14; -fx-background-radius: 8; -fx-font-size: 12px;");
        lbl.setVisible(true);
        lbl.setManaged(true);
        PauseTransition p = new PauseTransition(Duration.seconds(4));
        p.setOnFinished(e -> { lbl.setVisible(false); lbl.setManaged(false); });
        p.play();
    }

    // ═══════════════════════════════════════════════════════════
    //  WALLET VIEW
    // ═══════════════════════════════════════════════════════════

    private void showWallet(Label sidebarAmount) {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117; -fx-border-width: 0;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        VBox page = new VBox(24);
        page.setPadding(new Insets(28));
        page.setStyle("-fx-background-color: #0d1117;");

        Label title    = new Label("My Wallet");
        Label subtitle = new Label("Manage your balance and top up funds");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #484f58;");

        // Balance card
        VBox balCard = new VBox(8);
        balCard.setPadding(new Insets(28));
        balCard.setStyle("-fx-background-color: #0a1a0a; -fx-background-radius: 16; " +
                "-fx-border-color: #22c55e33; -fx-border-radius: 16; -fx-border-width: 1;");
        Label balLabel = new Label("AVAILABLE BALANCE");
        balLabel.setStyle("-fx-text-fill: #30363d; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1.5px;");
        Label balAmount = new Label(String.format("$%.2f", myCustomer.getWalletBalance()));
        balAmount.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #4ade80;");
        Label balSub = new Label("Customer ID: " + myCustomer.getCustomerId());
        balSub.setStyle("-fx-text-fill: #30363d; -fx-font-size: 12px;");
        balCard.getChildren().addAll(balLabel, balAmount, balSub);

        // Top up card
        VBox topUpCard = new VBox(16);
        topUpCard.setPadding(new Insets(24));
        topUpCard.setStyle("-fx-background-color: #161b24; -fx-background-radius: 14; " +
                "-fx-border-color: #21262d; -fx-border-width: 1; -fx-border-radius: 14;");
        Label topUpTitle = new Label("Top Up Wallet");
        topUpTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e6edf3;");

        // Quick amount buttons
        HBox quickRow = new HBox(10);
        double[] amounts = {10, 25, 50, 100, 250};
        TextField customAmt = new TextField();
        customAmt.setPromptText("Custom amount...");
        customAmt.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #e6edf3; " +
                "-fx-prompt-text-fill: #30363d; -fx-border-color: #30363d; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1; -fx-padding: 9 14;");

        for (double amt : amounts) {
            Button qBtn = new Button("$" + (int)amt);
            qBtn.setStyle("-fx-background-color: #161b24; -fx-text-fill: #8b949e; " +
                    "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                    "-fx-border-color: #30363d; -fx-border-width: 1; -fx-border-radius: 8; " +
                    "-fx-cursor: hand; -fx-padding: 8 16;");
            qBtn.setOnAction(e -> customAmt.setText(String.valueOf((int)amt)));
            quickRow.getChildren().add(qBtn);
        }

        Label msgLabel = new Label();
        msgLabel.setWrapText(true);

        Button topUpBtn = new Button("Top Up Now");
        topUpBtn.setPrefHeight(44);
        topUpBtn.setPrefWidth(200);
        topUpBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: #0d1117; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-width: 0; -fx-cursor: hand;");
        topUpBtn.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(customAmt.getText().trim());
                myCustomer.topUpWallet(amt);
                manager.saveState();
                balAmount.setText(String.format("$%.2f", myCustomer.getWalletBalance()));
                sidebarAmount.setText(String.format("$%.2f", myCustomer.getWalletBalance()));
                customAmt.clear();
                msgLabel.setText("✓  Wallet topped up by $" + String.format("%.2f", amt));
                msgLabel.setStyle("-fx-background-color: #052e16; -fx-text-fill: #4ade80; " +
                        "-fx-padding: 8 14; -fx-background-radius: 8; -fx-font-size: 12px;");
            } catch (NumberFormatException ex) {
                msgLabel.setText("⚠  Please enter a valid amount.");
                msgLabel.setStyle("-fx-background-color: #3b0d0d; -fx-text-fill: #f87171; " +
                        "-fx-padding: 8 14; -fx-background-radius: 8; -fx-font-size: 12px;");
            } catch (IllegalArgumentException ex) {
                msgLabel.setText("⚠  " + ex.getMessage());
                msgLabel.setStyle("-fx-background-color: #3b0d0d; -fx-text-fill: #f87171; " +
                        "-fx-padding: 8 14; -fx-background-radius: 8; -fx-font-size: 12px;");
            }
        });

        topUpCard.getChildren().addAll(topUpTitle, quickRow,
                fieldRow2("Amount ($)", customAmt), msgLabel, topUpBtn);
        page.getChildren().addAll(new VBox(4, title, subtitle), balCard, topUpCard);
        sp.setContent(page);
        setContent(sp);
    }

    private HBox fieldRow2(String label, javafx.scene.Node field) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
        lbl.setPrefWidth(100);
        HBox.setHgrow(field instanceof Region ? (Region)field : new HBox(field), Priority.ALWAYS);
        row.getChildren().addAll(lbl, field);
        return row;
    }

    // ═══════════════════════════════════════════════════════════
    //  ORDERS VIEW
    // ═══════════════════════════════════════════════════════════

    private void showOrders() {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117; -fx-border-width: 0;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        VBox page = new VBox(20);
        page.setPadding(new Insets(28));
        page.setStyle("-fx-background-color: #0d1117;");

        Label title    = new Label("My Orders");
        Label subtitle = new Label("Your purchase history this session");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #f0f6fc;");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #484f58;");

        VBox listCard = new VBox(10);
        listCard.setPadding(new Insets(20));
        listCard.setStyle("-fx-background-color: #161b24; -fx-background-radius: 14; " +
                "-fx-border-color: #21262d; -fx-border-width: 1; -fx-border-radius: 14;");

        if (orderHistory.isEmpty()) {
            Label empty = new Label("No purchases yet this session.\nStart browsing the shop! 🛍");
            empty.setStyle("-fx-text-fill: #484f58; -fx-font-size: 14px; -fx-text-alignment: center;");
            empty.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            empty.setAlignment(Pos.CENTER);
            empty.setMaxWidth(Double.MAX_VALUE);
            listCard.getChildren().add(empty);
        } else {
            Label orderTitle = new Label("Order History  (" + orderHistory.size() + " items)");
            orderTitle.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 15px; -fx-font-weight: bold;");
            listCard.getChildren().add(orderTitle);
            for (int i = 0; i < orderHistory.size(); i++) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 14, 10, 14));
                row.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 8;");
                Label num = new Label("#" + (orderHistory.size() - i));
                num.setStyle("-fx-text-fill: #30363d; -fx-font-size: 12px; -fx-min-width: 28;");
                Label entry = new Label(orderHistory.get(i));
                entry.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
                row.getChildren().addAll(num, entry);
                listCard.getChildren().add(row);
            }
        }

        page.getChildren().addAll(new VBox(4, title, subtitle), listCard);
        sp.setContent(page);
        setContent(sp);
    }

    // ── Helpers ───────────────────────────────────────────────

    private String categoryColor(String cat) {
        if (cat == null) return "#6366f1";
        switch (cat.toLowerCase()) {
            case "electronics": return "#818cf8";
            case "food & grocery": return "#34d399";
            case "clothing & apparel": return "#ec4899";
            default: return "#6366f1";
        }
    }

    private String categoryEmoji(String cat) {
        if (cat == null) return "📦";
        switch (cat.toLowerCase()) {
            case "electronics": return "💻";
            case "food & grocery": return "🥗";
            case "clothing & apparel": return "👗";
            default: return "📦";
        }
    }

    private Button catTab(String label, String color, boolean active) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + (active ? color + "22" : "transparent") + "; " +
                "-fx-text-fill: " + (active ? color : "#484f58") + "; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 20; " +
                "-fx-border-width: 0; -fx-cursor: hand; -fx-padding: 6 14;");
        return btn;
    }

    public VBox getRoot() { return root; }
}
