public class FinancialReport {
    private Database database;

    public FinancialReport(Database database) {
        this.database = database;
    }

    public void displayFinancials() {
    	System.out.println(String.format("Total Cost of Goods Sold: $%.2f", database.getTotalCost()));
        System.out.println(String.format("Total Revenue: $%.2f", database.getTotalRevenue()));
        System.out.println(String.format("Total Profit: $%.2f", database.getTotalProfit()));
    }
}


