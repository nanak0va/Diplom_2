package ru.yandex.dto.responses;

import java.util.List;
import lombok.Data;
import ru.yandex.dto.entity.IngredientData;

@Data
public class GetIngredientsResponseData {
    private boolean success;
    private List<IngredientData> data;
}
