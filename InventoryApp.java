import java.util.List;
import java.util.Scanner;

public class InventoryApp {
    private static Database database = new Database();
    private static FinancialReport financialReport = new FinancialReport(database);
    
    public static void main(String[] args) {
        try (Scanner keyboard = new Scanner(System.in)) {
            while (true) {
                
            	System.out.println("1. Display items"); // THIS IS THE MAIN MENU
                System.out.println("2. Add item");
                System.out.println("3. Update item");
                System.out.println("4. Delete item");
                System.out.println("5. Sell item");
                System.out.println("6. Return item");
                System.out.println("7. Display Financials");
                System.out.println("8. Delete Financials");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");
                
                String choice = keyboard.next();

                while (!("1".equals(choice) || "2".equals(choice) || "3".equals(choice) || "4".equals(choice) || "5".equals(choice) || "6".equals(choice) || "7".equals(choice) || "8".equals(choice) || "9".equals(choice))) {
                    System.out.println("INVALID OPTION: Please enter a number that corresponds with a database option");
                    System.out.println("Example: Entering the integer '1' will select the 'Display items' option");
                    System.out.print("Choose an option: ");
                    choice = keyboard.next();
                }

                if (choice.equals("1")) { // MAIN MENU WILL LEAD TO THE FOLLOWING
                    displayItems();
                } 
                
                else if (choice.equals("2")) {
                    createItem(keyboard);
                } 
                
                else if (choice.equals("3")) {
                    updateItem(keyboard);
                } 
                
                else if (choice.equals("4")) {
                    deleteItem(keyboard);
                } 
                
                else if (choice.equals("5")) {
                	displayItems();
                	System.out.print("Enter the ID of the item to sell: ");
                    int itemId = keyboard.nextInt();
                    System.out.print("Enter the quantity to sell: ");
                    int quantitySold = keyboard.nextInt();
                    if (database.sellItem(itemId, quantitySold)) {
                        System.out.println("Item sold successfully!");
                    } 
                    else {
                        System.out.println("Error! Item could not be sold.");
                     }
                	
                }
                
                else if (choice.equals("6")) {
                	displayItems();
                	System.out.print("Enter the ID of the item to return: ");
                    int returnItemId = keyboard.nextInt();
                    System.out.print("Enter the quantity to return: ");
                    int quantityReturned = keyboard.nextInt();
                    
                    if (database.returnItem(returnItemId, quantityReturned)) {
                        System.out.println("Item returned successfully!");
                    } 
                    else {
                        System.out.println("Error! Item could not be returned.");
                    }
                    
                }
                
                else if (choice.equals("7")) {
                    financialReport.displayFinancials();
                }
                
                else if (choice.equals("8")) {
                    System.out.print("WARNING THIS WILL PERMANENTLY DELETE YOUR FINANCIAL RECORDS. DO YOU WISH TO CONTINUE? Y/N: ");
                    
                    while (true) {
                        String confirmation = keyboard.nextLine().trim().toUpperCase();

                        if (confirmation.equals("Y")) {
                            boolean result = database.resetFinancialReport();
                            
                            if (result) {
                                System.out.println("Financial records successfully reset.");
                            } else {
                                System.out.println("An error occurred while resetting financial records.");
                            }
                            break;
                        } 
                        else if (confirmation.equals("N")) {
                            break;  
                        } 
                        else {
                            System.out.print("Please answer with 'Y' or 'N': ");
                        }
                    }
                }

                
                else if (choice.equals("9")) {
                    exit();
                } 
                
                else // Thought this would be a fun read =)
                {
                	System.out.println("");
    			    System.out.println("On one occasion, while Diogenes was sunning himself in Corinth, he was approached by");
    			    System.out.println("Alexander the Great, the most powerful man of his era. In admiration of the philosopher, ");
    			    System.out.println("Alexander said he would fulfill any wish or request Diogenes might have.");
    			    System.out.println("Without even glancing at the powerful conqueror, Diogenes simply responded, \"Stand out of");
    			    System.out.print("my sunlight.\"");
    			    System.out.println("Taken aback, yet also impressed by the philosopher's indifference to his power and wealth, ");
    			    System.out.println("Alexander remarked, \"If I were not Alexander, I would wish to be Diogenes.\"");
    			    
                }
            }
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void displayItems() { // DISPLAYS ITEM LIST
        List<Item> items = database.getAllItems();
        for (Item item : items) {
            System.out.println(item);
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createItem(Scanner keyboard) {
  
        System.out.print("Enter new item name: "); // SETS ITEM NAME
        keyboard.nextLine();
        String name = keyboard.nextLine();
        while(name.trim().isEmpty()) {
        	System.out.println("INVALID INPUT: Please enter a non-empty String");
        	name = keyboard.nextLine();
        }
        
        System.out.print("Enter item quantity: "); // SETS ITEM QUANTITY
        while (!keyboard.hasNextInt()) { 
            System.out.println();
        	System.out.print("Please enter a valid integer: ");
            keyboard.nextInt();  // Consume the invalid input
        }
        int quantity = keyboard.nextInt();
        while(quantity < 0) {
        	System.out.println();
        	System.out.print("Please enter a non-negative integer: ");
        	quantity = keyboard.nextInt();
        }
      

        System.out.print("Enter low stock threshold: "); // SETS LOW STOCK THRESHOLD
        while (!keyboard.hasNextInt()) { 
            System.out.print("Please enter a valid integer: ");
            keyboard.next();  // Consume the invalid input
        }
        int lowStockThreshold = keyboard.nextInt();
        while(lowStockThreshold < 0) {
            System.out.println();
        	System.out.print("Please enter a non-negative integer: ");
            lowStockThreshold = keyboard.nextInt();
        }


        System.out.print("Enter the cost per item (how much you paid): "); // SETS COST PER ITEM
        while (!keyboard.hasNextDouble()) { 
            System.out.println();
        	System.out.print("Please enter a valid double.");
            keyboard.next();  // Consume the invalid input
        }
        double costPerItem = keyboard.nextDouble();
        while(costPerItem < 0) {
            System.out.print("Please enter a non-negative double: ");
            costPerItem = keyboard.nextDouble();
        }
        
        System.out.print("Enter the revenue per item (how much customer paid): "); // SETS REVENUE PER ITEM
        while (!keyboard.hasNextDouble()) { 
            System.out.println("Please enter a valid double: ");
            keyboard.next();  // Consume the invalid input
        }
        double revenuePerItem = keyboard.nextDouble();
        while(costPerItem < 0) {
            System.out.print("Please enter a non-negative double: ");
            revenuePerItem = keyboard.nextDouble();
        }

        Item item = new Item(0, name, quantity, lowStockThreshold, costPerItem, revenuePerItem); //SETS ITEM ITSELF USING VALUES ABOVE
        if (database.addItem(item)) {
            System.out.println("Item added successfully!");
        } 
        else {
            System.out.println("Error adding the item.");
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void updateItem(Scanner keyboard) {
        displayItems();
        System.out.print("Enter the ID of the item you want to update: "); //CONNECTS WITH SQL TO FIND ITEM
        int id = keyboard.nextInt();
        
        System.out.print("Enter updated item name: "); // SETS TO UPDATED ITEM NAME
        keyboard.nextLine();
        String name = keyboard.nextLine();
        while(name.trim().isEmpty()) {
        	System.out.println("INVALID INPUT: Please enter a non-empty String");
        	name = keyboard.nextLine();
        }
        
        System.out.print("Enter updated item quantity: "); // SETS TO UPDATED ITEM QUANTITY
        while (!keyboard.hasNextInt()) { 
            System.out.println();
        	System.out.print("Please enter a valid integer: ");
            keyboard.nextInt();  // Consume the invalid input DO NOT TOUCH
        }
        int quantity = keyboard.nextInt();
        while(quantity < 0) {
        	System.out.println();
        	System.out.print("Please enter a non-negative integer: ");
        	quantity = keyboard.nextInt();
        }

        System.out.print("Enter updated low stock threshold: "); // SETS UPDATED LOW STOCK THRESHOLD
        while (!keyboard.hasNextInt()) { 
            System.out.print("Please enter a valid integer: ");
            keyboard.next();  // Consume the invalid input DO NOT TOUCH
        }
        int lowStockThreshold = keyboard.nextInt();
        while(lowStockThreshold < 0) {
            System.out.println();
        	System.out.print("Please enter a non-negative integer: ");
            lowStockThreshold = keyboard.nextInt();
        }


        System.out.print("Enter the updated cost per item (how much you paid): "); // SETS UPDATED COST PER ITEM
        while (!keyboard.hasNextDouble()) { 
            System.out.println();
        	System.out.print("Please enter a valid double.");
            keyboard.next();  // Consume the invalid input DO NOT TOUCH
        }
        double costPerItem = keyboard.nextDouble();
        while(costPerItem < 0) {
            System.out.print("Please enter a non-negative double: ");
            costPerItem = keyboard.nextDouble();
        }
        
        System.out.print("Enter the updated revenue per item (how much customer paid): "); // SETS UPDATED REVENUE PER ITEM
        while (!keyboard.hasNextDouble()) { 
            System.out.println("Please enter a valid double: ");
            keyboard.next();  // Consume the invalid input DO NOT TOUCH
        }
        double revenuePerItem = keyboard.nextDouble();
        while(revenuePerItem < 0) {
            System.out.print("Please enter a non-negative double: ");
            revenuePerItem = keyboard.nextDouble();
        }

        Item item = new Item(id, name, quantity, lowStockThreshold, costPerItem, revenuePerItem); // SETS UPDATED ITEM USING VALUES ABOVE
        if (database.updateItem(item)) {
            System.out.println("Item updated successfully!");
        } 
        else {
            System.out.println("Error updating the item.");
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void deleteItem(Scanner keyboard) { // DELETES ITEMS 
        displayItems();

        System.out.print("Enter the ID of the item you want to delete:");
        int id = keyboard.nextInt();

        if (database.deleteItem(id)) {
            System.out.println("Item deleted successfully!");
        } 
        
        else {
            System.out.println("Error deleting the item.");
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void exit() { // ... PRETTY SELF-EXPLANATORY 
        System.out.print("Exiting the Program...");
        System.exit(0);
    }
}
