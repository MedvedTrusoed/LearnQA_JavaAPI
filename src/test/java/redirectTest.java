import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class redirectTest {

        @Test
                public void redirectTest(){
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get("https://playground.learnqa.ru/api/long_redirect")
                    .andReturn();

            String location = response.getHeader("Location");
            System.out.println("Redirect: " + location);

            int statusCode = response.getStatusCode();
            System.out.println("StatusCode: " + statusCode);
        }

}
