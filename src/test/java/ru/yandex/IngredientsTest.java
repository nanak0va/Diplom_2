package ru.yandex;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.LoginService;
import ru.yandex.api.OrderService;
import ru.yandex.model.AccessTokens;

@Epic("API Тесты")
@Feature("Работа с ингредиентами API /ingredients")
public class IngredientsTest extends BaseTest {

    public static final String INGREDIENTS_SCHEMA_JSON = "schemas/ingredients-response-schema.json";

    OrderService orderService;
    AccessTokens accessToken;

    @Override
    @Before
    @Step("Подготавливаем данные для теста")
    public void init() {
        super.init();
        orderService = new OrderService();
        accessToken = new LoginService().signInAndGetAccessTokens(defaultUser);
    }

    @Test
    @DisplayName("Проверка, что сервер возвращает список ингредиентов")
    @Description("Тест проверяет, что сервер возвращает не пустой список ингредиентов при GET-запросе к /ingredients")
    @Story("Получение списка ингредиентов")
    @Severity(BLOCKER)
    public void getIngredientsShouldReturnAllIngredients() {
        orderService
                .sendGetAllIngredients(accessToken)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(INGREDIENTS_SCHEMA_JSON));
    }
}
