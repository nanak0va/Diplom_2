package ru.yandex;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.*;
import static ru.yandex.api.ResponseSpec.*;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.LoginService;
import ru.yandex.dto.requests.CreateUserRequestData;
import ru.yandex.model.AccessTokens;
import ru.yandex.model.User;

@Epic("API Тесты")
@Feature("Создание пользователя API /auth/register")
public class CreateUserTest extends BaseTest {

    public static final String CREATE_USER_RESPONSE_SCHEMA_JSON = "schemas/login-user-response-schema.json";

    private AccessTokens accessTokens;
    private LoginService loginService;
    private User user;

    @Override
    @Before
    public void init() {
        loginService = new LoginService();

        user = User.builder()
                .email(String.format("user-%s@stellar-burgers.com", System.currentTimeMillis()))
                .password("P123456")
                .name("Duck")
                .build();
    }

    @Test
    @Story("Позитивный сценарий регистрации")
    @DisplayName("Успешное создание пользователя с валидными данными")
    @Description("Тест проверяет, что зарегистрироваться с валидными данными пользователя")
    public void createUserWithValidDataShouldReturnSuccess() {

        var createUserRequestData = CreateUserRequestData.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .build();

        var response = loginService
                .register(createUserRequestData)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(CREATE_USER_RESPONSE_SCHEMA_JSON));

        accessTokens = loginService.getAccessTokensFromCreateUserResponse(response);

        assertNotNull(
                "Не удалось получить токен авторизации пользователя после регистрации", accessTokens.getRefreshToken());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя c существующим email")
    @Description("Тест проверяет, что нельзя создать пользователя с существующим email")
    public void creatUsetWithExistingEmailShouldReturnError() {

        createDefaultUser();

        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(defaultUser.getEmail())
                .password(defaultUser.getPassword())
                .name(defaultUser.getName())
                .build();

        var response = loginService.register(createUserRequestData).spec(error403_userAlreadyExists());

        accessTokens = loginService.getAccessTokensFromCreateUserResponse(response);

        assertNull("Пользователь не должен был быть создан, не должно быть токенов в ответе", accessTokens);
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля name")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля name")
    public void createUserWithoutNameShouldReturnError() {

        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(null)
                .build();

        var response = loginService.register(createUserRequestData).spec(error403_requiredFields());

        accessTokens = loginService.getAccessTokensFromCreateUserResponse(response);

        assertNull("Пользователь не должен был быть создан, не должно быть токенов в ответе", accessTokens);
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля email")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля email")
    public void createUserWithoutEmailShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(null)
                .password(user.getPassword())
                .name(user.getName())
                .build();

        var response = loginService.register(createUserRequestData).spec(error403_requiredFields());

        accessTokens = loginService.getAccessTokensFromCreateUserResponse(response);

        assertNull("Пользователь не должен был быть создан, не должно быть токенов в ответе", accessTokens);
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля password")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля password")
    public void createUserWithoutPasswordShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(user.getEmail())
                .password(null)
                .name(user.getName())
                .build();

        var response = loginService.register(createUserRequestData).spec(error403_requiredFields());

        accessTokens = loginService.getAccessTokensFromCreateUserResponse(response);

        assertNull("Пользователь не должен был быть создан, не должно быть токенов в ответе", accessTokens);
    }

    @After
    @Step("Очищаем зарегистрированного пользователя, если создали")
    public void tearDown() {
        if (accessTokens != null) {
            loginService.logoutUser(accessTokens);
        }
    }
}
