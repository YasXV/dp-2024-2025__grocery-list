package com.fges;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void should_use_default_json_format_when_not_specified() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("groceries.json");
        String jsonPath = jsonFile.toString();

        // When: Add item without specifying format
        int addResult = Main.exec(new String[]{
                "-f", jsonPath,
                "add", "Milk", "10"
        });

        // Then
        assertThat(addResult).isEqualTo(0);

        // When: List items
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int listResult = Main.exec(new String[]{
                "-f", jsonPath,
                "list"
        });

        // Then
        assertThat(listResult).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk, 10");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_work_with_csv_format() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("groceries.csv");
        String csvPath = csvFile.toString();

        // When: Add item
        int addResult = Main.exec(new String[]{
                "-f", csvPath,
                "-t", "csv",
                "add", "Milk", "10"
        });

        // Then
        assertThat(addResult).isEqualTo(0);

        // When: List items
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int listResult = Main.exec(new String[]{
                "-f", csvPath,
                "-t", "csv",
                "list"
        });

        // Then
        assertThat(listResult).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk, 10");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_remove_items_correctly() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("grocery-remove-test.json");
        String jsonPath = jsonFile.toString();

        // Add items
        Main.exec(new String[]{"-f", jsonPath, "add", "Apple", "5"});
        Main.exec(new String[]{"-f", jsonPath, "add", "Banana", "3"});

        // When: Remove item
        int removeResult = Main.exec(new String[]{
                "-f", jsonPath,
                "remove", "Apple"
        });

        // Then
        assertThat(removeResult).isEqualTo(0);

        // Check that only Banana remains
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.exec(new String[]{"-f", jsonPath, "list"});

        String output = outContent.toString();
        assertThat(output).contains("Banana, 3");
        assertThat(output).doesNotContain("Apple, 5");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_handle_categories_correctly() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("grocery-categories-test.json");
        String jsonPath = jsonFile.toString();

        // Add items to different categories
        Main.exec(new String[]{"-f", jsonPath, "-c", "fruits", "add", "Apple", "5"});
        Main.exec(new String[]{"-f", jsonPath, "-c", "dairy", "add", "Milk", "2"});

        // When: List all items
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int listResult = Main.exec(new String[]{
                "-f", jsonPath,
                "list"
        });

        // Then
        assertThat(listResult).isEqualTo(0);
        String output = outContent.toString();

        // Check categories appear correctly
        assertThat(output).contains("# fruits:");
        assertThat(output).contains("Apple, 5");
        assertThat(output).contains("# dairy:");
        assertThat(output).contains("Milk, 2");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_delete_file_correctly() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("grocery-delete-test.json");
        String jsonPath = jsonFile.toString();

        // Add an item
        Main.exec(new String[]{"-f", jsonPath, "add", "TestItem", "1"});

        // When: Delete file
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int deleteResult = Main.exec(new String[]{
                "-f", jsonPath,
                "delete"
        });

        // Then
        assertThat(deleteResult).isEqualTo(0);
        assertThat(outContent.toString()).contains("File deleted successfully");

        // Check file is gone
        assertThat(jsonFile.toFile().exists()).isFalse();

        // Restore stdout
        System.setOut(System.out);
    }
}