package ru.yandex;

import static ru.yandex.utils.TestDataGenerator.generateUniqueUser;

import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import ru.yandex.api.LoginService;
import ru.yandex.model.AccessTokens;
import ru.yandex.model.User;

public abstract class BaseTest {

    public User userForMainScenario;
    public User userForConflictScenario;
    public LoginService loginService;
    public AccessTokens accessTokens;

    @Before
    @Step("Подготавливаем данные для теста")
    public void init() {
        loginService = new LoginService();
        userForMainScenario = generateUniqueUser();
    }

    @After
    @Step("Очищаем зарегистрированного пользователя, если создали")
    public void tearDown() {
        loginService.deleteUserAfterTest(userForMainScenario, accessTokens);
        loginService.deleteUserAfterTest(userForConflictScenario, accessTokens);
    }
}
