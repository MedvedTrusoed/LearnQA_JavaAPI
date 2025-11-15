package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Epic("User Registration")
@Feature("User Management")
@Owner("Test Automation Team")
@Severity(SeverityLevel.CRITICAL)
public class userRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String BASE_URL = "https://playground.learnqa.ru/api_dev/user/";
    private final String URL_USER_LOGIN = BASE_URL+"login";

    @Test
    @Description("This test verifies that system correctly handles attempt to create user with already registered email")
    @DisplayName("Negative: Create user with existing email")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ссылка на тест-кейс")
    public void testCreateUserWithExistingEmail() {
        Map<String,String> userData = new HashMap<>();
        String email = "vinkotov@example.com";
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(BASE_URL)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '"+email+"' already exists");
    }

    @Test
    @Description("This test verifies successful user creation with valid data")
    @DisplayName("Positive: Create user successfully")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("ссылка на тест-кейс")
    public void testCreateUserSuccessfilly() {
        String email = DataGenerator.getRandomEmail();

        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(BASE_URL)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }

    @Test
    @Description("This test verifies email format validation during user registration")
    @DisplayName("Negative: Create user with incorrect email format")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("ссылка на тест-кейс")
    public void testCreateUserWithIncorrectEmail() {
        Map<String,String> userData = new HashMap<>();
        String email = "vinkotovexample.com";
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(BASE_URL, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    private static Stream<Arguments> provideUserDataWithMissingField() {
        Map<String, String> baseData = DataGenerator.getRegistrationData();

        return Stream.of(
                Arguments.of(removeField(new HashMap<>(baseData), "email"), "email"),
                Arguments.of(removeField(new HashMap<>(baseData), "password"), "password"),
                Arguments.of(removeField(new HashMap<>(baseData), "username"), "username"),
                Arguments.of(removeField(new HashMap<>(baseData), "firstName"), "firstName"),
                Arguments.of(removeField(new HashMap<>(baseData), "lastName"), "lastName")
        );
    }

    private static Map<String, String> removeField(Map<String, String> data, String field) {
        data.remove(field);
        return data;
    }

    @ParameterizedTest
    @MethodSource("provideUserDataWithMissingField")
    @Description("This test verifies validation of required fields during user registration")
    @DisplayName("Negative: Create user without essential fields")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("ссылка на тест-кейс")
    public void testCreateUserWithoutEssentialFields(Map<String, String> userData, String missingField) {
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(BASE_URL, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + missingField);
    }

    @Test
    @Story("Validation: Username length")
    @Description("This test verifies username length validation - minimum length constraint")
    @DisplayName("Negative: Create user with too short username")
    @Severity(SeverityLevel.MINOR)
    @TmsLink("TMS-128")
    public void TestCreateUserWithShortUsername() {
        Map<String,String> userData = new HashMap<>();
        String username = "i";
        userData.put("username",username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(BASE_URL, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too short");
    }

    @Test
    @Description("This test verifies username length validation - maximum length constraint")
    @DisplayName("Negative: Create user with too long username")
    @Severity(SeverityLevel.MINOR)
    @TmsLink("ссылка на тест-кейс")
    public void TestCreateUserWithLongUsername() {
        Map<String,String> userData = new HashMap<>();
        String username = "useraaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        userData.put("username",username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(BASE_URL, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too long");
    }
}