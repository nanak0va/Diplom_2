package ru.yandex;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.error401IncorrectCredentials;
import static ru.yandex.api.ResponseSpec.success200;
import static ru.yandex.utils.TestDataGenerator.generateEmail;
import static ru.yandex.utils.TestDataGenerator.generatePassword;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.model.User;

@Epic("API Тесты")
@Feature("Логин пользователя API /auth/*")
public class LoginTest extends BaseTest {

    public static final String LOGIN_RESPONSE_SCHEMA_JSON = "schemas/login-user-response-schema.json";

    @Override
    @Before
    @Step("Подготавливаем данные для теста")
    public void init() {
        super.init();
        accessTokens = loginService.createUserBeforeTest(userForMainScenario);
    }

    @Test
    @Story("Позитивный сценарий авторизации")
    @DisplayName("Вход под существующим пользователем")
    @Description("Тест проверяет, что пользователь может войти под своими учетными данными")
    public void loginWithValidCredentialsShouldReturnCorrectResponse() {
        loginService
                .signIn(userForMainScenario)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(LOGIN_RESPONSE_SCHEMA_JSON));
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с неверным паролем")
    @Description("Тест проверяет, что пользователь не может войти под неверными учетными данными")
    public void loginWithoutValidPasswordShouldReturn401() {
        var user = User.builder()
                .email(userForMainScenario.getEmail())
                .name(null)
                .password(generatePassword())
                .build();

        loginService.signIn(user).spec(error401IncorrectCredentials());
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с неверным email")
    @Description("Тест проверяет, что пользователь не может войти под неверными учетными данными")
    public void loginWithoutValidEmailShouldReturn401() {
        var user = User.builder()
                .email(generateEmail())
                .name(null)
                .password(userForMainScenario.getPassword())
                .build();

        loginService.signIn(user).spec(error401IncorrectCredentials());
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с пустым паролем")
    @Description("Тест проверяет, что пользователь не может войти под пустыми учетными данными")
    public void loginWithEmptyPasswordShouldReturn401() {
        var user = User.builder()
                .email(userForMainScenario.getEmail())
                .name(null)
                .password(null)
                .build();

        loginService.signIn(user).spec(error401IncorrectCredentials());
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с пустым email")
    @Description("Тест проверяет, что пользователь не может войти под пустыми учетными данными")
    public void loginWithEmptyEmailShouldReturn401() {
        var user = User.builder()
                .email(null)
                .name(null)
                .password(userForMainScenario.getPassword())
                .build();

        loginService.signIn(user).spec(error401IncorrectCredentials());
    }
}
