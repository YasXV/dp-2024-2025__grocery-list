package com.fges.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for grocery items
 */
public interface GroceryDao {
    /**
     * Retrieves all grocery items grouped by category
     * @return Map of categories to lists of items
     * @throws IOException if there's an error reading data
     */
    Map<String, List<String>> getAllItems() throws IOException;

    /**
     * Retrieves items for a specific category
     * @param category the category to retrieve items for
     * @return List of items in the category
     * @throws IOException if there's an error reading data
     */
    List<String> getItemsByCategory(String category) throws IOException;

    /**
     * Adds an item to a specific category
     * @param category the category to add the item to
     * @param item the item to add (format: "name, quantity")
     * @throws IOException if there's an error writing data
     */
    void addItem(String category, String item) throws IOException;

    /**
     * Removes all items with the given name
     * @param itemName the name of the item to remove
     * @throws IOException if there's an error writing data
     */
    void removeItemByName(String itemName) throws IOException;

    /**
     * Deletes the entire data store
     * @throws IOException if there's an error deleting data
     */
    void deleteAll() throws IOException;

    /**
     * Checks if the data store exists
     * @return true if the data store exists, false otherwise
     */
    boolean exists();
}