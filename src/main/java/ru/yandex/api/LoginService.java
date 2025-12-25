package ru.yandex.api;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static ru.yandex.api.Endpoints.*;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import ru.yandex.dto.requests.CreateUserRequestData;
import ru.yandex.dto.requests.LoginUserRequestData;
import ru.yandex.dto.responses.CreateUserResponseData;
import ru.yandex.dto.responses.LoginUserResponseData;
import ru.yandex.model.AccessToken;
import ru.yandex.model.User;

public class LoginService extends AbstractService {

    public LoginService() {
        super(SB_API_URL + BASE_URL, "application/json");
    }

    @Step("Отправляем запрос на создание пользователя")
    public ValidatableResponse register(CreateUserRequestData request) {
        return post(REGISTER_USER, request).then();
    }

    @Step("Отправляем запрос на аутентификацию пользователя")
    public ValidatableResponse signIn(LoginUserRequestData request) {
        return post(LOGIN_USER, request).then();
    }

    @Step(
            "Получаем access token по логину и паролю пользователя для дальнейшего использования в запросах (аутентификация)")
    public AccessToken signInAndGetAccessToken(User user) {
        LoginUserResponseData loginUserResponse = signIn(new LoginUserRequestData(user.getEmail(), user.getPassword()))
                .spec(success200())
                .body("accessToken", not(emptyString()))
                .extract()
                .as(LoginUserResponseData.class);

        return new AccessToken(loginUserResponse.getAccessToken());
    }

    public User createDefaultUser(User defaultUser) throws RuntimeException {

        Response response = register(new CreateUserRequestData(
                        defaultUser.getEmail(), defaultUser.getName(), defaultUser.getPassword()))
                .extract()
                .response();

        if (response.getStatusCode() != SC_OK && !isUserAlreadyExists(response)) {
            throw new RuntimeException(
                    "Не удалось зарегистрировать дефолтного пользователя для тестов. Остановка тестов");
        }

        return defaultUser;
    }

    private boolean isUserAlreadyExists(Response response) {
        if (response.getStatusCode() != SC_FORBIDDEN) {
            return false;
        }

        CreateUserResponseData errorResponse = response.getBody().as(CreateUserResponseData.class);
        return errorResponse != null && "User already exists".equals(errorResponse.getMessage());
    }
}
