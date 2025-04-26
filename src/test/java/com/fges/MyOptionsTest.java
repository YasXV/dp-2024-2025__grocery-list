package com.fges;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyOptionsTest {

    @Test
    void should_parse_valid_arguments_with_format() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.json", "-t", "json", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.json");
        assertThat(options.getFormat()).isEqualTo("json");
        assertThat(options.getCommand()).isEqualTo("list");
        assertThat(options.getCommandArgs()).isEmpty();
    }

    @Test
    void should_parse_valid_arguments_with_csv_format() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.csv", "-t", "csv", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.csv");
        assertThat(options.getFormat()).isEqualTo("csv");
        assertThat(options.getCommand()).isEqualTo("list");
        assertThat(options.getCommandArgs()).isEmpty();
    }

    @Test
    void should_use_default_format_when_not_specified() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.json", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.json");
        assertThat(options.getFormat()).isEqualTo("json"); // Default format
        assertThat(options.getCommand()).isEqualTo("list");
    }

    @Test
    void should_parse_with_category_option() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.json", "-c", "fruits", "add", "apple", "5"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.json");
        assertThat(options.getCategory()).isEqualTo("fruits");
        assertThat(options.getCommand()).isEqualTo("add");
        assertThat(options.getCommandArgs()).containsExactly("apple", "5");
    }

    @Test
    void should_fail_when_unknown_format() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.xml", "-t", "xml", "list"};  // xml is not supported

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_fail_when_no_command() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-f", "groceries.json"};  // Missing command

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isFalse();
    }
}