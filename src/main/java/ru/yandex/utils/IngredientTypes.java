package ru.yandex.utils;

public enum IngredientTypes {
    BUN("bun"),
    SAUCE("sauce");

    private final String value;

    IngredientTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
