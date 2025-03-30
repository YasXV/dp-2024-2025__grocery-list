package com.fges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroceryService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String fileName;
    private final String format;

    public GroceryService(String fileName, String format) {
        this.fileName = fileName;
        this.format = format.toLowerCase();
    }

    public List<String> getItems() throws IOException {
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        if ("csv".equals(format)) {
            return Files.readAllLines(filePath);
        } else {
            // Default to JSON
            String fileContent = Files.readString(filePath);
            var parsedList = OBJECT_MAPPER.readValue(fileContent, new TypeReference<List<String>>() {});
            return new ArrayList<>(parsedList);
        }
    }

    public void saveItems(List<String> items) throws IOException {
        Path filePath = Paths.get(fileName);

        if ("csv".equals(format)) {
            Files.write(filePath, items);
        } else {
            // Default to JSON
            var outputFile = new File(fileName);
            OBJECT_MAPPER.writeValue(outputFile, items);
        }
    }

    public int executeCommand(String command, List<String> args) throws IOException {
        List<String> groceryList = getItems();

        switch (command) {
            case "add" -> {
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

                groceryList.add(itemName + ": " + quantity);
                saveItems(groceryList);
                return 0;
            }

            case "list" -> {
                for (String item : groceryList) {
                    System.out.println(item);
                }
                return 0;
            }

            case "remove" -> {
                if (args.isEmpty()) {
                    System.err.println("Missing arguments. Usage: remove <item_name>");
                    return 1;
                }

                String itemName = args.get(0);
                var newGroceryList = groceryList.stream()
                        .filter(item -> !item.contains(itemName))
                        .collect(Collectors.toList());

                saveItems(newGroceryList);
                return 0;
            }

            default -> {
                System.err.println("Unknown command: " + command);
                return 1;
            }
        }
    }
}