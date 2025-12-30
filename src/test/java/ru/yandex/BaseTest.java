package ru.yandex;

import org.junit.Before;
import ru.yandex.api.LoginService;
import ru.yandex.model.User;

public abstract class BaseTest {

    public static final String DEFAULT_USER_EMAIL = "burger-edu-default-test-user-34@stellar-burgers.com";
    public static final String DEFAULT_USER_NAME = "test";
    public static final String DEFAULT_USER_PASSWORD = "123456";

    public User defaultUser;

    @Before
    public void init() {
        createDefaultUser();
    }

    public void createDefaultUser() {
        defaultUser = new LoginService()
                .createDefaultUser(new User(DEFAULT_USER_EMAIL, DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD));
    }
}
