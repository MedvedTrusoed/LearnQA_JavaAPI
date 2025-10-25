import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class longRedirectTest {
    @Test
    public void longRedirectTest(){
        int statusCode;
        String location = "https://playground.learnqa.ru/api/long_redirect";
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();

            location = response.getHeader("Location");
            System.out.println("Redirect: " + location);

            statusCode = response.getStatusCode();
            System.out.println("StatusCode: " + statusCode);
        }while (statusCode!=200);
    }
}
