package com.fges;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroceryServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void should_load_items_from_json() throws IOException {
        // arrange
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "[\"Milk: 10\",\"Bread: 2\"]");

        GroceryService service = new GroceryService(jsonFile.toString(), "json");

        // act
        List<String> items = service.getItems();

        // assert
        assertThat(items).hasSize(2);
        assertThat(items).containsExactly("Milk: 10", "Bread: 2");
    }

    @Test
    void should_load_items_from_csv() throws IOException {
        // arrange
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "Milk: 10\nBread: 2");

        GroceryService service = new GroceryService(csvFile.toString(), "csv");

        // act
        List<String> items = service.getItems();

        // assert
        assertThat(items).hasSize(2);
        assertThat(items).containsExactly("Milk: 10", "Bread: 2");
    }

    @Test
    void should_save_items_to_json() throws IOException {
        // arrange
        Path jsonFile = tempDir.resolve("test.json");
        GroceryService service = new GroceryService(jsonFile.toString(), "json");
        List<String> items = Arrays.asList("Milk: 10", "Bread: 2");

        // act
        service.saveItems(items);
        String content = Files.readString(jsonFile);

        // Tassert
        assertThat(content).contains("Milk: 10");
        assertThat(content).contains("Bread: 2");
    }

    @Test
    void should_save_items_to_csv() throws IOException {
        // arrange
        Path csvFile = tempDir.resolve("test.csv");
        GroceryService service = new GroceryService(csvFile.toString(), "csv");
        List<String> items = Arrays.asList("Milk: 10", "Bread: 2");

        // act
        service.saveItems(items);
        List<String> lines = Files.readAllLines(csvFile);

        //  assert
        assertThat(lines).hasSize(2);
        assertThat(lines).containsExactly("Milk: 10", "Bread: 2");
    }

    @Test
    void should_add_item() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "[]");

        GroceryService service = new GroceryService(jsonFile.toString(), "json");

        // When
        int result = service.executeCommand("add", Arrays.asList("Milk", "10"));

        // Then
        assertThat(result).isEqualTo(0);
        List<String> items = service.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo("Milk: 10");
    }

    @Test
    void should_list_items() throws IOException {
        // arrange
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "[\"Milk: 10\",\"Bread: 2\"]");

        GroceryService service = new GroceryService(jsonFile.toString(), "json");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // act
        int result = service.executeCommand("list", List.of());

        // assert
        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk: 10");
        assertThat(outContent.toString()).contains("Bread: 2");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_remove_item() throws IOException {
        // arrange
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "[\"Milk: 10\",\"Bread: 2\"]");

        GroceryService service = new GroceryService(jsonFile.toString(), "json");

        // act
        int result = service.executeCommand("remove", List.of("Milk"));

        // assert
        assertThat(result).isEqualTo(0);
        List<String> items = service.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo("Bread: 2");
    }
}