package ru.yandex.dto.requests;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequestData {
    List<String> ingredients;

    public CreateOrderRequestData addIngredient(String ingredientId) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredientId);
        return this;
    }

    public CreateOrderRequestData addAllIngredients(List<String> ingredientIds) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        if (!ingredientIds.isEmpty()) {
            this.ingredients.addAll(ingredientIds);
        }
        return this;
    }
}
