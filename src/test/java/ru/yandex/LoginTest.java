package ru.yandex;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.error401_incorrectCredentials;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.LoginService;
import ru.yandex.dto.requests.LoginUserRequestData;

@Epic("API Тесты")
@Feature("Логин пользователя API /auth/*")
public class LoginTest extends BaseTest {

    public static final String LOGIN_RESPONSE_SCHEMA_JSON = "schemas/login-user-response-schema.json";

    LoginService loginService;

    @Before
    public void init() {
        loginService = new LoginService();
    }

    @Test
    @Story("Позитивный сценарий авторизации")
    @DisplayName("Вход под существующим пользователем")
    @Description("Тест проверяет, что пользователь может войти под своими учетными данными")
    public void loginWithValidCredentialsShouldReturnCorrectResponse() {

        LoginUserRequestData loginUserRequestData =
                new LoginUserRequestData(defaultUser.getEmail(), defaultUser.getPassword());

        loginService
                .signIn(loginUserRequestData)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(LOGIN_RESPONSE_SCHEMA_JSON));
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с неверным логином и паролем")
    @Description("Тест проверяет, что пользователь не может войти под неверными учетными данными")
    public void loginWithoutValidCredentialsShouldReturn401() {
        LoginUserRequestData loginUserRequestData = new LoginUserRequestData("Donald@Duck.com", "SuperDuck123");

        loginService.signIn(loginUserRequestData).spec(error401_incorrectCredentials());
    }

    @Test
    @Story("Негативный сценарий авторизации")
    @DisplayName("Вход с пустыми логином и паролем")
    @Description("Тест проверяет, что пользователь не может войти под пустыми учетными данными")
    public void loginWithEmptyCredentialsShouldReturn401() {
        LoginUserRequestData loginUserRequestData = new LoginUserRequestData();

        loginService.signIn(loginUserRequestData).spec(error401_incorrectCredentials());
    }
}
