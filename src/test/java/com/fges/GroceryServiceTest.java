package com.fges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class GroceryServiceTest {

    @TempDir
    Path tempDir;

    private String testFilePath;
    private GroceryService service;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test-groceries.json").toString();
        service = new GroceryService(testFilePath, "json", "default");

        // Set up System.out capture
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void should_add_item_successfully() throws IOException {
        // When
        int result = service.executeCommand("add", Arrays.asList("Apple", "5"));

        // Then
        assertThat(result).isEqualTo(0);

        // Verify item was added by listing
        service.executeCommand("list", Arrays.asList());
        assertThat(outContent.toString()).contains("Apple, 5");
    }

    @Test
    void should_return_error_when_add_with_missing_args() throws IOException {
        // When
        int result = service.executeCommand("add", Arrays.asList("Apple"));

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(outContent.toString()).contains("Missing arguments");
    }

    @Test
    void should_return_error_when_add_with_non_numeric_quantity() throws IOException {
        // When
        int result = service.executeCommand("add", Arrays.asList("Apple", "five"));

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(outContent.toString()).contains("Quantity must be a number");
    }

    @Test
    void should_remove_item_successfully() throws IOException {
        // Given
        service.executeCommand("add", Arrays.asList("Apple", "5"));
        service.executeCommand("add", Arrays.asList("Banana", "3"));
        outContent.reset();

        // When
        int result = service.executeCommand("remove", Arrays.asList("Apple"));

        // Then
        assertThat(result).isEqualTo(0);

        // Verify item was removed
        outContent.reset();
        service.executeCommand("list", Arrays.asList());
        String output = outContent.toString();
        assertThat(output).contains("Banana, 3");
        assertThat(output).doesNotContain("Apple, 5");
    }

    @Test
    void should_display_info_correctly() throws IOException {
        // When
        int result = service.executeCommand("info", Arrays.asList());

        // Then
        assertThat(result).isEqualTo(0);
        String output = outContent.toString();
        assertThat(output).contains("Date:");
        assertThat(output).contains("Operating System:");
        assertThat(output).contains("Java Version:");
    }

    @Test
    void should_return_error_for_unknown_command() throws IOException {
        // When
        int result = service.executeCommand("unknown", Arrays.asList());

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(outContent.toString()).contains("Unknown command");
    }

    @Test
    void should_delete_file_successfully() throws IOException {
        // Given
        service.executeCommand("add", Arrays.asList("TestItem", "1"));
        outContent.reset();

        // When
        int result = service.executeCommand("delete", Arrays.asList());

        // Then
        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("File deleted successfully");
    }
}