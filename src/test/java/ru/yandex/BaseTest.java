package ru.yandex;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.BeforeClass;
import ru.yandex.api.LoginService;
import ru.yandex.model.User;

public class BaseTest {

    public static final String DEFAULT_USER_EMAIL = "burger-edu-default-test-user-34@stellar-burgers.com";
    public static final String DEFAULT_USER_NAME = "test";
    public static final String DEFAULT_USER_PASSWORD = "123456";

    protected static User defaultUser;

    @BeforeClass
    public static void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        defaultUser = new LoginService()
                .createDefaultUser(new User(DEFAULT_USER_EMAIL, DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD));
    }
}
