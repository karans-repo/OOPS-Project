package knapsack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Inventoryknapsack {

    private static class Product {
        String productId;
        int cost;
        int profit;
        String productName;
        String category;

        public Product(String productId, int cost, int profit, String productName, String category) {
            this.productId = productId;
            this.cost = cost;
            this.profit = profit;
            this.productName = productName;
            this.category = category;
        }
    }

    public static void main(String[] args) {
        // Load products from .txt file
        List<Product> products = loadProducts("C:\\Users\\Karan\\Downloads\\Ecom_data.txt");

        // Input budget
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter budget (in dollars): ");
        int budget = scanner.nextInt();

        // Input category
        System.out.print("Enter product category (or type 'All' for all categories): ");
        scanner.nextLine(); // Consume the newline
        String category = scanner.nextLine();

        // Filter products by category if specified
        List<Product> filteredProducts;
        if (category.equalsIgnoreCase("All")) {
            filteredProducts = products; // Use all products
        } else {
            filteredProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.category.equalsIgnoreCase(category)) {
                    filteredProducts.add(product);
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            System.out.println("No products found in the selected category.");
        } else {
            // Calculate the optimal selection
            List<Product> selectedProducts = getMaximizedProfitProducts(filteredProducts, budget);

            // Display selected products
            System.out.println("\nSelected products within budget:");
            int totalCost = 0;
            int totalProfit = 0;
            for (Product product : selectedProducts) {
                System.out.println("Product ID: " + product.productId + ", Cost: " + product.cost +
                                   ", Profit: " + product.profit + ", Category: " + product.category +
                                   ", Product Name: " + product.productName);
                totalCost += product.cost;
                totalProfit += product.profit;
            }
            System.out.println("Total Cost: " + totalCost + " | Total Profit: " + totalProfit);
        }
        
        scanner.close();
    }

    private static List<Product> loadProducts(String filename) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Split by comma (if file is comma-separated)

                if (values.length < 9) {
                    continue;
                }

                String productId = values[2].trim(); // Column 3 (index 2)
                String category = values[3].trim();  // Column 4 (index 3)
                String productName = values[8].trim(); // Column 9 (index 8)
                double sellingPrice = 0.0;
                double profit = 0.0;

                try {
                    sellingPrice = Double.parseDouble(values[5].trim());  // Column 6 (index 5)
                    profit = Double.parseDouble(values[7].trim());  // Column 8 (index 7)
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid row (invalid number format): " + line);
                    continue;
                }

                int cost = (int) Math.round(sellingPrice - profit);
                products.add(new Product(productId, cost, (int) Math.round(profit), productName, category));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return products;
    }

    private static List<Product> getMaximizedProfitProducts(List<Product> products, int budget) {
        int n = products.size();
        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            Product product = products.get(i - 1);
            for (int j = 0; j <= budget; j++) {
                if (product.cost <= j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - product.cost] + product.profit);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        List<Product> selectedProducts = new ArrayList<>();
        int remainingBudget = budget;
        for (int i = n; i > 0 && remainingBudget > 0; i--) {
            if (dp[i][remainingBudget] != dp[i - 1][remainingBudget]) {
                Product product = products.get(i - 1);
                selectedProducts.add(product);
                remainingBudget -= product.cost;
            }
        }

        return selectedProducts;
    }
}
