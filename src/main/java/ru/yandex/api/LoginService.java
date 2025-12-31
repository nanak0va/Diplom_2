package ru.yandex.api;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static ru.yandex.api.Endpoints.*;
import static ru.yandex.api.ResponseSpec.success200;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.dto.requests.CreateUserRequestData;
import ru.yandex.dto.requests.LoginUserRequestData;
import ru.yandex.dto.requests.LogoutRequestData;
import ru.yandex.dto.responses.CreateUserResponseData;
import ru.yandex.dto.responses.LoginUserResponseData;
import ru.yandex.model.AccessTokens;
import ru.yandex.model.User;

public class LoginService extends AbstractService {

    public LoginService() {
        super(SB_API_URL + BASE_URL, "application/json");
    }

    @Step("Отправляем запрос на создание пользователя")
    public ValidatableResponse register(User user) {
        CreateUserRequestData request = new CreateUserRequestData(user.getEmail(), user.getPassword(), user.getName());
        return postWithoutAuth(REGISTER_USER, request).then();
    }

    @Step("Отправляем запрос на аутентификацию пользователя")
    public ValidatableResponse signIn(User user) {
        LoginUserRequestData request = new LoginUserRequestData(user.getEmail(), user.getPassword());
        return postWithoutAuth(LOGIN_USER, request).then();
    }

    @Step("Удаляем пользователя")
    public void deleteUser(AccessTokens accessTokens) {
        delete(DELETE_USER, accessTokens).then();
    }

    @Step(
            "Получаем access token по логину и паролю пользователя для дальнейшего использования в запросах (аутентификация)")
    public AccessTokens signInAndGetAccessTokensWithoutValidation(User user) {
        ValidatableResponse response = signIn(user);

        LoginUserResponseData loginUserResponse = response.extract().as(LoginUserResponseData.class);

        if (loginUserResponse.getAccessToken() == null
                || loginUserResponse.getAccessToken().isEmpty()) {
            return null;
        }

        return new AccessTokens(loginUserResponse.getAccessToken(), loginUserResponse.getRefreshToken());
    }

    @Step("Регистрируем пользователя перед тестом и получаем access token и refresh token")
    public AccessTokens createUserBeforeTest(User user) {
        CreateUserResponseData response = register(user)
                .spec(success200())
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .extract()
                .as(CreateUserResponseData.class);

        return new AccessTokens(response.getAccessToken(), response.getRefreshToken());
    }

    @Step("Удаляем пользователя после теста по refresh token")
    public void deleteUserAfterTest(User user, AccessTokens accessTokens) {
        if (user != null) {
            AccessTokens accessTokensFresh = signInAndGetAccessTokensWithoutValidation(user);
            if (accessTokensFresh != null && accessTokensFresh.getRefreshToken() != null) {
                deleteUser(accessTokensFresh);
            } else if (accessTokens != null && accessTokens.getRefreshToken() != null) {
                deleteUser(accessTokens);
            }
        }
    }

    @Step("Отправляем запрос на выход пользователя")
    public ValidatableResponse logoutUser(AccessTokens accessTokens) {
        LogoutRequestData request = new LogoutRequestData(accessTokens.getRefreshToken());

        return postWithoutAuth(LOGOUT_USER, request).then();
    }
}
