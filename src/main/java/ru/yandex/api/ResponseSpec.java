package ru.yandex.api;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";

    private ResponseSpec() {}

    private static final String RESPONSE_CONTENT_TYPE = "application/json";

    private static ResponseSpecBuilder baseSuccess(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .expectContentType(RESPONSE_CONTENT_TYPE)
                .expectBody(SUCCESS, equalTo(true));
    }

    private static ResponseSpecBuilder baseError(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .expectContentType(RESPONSE_CONTENT_TYPE)
                .expectBody(SUCCESS, equalTo(false));
    }

    public static ResponseSpecification success200() {
        return baseSuccess(SC_OK).build();
    }

    public static ResponseSpecification error400() {
        return baseError(SC_BAD_REQUEST).build();
    }

    public static ResponseSpecification error401() {
        return baseError(SC_UNAUTHORIZED)
                .expectBody(MESSAGE, is("You should be authorised"))
                .build();
    }

    public static ResponseSpecification error401_incorrectCredentials() {
        return baseError(SC_UNAUTHORIZED)
                .expectBody(MESSAGE, is("email or password are incorrect"))
                .build();
    }

    public static ResponseSpecification error403_userAlreadyExists() {
        return baseError(SC_FORBIDDEN)
                .expectBody(MESSAGE, is("User already exists"))
                .build();
    }

    public static ResponseSpecification error403_requiredFields() {
        return baseError(SC_FORBIDDEN)
                .expectBody(MESSAGE, is("Email, password and name are required fields"))
                .build();
    }

    public static ResponseSpecification error500() {
        return new ResponseSpecBuilder()
                .expectStatusCode(SC_INTERNAL_SERVER_ERROR)
                .build();
    }
}
