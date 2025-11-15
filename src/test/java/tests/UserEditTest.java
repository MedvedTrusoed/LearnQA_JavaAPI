package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User Data Modification")
@Feature("User Profile Editing")
@Owner("Test Automation Team")
@Severity(SeverityLevel.CRITICAL)
@Link(name = "API Documentation", url = "https://playground.learnqa.ru/api/map")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String BASE_URL = "https://playground.learnqa.ru/api_dev/user/";
    private final String URL_USER_LOGIN = BASE_URL+"login";

    @Test
    @Story("Successful editing")
    @Description("Test verifies that authenticated user can successfully edit their own profile data")
    @DisplayName("Positive: Edit just created user")
    @Severity(SeverityLevel.CRITICAL)
    public void testEditJustCreatedTest() {
        //generate user
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCteateAuth = RestAssured
                .given()
                .body(userData)
                .post(BASE_URL)
                .jsonPath();

        String userId = responseCteateAuth.getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(URL_USER_LOGIN)
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName",newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put(BASE_URL+userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid"))
                .get(BASE_URL+userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData,"firstName", newName);
    }

    @Test
    @Story("Authorization validation")
    @Description("Test verifies that user cannot edit profile without proper authentication")
    @DisplayName("Negative: Edit user without authorization")
    @Severity(SeverityLevel.NORMAL)
    public void testEditUserWithoutAuth() {
        // Создаем пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestJSON(BASE_URL, userData);

        String userId = responseCreateAuth.getString("id");

        // Пытаемся изменить данные БЕЗ авторизации
        String newName = "Changed Name Without Auth";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(BASE_URL, userData,userId);

        // Проверяем, что редактирование не удалось (должна быть ошибка авторизации)
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    @Story("Data validation")
    @Description("Test verifies email format validation during user profile editing")
    @DisplayName("Negative: Edit user email without @ symbol")
    @Severity(SeverityLevel.NORMAL)
    public void testEditUserEmailWithoutSymbol() {
        // Создаем пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestJSON(BASE_URL, userData);

        String userId = responseCreateAuth.getString("id");

        // Логинимся
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Пытаемся изменить email на невалидный (без @)
        String invalidEmail = "invalidemail.example.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", invalidEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(BASE_URL, editData,userId,cookie,header);

        // Проверяем, что редактирование не удалось (должна быть ошибка валидации)
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Invalid email format\"}");
    }

    @Test
    @Story("Data validation")
    @Description("Test verifies firstName length validation during user profile editing")
    @DisplayName("Negative: Edit user with too short first name")
    @Severity(SeverityLevel.MINOR)
    public void testEditUserWithTooShortFirstName() {
        // Создаем пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestJSON(BASE_URL, userData);

        String userId = responseCreateAuth.getString("id");
        String originalFirstName = userData.get("firstName");

        // Логинимся
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Пытаемся изменить firstName на слишком короткое значение (1 символ)
        String tooShortFirstName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", tooShortFirstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(BASE_URL, editData,userId,cookie,header);

        // Проверяем, что редактирование не удалось (должна быть ошибка валидации)
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"The value for field `firstName` is too short\"}");
    }

    @Test
    @Story("Security boundaries")
    @Description("Test verifies that user cannot edit another user's profile data")
    @DisplayName("Negative: Edit user as different user")
    @Severity(SeverityLevel.CRITICAL)
    public void testEditUserAsOtherUser() {
        // Создаем первого пользователя
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        JsonPath responseCreateUser1 = apiCoreRequests
                .makePostRequestJSON(BASE_URL, user1Data);

        String user1Id = responseCreateUser1.getString("id");

        // Создаем второго пользователя
        Map<String, String> user2Data = DataGenerator.getRegistrationData();
        JsonPath responseCreateUser2 = apiCoreRequests
                .makePostRequestJSON(BASE_URL, user2Data);

        String user2Id = responseCreateUser2.getString("id");

        // Логинимся вторым пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", user2Data.get("email"));
        authData.put("password", user2Data.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Пытаемся изменить данные первого пользователя
        String newName = "Changed By Other User";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(BASE_URL, editData, user1Id, cookie, header);

        // Проверяем, что редактирование не удалось
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"This user can only edit their own data.\"}");
    }
}