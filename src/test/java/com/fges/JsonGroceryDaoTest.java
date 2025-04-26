package com.fges.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonGroceryDaoTest {

    @TempDir
    Path tempDir;

    private String testFilePath;
    private JsonGroceryDao dao;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test-groceries.json").toString();
        dao = new JsonGroceryDao(testFilePath);
    }

    @Test
    void should_return_empty_map_when_file_not_exists() throws IOException {
        // When
        Map<String, List<String>> items = dao.getAllItems();

        // Then
        assertThat(items).isEmpty();
    }

    @Test
    void should_add_and_retrieve_items() throws IOException {
        // Given
        String category = "fruits";
        String item = "Apple, 5";

        // When
        dao.addItem(category, item);
        Map<String, List<String>> allItems = dao.getAllItems();

        // Then
        assertThat(allItems).hasSize(1);
        assertThat(allItems).containsKey("fruits");
        assertThat(allItems.get("fruits")).containsExactly("Apple, 5");
    }

    @Test
    void should_add_multiple_items_to_same_category() throws IOException {
        // Given
        String category = "fruits";

        // When
        dao.addItem(category, "Apple, 5");
        dao.addItem(category, "Banana, 3");
        List<String> items = dao.getItemsByCategory(category);

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).containsExactly("Apple, 5", "Banana, 3");
    }

    @Test
    void should_remove_item_by_name() throws IOException {
        // Given
        dao.addItem("fruits", "Apple, 5");
        dao.addItem("fruits", "Banana, 3");

        // When
        dao.removeItemByName("Apple");
        Map<String, List<String>> allItems = dao.getAllItems();

        // Then
        assertThat(allItems.get("fruits")).hasSize(1);
        assertThat(allItems.get("fruits")).containsExactly("Banana, 3");
    }

    @Test
    void should_handle_case_insensitive_category_and_item_names() throws IOException {
        // Given
        dao.addItem("Fruits", "Apple, 5");  // Uppercase category

        // When
        List<String> items = dao.getItemsByCategory("fruits");  // Lowercase category

        // Then
        assertThat(items).hasSize(1);
        assertThat(items).containsExactly("Apple, 5");

        // When
        dao.removeItemByName("apple");  // Lowercase item name
        items = dao.getItemsByCategory("fruits");

        // Then
        assertThat(items).isEmpty();
    }

    @Test
    void should_delete_file_when_deleteAll_is_called() throws IOException {
        // Given
        dao.addItem("fruits", "Apple, 5");
        assertThat(Files.exists(Path.of(testFilePath))).isTrue();

        // When
        dao.deleteAll();

        // Then
        assertThat(Files.exists(Path.of(testFilePath))).isFalse();
    }
}