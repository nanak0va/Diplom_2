package ru.yandex;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.*;
import static ru.yandex.utils.TestDataGenerator.generateHash;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.OrderService;
import ru.yandex.dto.entity.IngredientData;
import ru.yandex.dto.requests.CreateOrderRequestData;

@Epic("API Тесты")
@Feature("Работа с заказами API /orders")
public class OrdersTest extends BaseTest {

    public static final String ORDER_CREATE_SCHEMA_JSON = "schemas/order-create-response-schema.json";

    OrderService orderService;

    List<IngredientData> availableIngredients;

    @Override
    @Before
    @Step("Подготавливаем данные для теста")
    public void init() {
        super.init();
        orderService = new OrderService();
        accessTokens = loginService.createUserBeforeTest(userForMainScenario);
        availableIngredients = orderService.addRequairedIngredientsToAvailableIngredients(accessTokens);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description(
            "Тест проверяет, что авторизованный пользователь может создать заказ с валидными ингредиентами и получить статус 200")
    @Story("Создание заказа")
    @Severity(CRITICAL)
    public void createOrderWithAuthAndValidIngredientsShouldReturn200() {
        CreateOrderRequestData createOrderRequestData = new CreateOrderRequestData()
                .addAllIngredients(orderService.getListOfIngredientIds(availableIngredients));

        orderService
                .sendCreateOrder(createOrderRequestData, accessTokens)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(ORDER_CREATE_SCHEMA_JSON));
    }

    @Test
    @DisplayName("Создание заказа без авторизации с валидными ингредиентами")
    @Description("Тест проверяет, что неавторизованный пользователь не может создать заказ с валидными ингредиентами")
    @Story("Создание заказа")
    @Severity(CRITICAL)
    public void createOrderWithoutAuthWithValidIngredientsShouldReturn401() {
        CreateOrderRequestData createOrderRequestData = new CreateOrderRequestData()
                .addAllIngredients(orderService.getListOfIngredientIds(availableIngredients));

        orderService.sendCreateOrder(createOrderRequestData, null).spec(error401());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Тест проверяет, что при создании заказа без ингредиентов возвращается ошибка 400")
    @Story("Создание заказа")
    @Severity(CRITICAL)
    public void createOrderWithAuthAndEmptyIngredientsShouldReturn400() {
        CreateOrderRequestData createOrderRequestData = new CreateOrderRequestData();

        orderService.sendCreateOrder(createOrderRequestData, accessTokens).spec(error400());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Тест проверяет, что при создании заказа с несуществующим ID ингредиента возвращается ошибка 500")
    @Story("Создание заказа")
    @Severity(CRITICAL)
    public void createOrderWithInvalidIngredientHashShouldReturn500() {
        CreateOrderRequestData createOrderRequestData = new CreateOrderRequestData();
        createOrderRequestData.addIngredient(generateHash());

        orderService.sendCreateOrder(createOrderRequestData, accessTokens).spec(error500());
    }
}
