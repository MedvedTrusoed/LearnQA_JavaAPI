package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User Account Management")
@Feature("User Deletion")
@Owner("Test Automation Team")
@Severity(SeverityLevel.CRITICAL)
@Link(name = "API Documentation", url = "https://playground.learnqa.ru/api/map")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String BASE_URL = "https://playground.learnqa.ru/api_dev/user/";
    private final String URL_USER_LOGIN = BASE_URL+"login";

    @Test
    @Story("Protected users")
    @Description("Test verifies that system prevents deletion of protected test users (IDs 1,2,3,4,5)")
    @DisplayName("Negative: Delete protected user with ID 2")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("тест кейс")
    @Issue("Issue-1")
    public void testDeleteUserWithId2() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Пытаемся удалить пользователя с ID 2
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(BASE_URL, "2", cookie, header);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @Story("Successful deletion")
    @Description("Test verifies complete user deletion flow: create user, authenticate, delete, and verify deletion")
    @DisplayName("Positive: Delete just created user")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteJustCreatedUser() {
        // Generate and create user
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(BASE_URL, userData);

        String userId = responseCreateUser.jsonPath().getString("id");

        // Authenticate
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(URL_USER_LOGIN, authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Delete user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(BASE_URL, userId, cookie, header);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"success\":\"!\"}");

        // Verify user is deleted
        Response responseUserData = apiCoreRequests
                .makeGetRequestForUserWithoutAuth(BASE_URL, userId);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Story("Security boundaries")
    @Description("Test verifies that user cannot delete another user's account - security boundary check")
    @DisplayName("Negative: Delete user with authorization as different user")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserWithAuthAsOtherUser() {
        // Create first user
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        Response responseCreateUser1 = apiCoreRequests
                .makePostRequest(BASE_URL, user1Data);

        String user1Id = responseCreateUser1.jsonPath().getString("id");

        // Create second user
        Map<String, String> user2Data = DataGenerator.getRegistrationData();
        Response responseCreateUser2 = apiCoreRequests
                .makePostRequest(BASE_URL, user2Data);

        // Authenticate as second user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", user2Data.get("email"));
        authData.put("password", user2Data.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(BASE_URL + "login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // Try to delete first user while authenticated as second user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(BASE_URL, user1Id, cookie, header);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"This user can only delete their own account.\"}");
    }
}