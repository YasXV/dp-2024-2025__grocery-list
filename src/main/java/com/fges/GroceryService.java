package com.fges;

import com.fges.dao.GroceryDao;
import com.fges.dao.GroceryDaoFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service handling grocery list operations
 */
public class GroceryService {
    private final GroceryDao dao;
    private final String category;
    private final String format;

    public GroceryService(String fileName, String format, String category) {
        this.format = format.toLowerCase();
        this.category = category.toLowerCase();
        this.dao = GroceryDaoFactory.createDao(fileName, format);
    }

    public int executeCommand(String command, List<String> args) throws IOException {
        if ("add".equals(command)) {
            return addItem(args);
        } else if ("list".equals(command)) {
            return listItems();
        } else if ("remove".equals(command)) {
            return removeItem(args);
        } else if ("delete".equals(command)) {
            return deleteFile();
        } else if ("info".equals(command)) {
            return displayInfo();
        } else {
            System.err.println("Unknown command: " + command);
            return 1;
        }
    }

    private int addItem(List<String> args) throws IOException {
        if (args.size() < 2) {
            System.err.println("Missing arguments. Usage: add <item_name> <quantity>");
            return 1;
        }

        String itemName = args.get(0);
        int quantity;

        try {
            quantity = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            System.err.println("Quantity must be a number");
            return 1;
        }

        String item = itemName + ", " + quantity;
        dao.addItem(category, item);
        return 0;
    }

    private int listItems() throws IOException {
        Map<String, List<String>> categorizedItems = dao.getAllItems();

        if (categorizedItems.isEmpty()) {
            System.out.println("No items found.");
            return 0;
        }

        for (var entry : categorizedItems.entrySet()) {
            System.out.println("# " + entry.getKey() + ":");
            for (String item : entry.getValue()) {
                System.out.println("  " + item);
            }
        }

        return 0;
    }

    private int removeItem(List<String> args) throws IOException {
        if (args.isEmpty()) {
            System.err.println("Missing arguments. Usage: remove <item_name>");
            return 1;
        }

        String itemName = args.get(0).toLowerCase();

        if (!dao.exists()) {
            System.err.println("No grocery list found.");
            return 1;
        }

        dao.removeItemByName(itemName);
        return 0;
    }

    private int deleteFile() throws IOException {
        if (!format.equals("json") && !format.equals("csv")) {
            System.err.println("Delete command only supports json and csv formats.");
            return 1;
        }

        if (dao.exists()) {
            dao.deleteAll();
            System.out.println("File deleted successfully.");
            return 0;
        } else {
            System.err.println("File not found.");
            return 1;
        }
    }

    private int displayInfo() {
        // Display current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        System.out.println("Date: " + currentDate);

        // Display OS information
        String osName = System.getProperty("os.name");
        System.out.println("Operating System: " + osName);

        // Display Java version
        String javaVersion = System.getProperty("java.version");
        System.out.println("Java Version: " + javaVersion);

        return 0;
    }
}