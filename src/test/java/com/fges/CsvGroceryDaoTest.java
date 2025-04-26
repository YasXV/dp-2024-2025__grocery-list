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

class CsvGroceryDaoTest {

    @TempDir
    Path tempDir;

    private String testFilePath;
    private CsvGroceryDao dao;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test-groceries.csv").toString();
        dao = new CsvGroceryDao(testFilePath);
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
        assertThat(items).contains("Apple, 5", "Banana, 3");
    }

    @Test
    void should_write_csv_header_on_first_add() throws IOException {
        // When
        dao.addItem("fruits", "Apple, 5");

        // Then
        List<String> lines = Files.readAllLines(Path.of(testFilePath));
        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo("article,nombre,categorie");
    }

    @Test
    void should_remove_item_by_name() throws IOException {
        // Given
        dao.addItem("fruits", "Apple, 5");
        dao.addItem("fruits", "Banana, 3");

        // When
        dao.removeItemByName("Apple");
        List<String> items = dao.getItemsByCategory("fruits");

        // Then
        assertThat(items).hasSize(1);
        assertThat(items).containsExactly("Banana, 3");
    }

    @Test
    void should_handle_case_insensitive_operations() throws IOException {
        // Given
        dao.addItem("Fruits", "Apple, 5");  // Uppercase category

        // When - Get with lowercase category
        List<String> items = dao.getItemsByCategory("fruits");

        // Then
        assertThat(items).hasSize(1);

        // When - Remove with lowercase name
        dao.removeItemByName("apple");
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