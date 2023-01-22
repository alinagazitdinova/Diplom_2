package userTests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserCreationTest {
    protected final UserGenerator generator = new UserGenerator();
    private final String registerApi = "/api/auth/register";
    private String accessToken;
    private User user;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание уникального пользоватлея")
    public void uniqueUserCreatedSuccessfully() {
        var user = generator.random();
        String accessToken =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(registerApi)
                        .then().assertThat()
                        .body("success", is(true))
                        .and().statusCode(200)
                        .extract().path("accessToken");
        ;
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void creationFailsWithoutName() {
        var user = generator.random();
        user.setName(null);
        String accessToken =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(registerApi)
                        .then().assertThat()
                        .body("success", is(false))
                        .and().statusCode(403)
                        .extract().path("accessToken");
        ;
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    public void creationFailsWithoutEmail() {
        var user = generator.random();
        user.setEmail(null);
        String accessToken =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(registerApi)
                        .then().assertThat()
                        .body("success", is(false))
                        .and().statusCode(403)
                        .extract().path("accessToken");
        ;
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void creationFailsWithoutPassword() {
        var user = generator.random();
        user.setPassword(null);
        String accessToken =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(registerApi)
                        .then().assertThat()
                        .body("success", is(false))
                        .and().statusCode(403)
                        .extract().path("accessToken");
        ;
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    public void existingUserCreationFails() {
        var user = generator.random();
        String accessToken =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(registerApi)
                        .then().assertThat()
                        .body("success", is(true))
                        .and().statusCode(200)
                        .extract().path("accessToken");
        ;
        given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(registerApi)
                .then().assertThat()
                .body("success", is(false))
                .and().statusCode(403);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            Response response =
                    given().header("Authorization", accessToken)
                            .contentType(ContentType.JSON)
                            .and()
                            .delete("https://stellarburgers.nomoreparties.site/api/auth/user");
        }
    }
}
