public class Item {
    private int id;
    private String name;
    private int quantity;
    private int lowStockThreshold;
    private double costPerItem;
    private double revenuePerItem;
    private double profitPerItem;

    public Item(int id, String name, int quantity, int lowStockThreshold, double costPerItem, double revenuePerItem) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.costPerItem = costPerItem;
        this.revenuePerItem = revenuePerItem;
        setProfitPerItem();

	}

	// getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
    
    public double getCostPerItem() {
        return costPerItem;
    }
    
    public double getRevenuePerItem() {
        return revenuePerItem;
    }
    
    public double getProfitPerItem() {
        return profitPerItem;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
    	this.quantity = quantity;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }    
    
    public void setCostPerItem(double costPerItem) {
    	this.costPerItem = costPerItem;
        setProfitPerItem();
    }
    
    public void setRevenuePerItem(double revenuePerItem) {
        this.revenuePerItem = revenuePerItem;
        setProfitPerItem();
    }
    
    public void setProfitPerItem() {
        this.profitPerItem = this.revenuePerItem - this.costPerItem;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", lowStockThreshold=" + lowStockThreshold +
                ", costPerItem=" + costPerItem +
                ", revenuePerItem=" + revenuePerItem + 
                '}';
    }
}
