package com.fges.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonGroceryDao implements GroceryDao {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String fileName;

    public JsonGroceryDao(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String, List<String>> getAllItems() throws IOException {
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try {
            String fileContent = Files.readString(filePath);
            if (fileContent.isEmpty()) {
                return new HashMap<>();
            }
            return OBJECT_MAPPER.readValue(fileContent,
                    new TypeReference<Map<String, List<String>>>() {});
        } catch (Exception e) {
            // If there's an error parsing, return an empty map
            return new HashMap<>();
        }
    }

    @Override
    public List<String> getItemsByCategory(String category) throws IOException {
        Map<String, List<String>> allItems = getAllItems();
        return allItems.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }

    @Override
    public void addItem(String category, String item) throws IOException {
        category = category.toLowerCase();
        Map<String, List<String>> allItems = getAllItems();

        // Add the item to the specified category
        allItems.putIfAbsent(category, new ArrayList<>());
        allItems.get(category).add(item);

        // Save the updated map
        saveAllItems(allItems);
    }

    @Override
    public void removeItemByName(String itemName) throws IOException {
        // Créer une nouvelle variable finale avec la valeur en minuscules
        final String finalItemName = itemName.toLowerCase();
        Map<String, List<String>> allItems = getAllItems();

        // Pour chaque catégorie, filtrer les articles avec le nom correspondant
        for (Map.Entry<String, List<String>> entry : allItems.entrySet()) {
            List<String> updatedList = entry.getValue().stream()
                    .filter(item -> {
                        String[] parts = item.split(",");
                        if (parts.length > 0) {
                            String article = parts[0].trim().toLowerCase();
                            return !article.equals(finalItemName); // Utiliser la variable finale ici
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            entry.setValue(updatedList);
        }

        // Sauvegarder la carte mise à jour
        saveAllItems(allItems);
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

    private void saveAllItems(Map<String, List<String>> items) throws IOException {
        OBJECT_MAPPER.writeValue(new File(fileName), items);
    }
}