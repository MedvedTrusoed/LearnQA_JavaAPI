import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeaderTest {
    @Test
    public void header() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String headerValue = response.getHeader("x-secret-homework-header");
        Assertions.assertEquals("Some secret value", headerValue, "Header value is not equal - Some secret value");
    }
}
