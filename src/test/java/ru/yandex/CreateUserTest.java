package ru.yandex;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.*;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.api.LoginService;
import ru.yandex.dto.requests.CreateUserRequestData;

@Epic("API Тесты")
@Feature("Создание пользователя API /auth/register")
public class CreateUserTest extends BaseTest {

    public static final String CREATE_USER_RESPONSE_SCHEMA_JSON = "schemas/login-user-response-schema.json";

    LoginService loginService;

    @Before
    public void init() {
        loginService = new LoginService();
    }

    @Test
    @Story("Позитивный сценарий регистрации")
    @DisplayName("Успешное создание пользователя с валидными данными")
    @Description("Тест проверяет, что зарегистрироваться с валидными данными пользователя")
    public void createUserWithValidDataShouldReturnSuccess() {
        // В документации на API не указан формат запроса на удаление пользователя, поэтому не запоминаем кого мы
        // создали и не удаляем после теста
        // На сайте тоже такой функции нет
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(String.format("user-%s@stellar-burgers.com", System.currentTimeMillis()))
                .password("123456")
                .name("Duck")
                .build();

        loginService
                .register(createUserRequestData)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(CREATE_USER_RESPONSE_SCHEMA_JSON));
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя c существующим email")
    @Description("Тест проверяет, что нельзя создать пользователя с существующим email")
    public void creatUsetWithExistingEmailShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(defaultUser.getEmail())
                .password(defaultUser.getPassword())
                .name(defaultUser.getName())
                .build();

        loginService.register(createUserRequestData).spec(error403_userAlreadyExists());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля name")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля name")
    public void createUserWithoutNameShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(defaultUser.getEmail())
                .password(defaultUser.getPassword())
                .name(null)
                .build();

        loginService.register(createUserRequestData).spec(error403_requiredFields());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля email")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля email")
    public void createUserWithoutEmailShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(null)
                .password(defaultUser.getPassword())
                .name(defaultUser.getName())
                .build();

        loginService.register(createUserRequestData).spec(error403_requiredFields());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля password")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля password")
    public void createUserWithoutPasswordShouldReturnError() {
        CreateUserRequestData createUserRequestData = CreateUserRequestData.builder()
                .email(defaultUser.getEmail())
                .password(null)
                .name(defaultUser.getPassword())
                .build();

        loginService.register(createUserRequestData).spec(error403_requiredFields());
    }
}
