package ru.yandex;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.OrderService;

@Epic("API Тесты")
@Feature("Работа с ингредиентами API /ingredients")
public class IngredientsTest extends BaseTest {

    public static final String INGREDIENTS_SCHEMA_JSON = "schemas/ingredients-response-schema.json";

    OrderService orderService;

    @Before
    @Step("Подготавливаем данные для теста")
    @Override
    public void init() {
        super.init();
        orderService = new OrderService();
        accessTokens = loginService.createUserBeforeTest(userForMainScenario);
    }

    @Test
    @DisplayName("Проверка, что сервер возвращает список ингредиентов")
    @Description("Тест проверяет, что сервер возвращает не пустой список ингредиентов при GET-запросе к /ingredients")
    @Story("Получение списка ингредиентов")
    @Severity(BLOCKER)
    public void getIngredientsShouldReturnAllIngredients() {
        orderService
                .sendGetAllIngredients(accessTokens)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(INGREDIENTS_SCHEMA_JSON));
    }
}
