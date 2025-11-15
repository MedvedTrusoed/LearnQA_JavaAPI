package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to " + expectedValue);
    }

    public static void assertJsonByName(Response response, String name, String expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to " + expectedValue);
    }
    public static void assertResponseTextEquals(Response Response, String expectedAnswer) {
        assertEquals(expectedAnswer,
                Response.asString(),
                "Response text is not expected");
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode) {
        assertEquals(expectedStatusCode,
                Response.statusCode(),
                "Response status code is not expected");
    }
    public static void assertJsonHasField(Response Response, String expectedFieldName) {
       Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }
    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }
    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName) {
        Response.then().assertThat().body("$",not(hasKey(unexpectedFieldName)));
    }

}
