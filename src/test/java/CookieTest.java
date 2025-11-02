import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
