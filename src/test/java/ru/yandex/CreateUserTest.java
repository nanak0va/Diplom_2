package ru.yandex;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static ru.yandex.api.ResponseSpec.*;
import static ru.yandex.utils.TestDataGenerator.*;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import ru.yandex.model.User;

@Slf4j
@Epic("API Тесты")
@Feature("Создание пользователя API /auth/register")
public class CreateUserTest extends BaseTest {

    public static final String CREATE_USER_RESPONSE_SCHEMA_JSON = "schemas/login-user-response-schema.json";

    @Test
    @Story("Позитивный сценарий регистрации")
    @DisplayName("Успешное создание пользователя с валидными данными")
    @Description("Тест проверяет, что зарегистрироваться с валидными данными пользователя")
    public void createUserWithValidDataShouldReturnSuccess() {
        loginService
                .register(userForMainScenario)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(CREATE_USER_RESPONSE_SCHEMA_JSON));
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя c существующим email")
    @Description("Тест проверяет, что нельзя создать пользователя с существующим email")
    public void createUserWithExistingEmailShouldReturnError() {
        userForConflictScenario = userForMainScenario.toBuilder()
                .name(generateName())
                .password(generatePassword())
                .build();

        loginService
                .register(userForConflictScenario)
                .spec(success200())
                .body(matchesJsonSchemaInClasspath(CREATE_USER_RESPONSE_SCHEMA_JSON));

        loginService.register(userForMainScenario).spec(error403UserAlreadyExists());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля name")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля name")
    public void createUserWithoutNameShouldReturnError() {
        userForMainScenario = User.builder()
                .email(generateEmail())
                .name(null)
                .password(generatePassword())
                .build();

        loginService.register(userForMainScenario).spec(error403RequiredFields());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля email")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля email")
    public void createUserWithoutEmailShouldReturnError() {
        userForMainScenario = User.builder()
                .email(null)
                .name(generateName())
                .password(generatePassword())
                .build();

        loginService.register(userForMainScenario).spec(error403RequiredFields());
    }

    @Test
    @Story("Негативный сценарий регистрации")
    @DisplayName("Создать уникального пользователя без обязательного поля password")
    @Description("Тест проверяет, что нельзя создать пользователя без обязательного поля password")
    public void createUserWithoutPasswordShouldReturnError() {
        userForMainScenario = User.builder()
                .email(generateEmail())
                .name(generateName())
                .password(null)
                .build();

        loginService.register(userForMainScenario).spec(error403RequiredFields());
    }
}
