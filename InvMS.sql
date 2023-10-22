CREATE DATABASE IF NOT EXISTS InventoryDB;
GRANT ALL PRIVILEGES ON InventoryDB.* TO 'root'@'localhost';
USE InventoryDB;

CREATE TABLE IF NOT EXISTS Items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    initialQuantity INT NOT NULL DEFAULT 0,  -- added this column to track initial stock count
    lowStockThreshold INT NOT NULL DEFAULT 0,
    costPerItem DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    revenuePerItem DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    soldQuantity INT NOT NULL DEFAULT 0     -- added this column to track sold items
);

CREATE TABLE IF NOT EXISTS FinancialMetrics (
    id INT PRIMARY KEY,
    totalCost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    totalRevenue DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    totalProfit DECIMAL(10, 2) NOT NULL DEFAULT 0.00
);

INSERT INTO FinancialMetrics (id, totalCost, totalRevenue, totalProfit)
VALUES (1, 0.00, 0.00, 0.00);
