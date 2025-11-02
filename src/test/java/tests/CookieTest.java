package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CookieTest {
    @Test
    public void cookie() throws Exception{
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookieValue = response.getCookie("HomeWork");
        Assertions.assertEquals("hw_value", cookieValue, "Cookie value is not equal hw_value");
    }
}
