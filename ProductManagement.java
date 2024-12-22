package supermarketmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ProductManagement extends JFrame {
    private JTable productTable, soldItemsTable; // Added soldItemsTable
    private JTextField nameField, priceField, quantityField, categoryField;

    public ProductManagement() {
        setTitle("Product Management");
        setSize(800, 500); // Adjusted size for new table
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Product Table
        productTable = new JTable();
        loadProducts();
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setBorder(BorderFactory.createTitledBorder("Product Inventory"));

        // Sold Items Table
        soldItemsTable = new JTable(); // Added a table for sold items
        loadSoldItems();
        JScrollPane soldItemsScrollPane = new JScrollPane(soldItemsTable);
        soldItemsScrollPane.setBorder(BorderFactory.createTitledBorder("Sold Items"));

        // Layout for tables (split horizontally)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productScrollPane, soldItemsScrollPane);
        splitPane.setResizeWeight(0.5); // Equal space for both tables
        add(splitPane, BorderLayout.CENTER);

        // Input Panel for Product Management
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        // Buttons for Product Management
        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> addProduct());

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteProduct());

        JButton editButton = new JButton("Edit Product");
        editButton.addActionListener(e -> editProduct());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                nameField.setText(productTable.getValueAt(row, 1).toString());
                priceField.setText(productTable.getValueAt(row, 2).toString());
                quantityField.setText(productTable.getValueAt(row, 3).toString());
                categoryField.setText(productTable.getValueAt(row, 4).toString());
            }
        });

        setVisible(true);
    }

    private void loadProducts() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            productTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
    }

    private void loadSoldItems() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM bill_report");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            soldItemsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sold items: " + ex.getMessage());
        }
    }

    private void addProduct() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", "");
            PreparedStatement ps = con.prepareStatement("INSERT INTO products (name, price, quantity, category) VALUES (?, ?, ?, ?)");
            ps.setString(1, nameField.getText());
            ps.setDouble(2, Double.parseDouble(priceField.getText()));
            ps.setInt(3, Integer.parseInt(quantityField.getText()));
            ps.setString(4, categoryField.getText());
            ps.executeUpdate();
            con.close();
            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        try {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                String name = productTable.getValueAt(row, 1).toString();
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", "");
                PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE name=?");
                ps.setString(1, name);
                ps.executeUpdate();
                con.close();
                loadProducts();
                clearFields();
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to delete.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage());
        }
    }

    private void editProduct() {
        try {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                String oldName = productTable.getValueAt(row, 1).toString();
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", "");
                PreparedStatement ps = con.prepareStatement("UPDATE products SET name=?, price=?, quantity=?, category=? WHERE name=?");
                ps.setString(1, nameField.getText());
                ps.setDouble(2, Double.parseDouble(priceField.getText()));
                ps.setInt(3, Integer.parseInt(quantityField.getText()));
                ps.setString(4, categoryField.getText());
                ps.setString(5, oldName);
                ps.executeUpdate();
                con.close();
                loadProducts();
                clearFields();
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to edit.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing product: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        categoryField.setText("");
    }

    public static void main(String[] args) {
        new ProductManagement();
    }
}
