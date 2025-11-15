package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Создание GET запроса с токеном и куки")
    public Response makeGetRequest(String url,String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Создание GET запроса только с куки")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }

    @Step("Создание GET запроса только с токеном")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .get(url)
                .andReturn();
    }

    @Step("Создание POST запроса")
    public Response makePostRequest(String url, Map<String,String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Создание POST запроса JSON")
    public JsonPath makePostRequestJSON(String url, Map<String,String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .jsonPath();
    }

    @Step("Создание PUT запроса изменения данных без авторизации")
    public Response makePutRequest(String url, Map<String,String> authData,String userId){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .put(url + userId)
                .andReturn();
    }

    @Step("Создание PUT запроса изменения данных с авторизацией")
    public Response makePutRequest(String url, Map<String,String> authData,String userId,String cookie, String token){
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(authData)
                .put(url + userId)
                .andReturn();
    }

    @Step("Создание DELETE запроса с авторизацией")
    public Response makeDeleteRequest(String url, String userId, String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .delete(url + userId)
                .andReturn();
    }

    @Step("Создание GET запроса для получения данных пользователя по ID")
    public Response makeGetRequestForUser(String url, String userId, String cookie, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get(url + userId)
                .andReturn();
    }

    @Step("Создание GET запроса для получения данных пользователя по ID без авторизации")
    public Response makeGetRequestForUserWithoutAuth(String url, String userId) {
        return given()
                .filter(new AllureRestAssured())
                .get(url + userId)
                .andReturn();
    }

}
