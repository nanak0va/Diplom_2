package ru.yandex.api;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static ru.yandex.api.Endpoints.*;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.yandex.dto.entity.IngredientData;
import ru.yandex.dto.requests.CreateOrderRequestData;
import ru.yandex.dto.responses.GetIngredientsResponseData;
import ru.yandex.model.AccessTokens;
import ru.yandex.utils.IngredientTypes;

public class OrderService extends AbstractService {

    public OrderService() {
        super(SB_API_URL + BASE_URL, "application/json");
    }

    @Step("Отправляем запрос на создание заказа")
    public ValidatableResponse sendCreateOrder(CreateOrderRequestData request, AccessTokens accessToken) {
        return post(CREATE_ORDER, accessToken, request).then();
    }

    @Step("Отправляем запрос на получение всех ингредиентов")
    public ValidatableResponse sendGetAllIngredients(AccessTokens accessToken) {
        return get(GET_INGREDIENTS, accessToken).then();
    }

    public List<IngredientData> getAvailableIngredients(AccessTokens accessToken) {
        return sendGetAllIngredients(accessToken)
                .spec(success200())
                .body("data", not(emptyArray()))
                .body("data", hasSize(greaterThan(0)))
                .extract()
                .as(GetIngredientsResponseData.class)
                .getData();
    }

    @Step("Ищем первый доступный ингредиент по типу: {ingredientType} из того, что вернулось в ответе сервера")
    public IngredientData findIngredientByType(List<IngredientData> ingredients, IngredientTypes ingredientType) {
        if (ingredients.isEmpty()) {
            return null;
        }
        return ingredients.stream()
                .filter(ingredient -> ingredientType.getValue().equals(ingredient.getType()))
                .findFirst()
                .orElse(null);
    }

    public List<String> getListOfIngredientIds(List<IngredientData> ingredients) {
        return ingredients.stream().map(IngredientData::get_id).collect(Collectors.toList());
    }

    @Step("Добавляем необходимые ингредиенты в список доступных ингредиентов")
    public List<IngredientData> addRequairedIngredientsToAvailableIngredients(AccessTokens accessToken) {

        List<IngredientData> availableIngredients = new ArrayList<>();

        List<IngredientData> ingredients = getAvailableIngredients(accessToken);

        List<IngredientTypes> requaredIngredientsTypes = List.of(IngredientTypes.BUN, IngredientTypes.SAUCE);

        requaredIngredientsTypes.forEach(ingredientType -> {
            IngredientData selectedIngredient = findIngredientByType(ingredients, ingredientType);
            if (selectedIngredient != null) {
                availableIngredients.add(selectedIngredient);
            }
        });

        return availableIngredients;
    }
}
