package com.fges;

import java.io.IOException;

/**
 * Main application entry point
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.exit(exec(args));
    }

    public static int exec(String[] args) throws IOException {
        // Parse options
        MyOptions options = new MyOptions();
        if (!options.parse(args)) {
            return 1;
        }

        try {
            // Create grocery service with the appropriate DAO
            GroceryService service = new GroceryService(
                    options.getSourceFile(),
                    options.getFormat(),
                    options.getCategory()
            );

            // Execute the command
            return service.executeCommand(options.getCommand(), options.getCommandArgs());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}