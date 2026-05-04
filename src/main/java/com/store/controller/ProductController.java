package com.store.controller;

import com.store.component.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * ProductController — bridges the Products UI to the ProductManager backend.
 * UI captures input → controller calls backend → UI shows results.
 * NO business logic here — all logic is in ProductManager / Product classes.
 */
public class ProductController {

    private final ProductManager manager;
    private final SplitPane view;

    private TableView<Product> table;
    private ObservableList<Product> tableData;
    private Label statusLabel;

    public ProductController(ProductManager manager, com.store.auth.User user) { this(manager); }
    public ProductController(ProductManager manager) {
        this.manager   = manager;
        this.tableData = FXCollections.observableArrayList(manager.getInventory());
        this.view      = buildView();
    }

    // ── Build the full view ───────────────────────────────────

    private SplitPane buildView() {
        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color: #0f1117; -fx-border-width: 0;");
        split.setDividerPositions(0.62);

        split.getItems().addAll(buildLeftPanel(), buildRightPanel());
        return split;
    }

    // ── LEFT: Table with search/sort toolbar ─────────────────

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 20, 28, 28));

        // Header
        Label title    = new Label("Product Inventory");
        Label subtitle = new Label("Manage your store products");
        title.getStyleClass().add("page-title");
        subtitle.getStyleClass().add("page-subtitle");

        // Toolbar: Search + Sort + Remove
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, ID or category...");
        searchField.setPrefWidth(240);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Default", "Name", "Price Asc", "Price Desc", "Stock", "Category");
        sortBox.setValue("Default");
        sortBox.setPrefWidth(140);

        Button btnRemove = new Button("Remove Selected");
        btnRemove.getStyleClass().add("btn-danger");

        toolbar.getChildren().addAll(searchField, sortBox, btnRemove);

        // Status label
        statusLabel = new Label();
        statusLabel.setWrapText(true);

        // TableView
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        panel.getChildren().addAll(
                new VBox(4, title, subtitle),
                toolbar,
                statusLabel,
                table
        );

        // ── Wire Events ──
        searchField.textProperty().addListener((obs, old, val) -> {
            List<Product> results = manager.searchProducts(val);
            tableData.setAll(results);
        });

        sortBox.setOnAction(e -> {
            String sel = sortBox.getValue().toLowerCase().replace(" ", " ");
            List<Product> sorted = manager.getSortedProducts(sel.equals("default") ? "" : sel);
            tableData.setAll(sorted);
        });

        btnRemove.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showStatus("Please select a product to remove.", "error");
                return;
            }
            boolean ok = manager.removeProduct(selected.getProductId());
            if (ok) {
                tableData.remove(selected);
                showStatus("Removed: " + selected.getName(), "success");
            } else {
                showStatus("Could not remove product.", "error");
            }
        });

        return panel;
    }

    private TableView<Product> buildTable() {
        TableView<Product> tv = new TableView<>(tableData);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("No products found."));

        TableColumn<Product, String> colId   = new TableColumn<>("ID");
        TableColumn<Product, String> colName = new TableColumn<>("Name");
        TableColumn<Product, String> colCat  = new TableColumn<>("Category");
        TableColumn<Product, String> colPrice= new TableColumn<>("Price");
        TableColumn<Product, String> colDisc = new TableColumn<>("Discount");
        TableColumn<Product, String> colFinal= new TableColumn<>("Final Price");
        TableColumn<Product, String> colStock= new TableColumn<>("Stock");
        TableColumn<Product, String> colExtra= new TableColumn<>("Extra Info");

        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductId()));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getPrice())));
        colDisc.setCellValueFactory(d -> new SimpleStringProperty(String.format("-$%.2f", d.getValue().applyDiscount())));
        colFinal.setCellValueFactory(d -> {
            double final_ = d.getValue().getPrice() - d.getValue().applyDiscount();
            return new SimpleStringProperty(String.format("$%.2f", final_));
        });
        colStock.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStockQuantity())));
        colExtra.setCellValueFactory(d -> {
            Product p = d.getValue();
            if (p instanceof Electronics)  return new SimpleStringProperty(((Electronics) p).getWarrantyYears() + "yr warranty");
            if (p instanceof Food)         return new SimpleStringProperty("Exp: " + ((Food) p).getExpiryDate());
            if (p instanceof Clothing)     return new SimpleStringProperty(((Clothing) p).getSize() + " · " + ((Clothing) p).getMaterial());
            return new SimpleStringProperty("-");
        });

        // Color-code stock column
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                int qty = Integer.parseInt(item);
                if      (qty == 0)  setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                else if (qty < 5)   setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                else                setStyle("-fx-text-fill: #86efac;");
            }
        });

        tv.getColumns().addAll(colId, colName, colCat, colPrice, colDisc, colFinal, colStock, colExtra);
        return tv;
    }

    // ── RIGHT: Add product form (tab per type) ────────────────

    private VBox buildRightPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-area");
        panel.setPadding(new Insets(28, 28, 28, 16));

        Label title = new Label("Add Product");
        title.getStyleClass().add("page-title");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
                buildElectronicsTab(),
                buildFoodTab(),
                buildClothingTab(),
                buildRestockTab(),
                buildUpdatePriceTab()
        );

        VBox.setVgrow(tabs, Priority.ALWAYS);
        panel.getChildren().addAll(title, tabs);
        return panel;
    }

    private Tab buildElectronicsTab() {
        Tab tab = new Tab("Electronics");
        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id       = field("Product ID");
        TextField name     = field("Product Name");
        TextField price    = field("Price ($)");
        TextField stock    = field("Stock (units)");
        TextField warranty = field("Warranty (years)");

        Button btn = new Button("Add Electronics");
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);

        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                Electronics p = new Electronics(
                        id.getText().trim(), name.getText().trim(),
                        Double.parseDouble(price.getText().trim()),
                        Integer.parseInt(stock.getText().trim()),
                        Integer.parseInt(warranty.getText().trim()));
                boolean ok = manager.addProduct(p);
                if (ok) {
                    tableData.setAll(manager.getInventory());
                    showStatus("Added: " + p.getName(), "success");
                    msg.setText("Added successfully!");
                    msg.getStyleClass().setAll("msg-success");
                    clearFields(id, name, price, stock, warranty);
                } else {
                    msg.setText("Product ID already exists.");
                    msg.getStyleClass().setAll("msg-error");
                }
            } catch (InvalidPriceException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            } catch (NumberFormatException ex) {
                msg.setText("Invalid number — check price, stock, or warranty.");
                msg.getStyleClass().setAll("msg-error");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Product ID", id),
                labeledField("Name", name),
                labeledField("Price ($)", price),
                labeledField("Stock (units)", stock),
                labeledField("Warranty (years)", warranty),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    private Tab buildFoodTab() {
        Tab tab = new Tab("Food");
        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id     = field("Product ID");
        TextField name   = field("Product Name");
        TextField price  = field("Price ($)");
        TextField stock  = field("Stock (units)");
        TextField expiry = field("Expiry Date (YYYY-MM-DD)");

        Button btn = new Button("Add Food Product");
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                Food p = new Food(
                        id.getText().trim(), name.getText().trim(),
                        Double.parseDouble(price.getText().trim()),
                        Integer.parseInt(stock.getText().trim()),
                        expiry.getText().trim());
                boolean ok = manager.addProduct(p);
                if (ok) {
                    tableData.setAll(manager.getInventory());
                    showStatus("Added: " + p.getName(), "success");
                    msg.setText("Added successfully!");
                    msg.getStyleClass().setAll("msg-success");
                    clearFields(id, name, price, stock, expiry);
                } else {
                    msg.setText("Product ID already exists.");
                    msg.getStyleClass().setAll("msg-error");
                }
            } catch (InvalidPriceException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            } catch (NumberFormatException ex) {
                msg.setText("Invalid number — check price or stock.");
                msg.getStyleClass().setAll("msg-error");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Product ID", id),
                labeledField("Name", name),
                labeledField("Price ($)", price),
                labeledField("Stock (units)", stock),
                labeledField("Expiry Date", expiry),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    private Tab buildClothingTab() {
        Tab tab = new Tab("Clothing");
        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id       = field("Product ID");
        TextField name     = field("Product Name");
        TextField price    = field("Price ($)");
        TextField stock    = field("Stock (units)");
        ComboBox<String> size = new ComboBox<>();
        size.getItems().addAll("XS", "S", "M", "L", "XL", "XXL");
        size.setValue("M");
        size.setMaxWidth(Double.MAX_VALUE);
        TextField material = field("Material (e.g. Cotton)");

        Button btn = new Button("Add Clothing");
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                Clothing p = new Clothing(
                        id.getText().trim(), name.getText().trim(),
                        Double.parseDouble(price.getText().trim()),
                        Integer.parseInt(stock.getText().trim()),
                        size.getValue(),
                        material.getText().trim());
                boolean ok = manager.addProduct(p);
                if (ok) {
                    tableData.setAll(manager.getInventory());
                    showStatus("Added: " + p.getName(), "success");
                    msg.setText("Added successfully!");
                    msg.getStyleClass().setAll("msg-success");
                    clearFields(id, name, price, stock, material);
                } else {
                    msg.setText("Product ID already exists.");
                    msg.getStyleClass().setAll("msg-error");
                }
            } catch (InvalidPriceException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            } catch (NumberFormatException ex) {
                msg.setText("Invalid number — check price or stock.");
                msg.getStyleClass().setAll("msg-error");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Product ID", id),
                labeledField("Name", name),
                labeledField("Price ($)", price),
                labeledField("Stock (units)", stock),
                labeledField("Size", size),
                labeledField("Material", material),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    private Tab buildRestockTab() {
        Tab tab = new Tab("Restock");
        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id  = field("Product ID");
        Spinner<Integer> qty = new Spinner<>(1, 10000, 10);
        qty.setEditable(true);
        qty.setMaxWidth(Double.MAX_VALUE);

        Button btn = new Button("Restock Product");
        btn.getStyleClass().add("btn-success");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                manager.restockProduct(id.getText().trim(), qty.getValue());
                tableData.setAll(manager.getInventory());
                showStatus("Restocked successfully!", "success");
                msg.setText("Restocked successfully!");
                msg.getStyleClass().setAll("msg-success");
                clearFields(id);
            } catch (Exception ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Product ID", id),
                labeledField("Quantity to Add", qty),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    private Tab buildUpdatePriceTab() {
        Tab tab = new Tab("Update Price");
        VBox form = new VBox(12);
        form.setPadding(new Insets(16, 0, 0, 0));

        TextField id    = field("Product ID");
        TextField price = field("New Price ($)");

        Button btn = new Button("Update Price");
        btn.getStyleClass().add("btn-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        Label msg = new Label();
        msg.setWrapText(true);

        btn.setOnAction(e -> {
            try {
                manager.updateProductPrice(id.getText().trim(),
                        Double.parseDouble(price.getText().trim()));
                tableData.setAll(manager.getInventory());
                showStatus("Price updated!", "success");
                msg.setText("Price updated successfully!");
                msg.getStyleClass().setAll("msg-success");
                clearFields(id, price);
            } catch (InvalidPriceException ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            } catch (NumberFormatException ex) {
                msg.setText("Invalid price format.");
                msg.getStyleClass().setAll("msg-error");
            } catch (Exception ex) {
                msg.setText(ex.getMessage());
                msg.getStyleClass().setAll("msg-error");
            }
        });

        form.getChildren().addAll(
                labeledField("Product ID", id),
                labeledField("New Price ($)", price),
                btn, msg
        );
        tab.setContent(form);
        return tab;
    }

    // ── Helpers ───────────────────────────────────────────────

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
