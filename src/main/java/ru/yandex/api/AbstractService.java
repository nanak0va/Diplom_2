package ru.yandex.api;

import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import ru.yandex.model.AccessToken;

@AllArgsConstructor
public abstract class AbstractService {

    private final String baseURI;
    private final String requestsContentType;

    public RequestSpecification getRequestSpec() {
        return given().baseUri(baseURI)
                .filter(new AllureRestAssured())
                .contentType(requestsContentType)
                .accept(requestsContentType);
    }

    public Response request(Method method, String url, Object body) {
        return getRequestSpec().when().body(body).request(method, url);
    }

    public Response request(Method method, String url, AccessToken accessToken, Object body) {
        return getRequestSpec()
                .header("Authorization", (accessToken == null ? "" : accessToken.getToken()))
                .when()
                .body(body)
                .request(method, url);
    }

    public Response post(String url, Object body) {
        return request(Method.POST, url, body);
    }

    public Response post(String url, AccessToken accessToken, Object body) {
        return request(Method.POST, url, accessToken, body);
    }

    public Response get(String url, AccessToken authToken) {
        return request(Method.GET, url, authToken);
    }
}
