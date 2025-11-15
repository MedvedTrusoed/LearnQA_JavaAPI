package tests;

import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import lib.Assertions;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

import lib.ApiCoreRequests;

@Epic("Authorisation cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String BASE_URL = "https://playground.learnqa.ru/api_dev/";
    private final String URL_USER_LOGIN = BASE_URL+"user/login";
    private final String URL_USER_AUTH = BASE_URL+"user/auth";

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Description("Успешная авторизация пользователя по email и паролю")
    @DisplayName("TestPositiveAuthUser")
    public void testAuthUser() {
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(URL_USER_AUTH,
                        this.header, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("Проверка авторизации без отправки куки и токена")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri(URL_USER_AUTH);

        if(condition.equals("cookie")){
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    URL_USER_AUTH, this.cookie
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        }else if(condition.equals("headers")){
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    URL_USER_AUTH, this.header
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        }else {
            throw new IllegalArgumentException("Condition value is unknown: " + condition);
        }
    }
}