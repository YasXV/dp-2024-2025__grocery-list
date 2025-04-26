package com.fges.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvGroceryDao implements GroceryDao {
    private static final String HEADER = "article,nombre,categorie";
    private final String fileName;

    public CsvGroceryDao(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String, List<String>> getAllItems() throws IOException {
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath) || Files.size(filePath) == 0) {
            return new HashMap<>();
        }

        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<String>> result = new HashMap<>();

        // Skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");

            if (parts.length >= 3) {
                String article = parts[0].trim();
                String quantity = parts[1].trim();
                String category = parts[2].trim().toLowerCase();

                String itemEntry = article + ", " + quantity;

                result.putIfAbsent(category, new ArrayList<>());
                result.get(category).add(itemEntry);
            }
        }

        return result;
    }

    @Override
    public List<String> getItemsByCategory(String category) throws IOException {
        Map<String, List<String>> allItems = getAllItems();
        return allItems.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }

    @Override
    public void addItem(String category, String item) throws IOException {
        category = category.toLowerCase();
        Path filePath = Paths.get(fileName);
        List<String> lines = new ArrayList<>();

        // Check if file exists and has content
        if (Files.exists(filePath) && Files.size(filePath) > 0) {
            lines = Files.readAllLines(filePath);
        }

        // If the file is empty or doesn't exist, add the header
        if (lines.isEmpty()) {
            lines.add(HEADER);
        }

        // Parse the item to extract name and quantity
        String[] itemParts = item.split(",");
        if (itemParts.length >= 2) {
            String name = itemParts[0].trim();
            String quantity = itemParts[1].trim();

            // Add new line with article, quantity, and category
            lines.add(name + "," + quantity + "," + category);
            Files.write(filePath, lines);
        }
    }

    @Override
    public void removeItemByName(String itemName) throws IOException {
        itemName = itemName.toLowerCase();
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath) || Files.size(filePath) == 0) {
            return;
        }

        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            return;
        }

        // Keep the header
        String header = lines.get(0);
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add(header);

        // Filter out lines with matching item name
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");

            if (parts.length > 0 && !parts[0].trim().toLowerCase().equals(itemName)) {
                updatedLines.add(line);
            }
        }

        Files.write(filePath, updatedLines);
    }

    @Override
    public void deleteAll() throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(Paths.get(fileName));
    }
}