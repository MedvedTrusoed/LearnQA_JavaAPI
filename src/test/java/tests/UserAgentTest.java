package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UserAgentTest {

    static Object[][] testData() {
        return new Object[][]{
                {
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Android", "No", "Mobile"
                },
                {
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "iOS", "Chrome", "Mobile"
                },
                {
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Unknown", "Unknown", "Googlebot"
                },
                {
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "No", "Chrome", "Web"
                },
                {
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "iPhone", "No", "Mobile"
                }
        };
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testUserAgent(String userAgent, String expectedDevice, String expectedBrowser, String expectedPlatform) {
         Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        String actualDevice = response.jsonPath().getString("device");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualPlatform = response.jsonPath().getString("platform");

        System.out.println(userAgent);

        Assertions.assertEquals(expectedDevice, actualDevice, "Ожидали: "+ expectedDevice + " получили: "+ actualDevice);
        Assertions.assertEquals(expectedBrowser, actualBrowser, "Ожидали: "+ expectedBrowser + " получили: "+ actualBrowser);
        Assertions.assertEquals(expectedPlatform, actualPlatform, "Ожидали: "+ expectedPlatform + " получили: "+ actualPlatform);
    }
}