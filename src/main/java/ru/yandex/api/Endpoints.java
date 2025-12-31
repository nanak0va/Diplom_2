package ru.yandex.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoints {
    public static final String SB_API_URL = "https://stellarburgers.education-services.ru";
    public static final String BASE_URL = "/api";
    public static final String REGISTER_USER = "/auth/register";
    public static final String LOGIN_USER = "/auth/login";
    public static final String GET_INGREDIENTS = "/ingredients";
    public static final String CREATE_ORDER = "/orders";
    public static final String LOGOUT_USER = "/auth/logout";
    public static final String DELETE_USER = "/auth/user";
}
