package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User Data Retrieval")
@Feature("User Information Access")
@Owner("Test Automation Team")
@Severity(SeverityLevel.CRITICAL)
@Link(name = "API Documentation", url = "https://playground.learnqa.ru/api/map")
@TmsLink("ссылка на тест-кейс")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String BASE_URL = "https://playground.learnqa.ru/api_dev/user/";
    private final String URL_USER_LOGIN = BASE_URL+"login";

    @Test
    @Story("Unauthorized access")
    @Description("Test verifies that unauthorized user can only see username field without sensitive data")
    @DisplayName("Get user data without authorization")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("ссылка на тест-кейс")
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get(BASE_URL+2)
                .andReturn();

        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasNotField(responseUserData,"firstName");
        Assertions.assertJsonHasNotField(responseUserData,"lastName");
        Assertions.assertJsonHasNotField(responseUserData,"email");
    }

    @Test
    @Story("Authorized access")
    @Description("Test verifies that authorized user can see all personal data when accessing own profile")
    @DisplayName("Get user details when authenticated as same user")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("ссылка на тест-кейс")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(URL_USER_LOGIN)
                .andReturn();

        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid",cookie)
                .get(BASE_URL+2)
                .andReturn();

        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }

    @Test
    @Story("Authorization boundaries")
    @Description("Test verifies security boundaries - authenticated user cannot access sensitive data of other users")
    @DisplayName("Get user details when authenticated as different user")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("ссылка на тест-кейс")
    @Issue("link")
    public void testGetUserDetailsAuthAsOtherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(BASE_URL+1, header,cookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}