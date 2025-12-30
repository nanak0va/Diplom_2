package ru.yandex.api;

import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import ru.yandex.model.AccessTokens;

@AllArgsConstructor
public abstract class AbstractService {

    private final String baseURI;
    private final String requestsContentType;

    public RequestSpecification getRequestSpec() {
        return given().baseUri(baseURI)
                .filter(new AllureRestAssured())
                .log()
                .ifValidationFails()
                .contentType(requestsContentType)
                .accept(requestsContentType);
    }

    public Response request(Method method, String url, AccessTokens accessTokens, Object body) {
        var request = given().spec(getRequestSpec());

        if (accessTokens != null && accessTokens.getAccessToken() != null) {
            request.header("Authorization", accessTokens.getAccessToken());
        }

        if (body != null) {
            request.body(body);
        }

        return request.when().request(method, url);
    }

    public Response post(String url, Object body) {
        return request(Method.POST, url, null, body);
    }

    public Response post(String url, AccessTokens accessTokens, Object body) {
        return request(Method.POST, url, accessTokens, body);
    }

    public Response get(String url, AccessTokens accessTokens) {
        return request(Method.GET, url, accessTokens, null);
    }

}
