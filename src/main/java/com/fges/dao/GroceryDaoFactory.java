package com.fges.dao;

/**
 * Factory for creating appropriate DAO based on format
 */
public class GroceryDaoFactory {
    /**
     * Creates a DAO instance based on the specified format
     *
     * @param fileName the name of the file to use for storage
     * @param format the format of the file (csv or json)
     * @return a GroceryDao instance
     */
    public static GroceryDao createDao(String fileName, String format) {
        // Ensure the file has the correct extension
        if (!(fileName.endsWith(".json") || fileName.endsWith(".csv"))) {
            fileName += "." + format;
        }

        if ("csv".equalsIgnoreCase(format)) {
            return new CsvGroceryDao(fileName);
        } else {
            return new JsonGroceryDao(fileName); // json is default
        }
    }
}