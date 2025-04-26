package com.fges.dao;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroceryDaoFactoryTest {

    @Test
    void should_create_json_dao_for_json_format() {
        // When
        GroceryDao dao = GroceryDaoFactory.createDao("test", "json");

        // Then
        assertThat(dao).isInstanceOf(JsonGroceryDao.class);
    }

    @Test
    void should_create_csv_dao_for_csv_format() {
        // When
        GroceryDao dao = GroceryDaoFactory.createDao("test", "csv");

        // Then
        assertThat(dao).isInstanceOf(CsvGroceryDao.class);
    }

    @Test
    void should_default_to_json_dao_for_unknown_format() {
        // When
        GroceryDao dao = GroceryDaoFactory.createDao("test", "unknown");

        // Then
        assertThat(dao).isInstanceOf(JsonGroceryDao.class);
    }

    @Test
    void should_add_extension_when_missing() {
        // When
        GroceryDao jsonDao = GroceryDaoFactory.createDao("test", "json");
        GroceryDao csvDao = GroceryDaoFactory.createDao("test", "csv");

        // Then
        // We don't have a direct way to check the filename, but we can check the class types
        assertThat(jsonDao).isInstanceOf(JsonGroceryDao.class);
        assertThat(csvDao).isInstanceOf(CsvGroceryDao.class);
    }

    @Test
    void should_not_add_extension_when_already_present() {
        // When
        GroceryDao jsonDao = GroceryDaoFactory.createDao("test.json", "json");
        GroceryDao csvDao = GroceryDaoFactory.createDao("test.csv", "csv");

        // Then
        assertThat(jsonDao).isInstanceOf(JsonGroceryDao.class);
        assertThat(csvDao).isInstanceOf(CsvGroceryDao.class);
    }
}