package userTests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.User;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserLoginTest {
    protected final UserGenerator generator = new UserGenerator();
    private final String apiLogin = "/api/auth/login";
    private final String apiRegister = "/api/auth/register";
    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void userLoggedInSuccessfully() {
        var user = generator.random();
        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(apiRegister)
                        .then().statusCode(200);
        Credentials creds = Credentials.from(user);
        given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(apiLogin)
                .then().assertThat()
                .body("success", is(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Авторизация пользователя с несуществующей почтой")
    public void userWithFakeEmailLoggedInFailed() {
        var user = generator.random();
        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(apiRegister)
                        .then().statusCode(200);
        user.setEmail("sosiska@gmail.com");
        user.setPassword("1234");
        given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(apiLogin)
                .then().assertThat()
                .body("success", is(false))
                .and().statusCode(401);
    }
}
