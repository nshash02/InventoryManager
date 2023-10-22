import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Database {
	// PUT IN YOUR INFORMATION FOR OWN SQL SERVER (MAKE SURE YOU HAVE JDBC INSTALLED AND CONNECTED)
	private String url = "jdbc:mysql://[hostname]:[port]/[database-name]?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=[timezone]\n";
    private String user = "root";
    private String password = "password";
    private Map<Integer, Item> items = new HashMap<>();

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // REGISTERS JDBC DRIVER (THIS IS FOR NEWER VERSIONS OF JDBC)
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found. Include it in your library path");
        }

        return DriverManager.getConnection(url, user, password); // GETS CONNECTION
    }
    
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM Items";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int quantity = rs.getInt("quantity");
            int lowStockThreshold = rs.getInt("lowStockThreshold");
            double costPerItem = rs.getDouble("costPerItem");
            double revenuePerItem = rs.getDouble("revenuePerItem");
            items.add(new Item(id, name, quantity, lowStockThreshold, costPerItem, revenuePerItem));
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    public boolean addItem(Item item) { // METHOD TO ADD ITEM
        String query = "INSERT INTO Items (name, quantity, lowStockThreshold, costPerItem, revenuePerItem) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
             
            pst.setString(1, item.getName());
            pst.setInt(2, item.getQuantity());
            pst.setInt(3, item.getLowStockThreshold());
            pst.setDouble(4, item.getCostPerItem());
            pst.setDouble(5, item.getRevenuePerItem());
            int result = pst.executeUpdate();
            
            if (result > 0) {
                return true;
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
    public boolean updateItem(Item item) { //METHOD TO UPDATE ITEM
    	String query = "UPDATE Items SET name = ?, quantity = ?, lowStockThreshold = ?, costPerItem = ?, revenuePerItem = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
             
        	pst.setString(1, item.getName());
        	pst.setInt(2, item.getQuantity());
        	pst.setInt(3, item.getLowStockThreshold());
        	pst.setDouble(4, item.getCostPerItem());
        	pst.setDouble(5, item.getRevenuePerItem());
        	pst.setInt(6, item.getId()); 
            int result = pst.executeUpdate();
            
            if (result > 0) {
                return true;
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
    public boolean deleteItem(int id) { //METHOD TO DELETE ITEM
        String query = "DELETE FROM Items WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
             
            pst.setInt(1, id);
            int result = pst.executeUpdate();
            
            if (result > 0) {
                return true;
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean sellItem(int itemId, int quantitySold) { //METHOD TO SELL ITEM (TOOK WAY TOO MUCH TIME AND RESEARCH...)
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);  // BEGINS TRANSACTION

            String checkQuantityQuery = "SELECT quantity FROM Items WHERE id = ?"; // CHECKS IF AVAILABLE QUANTITY FOR SALE
            try (PreparedStatement pst = con.prepareStatement(checkQuantityQuery)) {
                pst.setInt(1, itemId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("quantity") < quantitySold) {
                        con.rollback();
                        return false;  // NO STOCK TO SELL
                    }
                } 
                
                else {
                    con.rollback();
                    return false;  // ITEM NOT FOUND
                }
            }

            // INCREMENTS SOLD QUANTITY IN ITEMS TABLE (MOVED FROM RIGHT TO ABOVE FOR READABILITY... YOU ARE WELCOME)
            String updateSoldQuantityQuery = "UPDATE Items SET soldQuantity = soldQuantity + ? WHERE id = ?"; 
            try (PreparedStatement pst1_1 = con.prepareStatement(updateSoldQuantityQuery)) {
                pst1_1.setInt(1, quantitySold);
                pst1_1.setInt(2, itemId);
                pst1_1.executeUpdate();
            }

            // DEDUCTS SOLD QUANTITY FROM ITEMS TABLE
            String deductQuantityQuery = "UPDATE Items SET quantity = quantity - ? WHERE id = ?";
            try (PreparedStatement pst1 = con.prepareStatement(deductQuantityQuery)) {
                pst1.setInt(1, quantitySold);
                pst1.setInt(2, itemId);
                pst1.executeUpdate();
            }

            // UPDATES FINANCIALS BASED ON SOLD ITEMS
            String updateMetricsQuery = 
                    "UPDATE FinancialMetrics SET " +
                            "totalRevenue = totalRevenue + (? * (SELECT revenuePerItem FROM Items WHERE id = ?)), " + 
                            "totalCost = totalCost + (? * (SELECT costPerItem FROM Items WHERE id = ?)), " +
                            "totalProfit = totalRevenue - totalCost WHERE id = 1";
            try (PreparedStatement pst2 = con.prepareStatement(updateMetricsQuery)) {
                pst2.setInt(1, quantitySold);
                pst2.setInt(2, itemId);
                pst2.setInt(3, quantitySold);
                pst2.setInt(4, itemId);
                pst2.executeUpdate();
            }

            con.commit();  // COMMITS TO THE G (JUST KIDDING, COMMITS THE TRANSACTION)
            return true;

        } 
        
        catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback();  // ROLLS BACK CHANGES
                } 
                
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
            return false;

        } finally {
            if (con != null) {
                try {
                    con.close();  // CLOSES CONNECTION MANUALLY
                } 
                
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean returnItem(int itemId, int quantityReturned) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);  // Begin transaction

            // Check if the returned quantity does not exceed the sold amount
            String checkSoldQuantityQuery = "SELECT soldQuantity, quantity FROM Items WHERE id = ?";
            try (PreparedStatement pst = con.prepareStatement(checkSoldQuantityQuery)) {
                pst.setInt(1, itemId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    int soldQuantity = rs.getInt("soldQuantity");
                    if (quantityReturned > soldQuantity) {
                        con.rollback();
                        return false;  // Returning more items than sold
                    }
                } else {
                    con.rollback();
                    return false;  // Item not found
                }
            }

            // Add the returned quantity back to the Items table and decrement soldQuantity
            String updateQuantityQuery = "UPDATE Items SET quantity = quantity + ?, soldQuantity = soldQuantity - ? WHERE id = ?";
            try (PreparedStatement pst1 = con.prepareStatement(updateQuantityQuery)) {
                pst1.setInt(1, quantityReturned);
                pst1.setInt(2, quantityReturned);
                pst1.setInt(3, itemId);
                pst1.executeUpdate();
            }

            // Update the FinancialMetrics based on the returned items
            String updateMetricsQuery = 
                    "UPDATE FinancialMetrics SET " +
                            "totalRevenue = totalRevenue - (? * (SELECT revenuePerItem FROM Items WHERE id = ?)), " + 
                            "totalCost = totalCost - (? * (SELECT costPerItem FROM Items WHERE id = ?)), " +
                            "totalProfit = totalRevenue - totalCost WHERE id = 1";
            try (PreparedStatement pst2 = con.prepareStatement(updateMetricsQuery)) {
                pst2.setInt(1, quantityReturned);
                pst2.setInt(2, itemId);
                pst2.setInt(3, quantityReturned);
                pst2.setInt(4, itemId);
                pst2.executeUpdate();
            }

            con.commit();  // Commit the transaction
            return true;

        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback();  // Roll back changes
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
            return false;

        } finally {
            if (con != null) {
                try {
                    con.close();  // Close the connection manually
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public boolean resetFinancialReport() {
    Connection con = null;
    try {
        con = DriverManager.getConnection(url, user, password);
        con.setAutoCommit(false);  // BEGINS TRANSACTION

        // UPDATES FINANCES TO INITIAL STATE
        String resetMetricsQuery = "UPDATE FinancialMetrics SET totalRevenue = 0, totalCost = 0, totalProfit = 0 WHERE id = 1";

        try (PreparedStatement pst = con.prepareStatement(resetMetricsQuery)) {
            pst.executeUpdate();
        }

        con.commit();  // COMMITS THE TRANSACTION
        return true;

    } 
    
    catch (SQLException ex) {
        if (con != null) {
            try {
                con.rollback();  // ROLLS BACK CHANGES
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ex.printStackTrace();
        return false;

    } 
    
    finally {
        if (con != null) {
            try {
                con.close();  // CLOSES CONNECTION MANUALLY
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
    public double getTotalCost() {
        String query = "SELECT totalCost FROM FinancialMetrics WHERE id = 1"; // METHOD TO GET TOTAL COST
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("totalCost");
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public void setTotalCost(double totalCost) {
        String query = "UPDATE FinancialMetrics SET totalCost = ? WHERE id = 1"; //METHOD TO SET TOTAL COST
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
         
            pst.setDouble(1, totalCost);
            pst.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public double getTotalRevenue() { // METHOD TO GET TOTAL REVENUE
        String query = "SELECT totalRevenue FROM FinancialMetrics WHERE id = 1";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public void setTotalRevenue(double totalRevenue) { // METHOD TO SET TOTAL REVENUE
        String query = "UPDATE FinancialMetrics SET totalRevenue = ? WHERE id = 1";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
         
            pst.setDouble(1, totalRevenue);
            pst.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public double getTotalProfit() { // METHOD TO GET TOTAL PROFIT
        String query = "SELECT totalProfit FROM FinancialMetrics WHERE id = 1";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("totalProfit");
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public void setTotalProfit(double totalProfit) { // METHOD TO SET TOTAL PROFIT
        String query = "UPDATE FinancialMetrics SET totalProfit = ? WHERE id = 1";
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {
         
            pst.setDouble(1, totalProfit);
            pst.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}