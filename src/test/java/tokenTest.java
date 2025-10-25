import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class tokenTest {
    @Test
    public void tokenTest()throws InterruptedException{
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = response.get("token");
        int seconds = response.get("seconds");

        JsonPath responseBeforeWait = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusBefore = responseBeforeWait.getString("status");
        System.out.println("Status before waiting: " + statusBefore);

        assertEquals("Job is NOT ready", statusBefore);

        System.out.println("Sleep: " + seconds + " seconds");
        Thread.sleep(seconds * 1000);

        JsonPath responseAfterWait = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusAfter = responseAfterWait.getString("status");
        System.out.println("Status after waiting: " + statusAfter);

        String result = responseAfterWait.getString("result");
        System.out.println("Result: " + result);

        assertNotNull(responseAfterWait.get("result"), "Result should be not null");

        assertEquals("Job is ready", statusAfter);
    }
}
