package userTests;

import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;

public class UserCreationTest {
    protected final UserGenerator generator = new UserGenerator();
    private final UserClient client = new UserClient();
    private final Assertions check = new Assertions();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание уникального пользоватлея")
    public void uniqueUserCreatedSuccessfully() {
        var user = generator.random();
        ValidatableResponse creationResponse = client.create(user);
        accessToken = check.successIsTrue200(creationResponse);
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void creationFailsWithoutName() {
        var user = generator.random();
        user.setName(null);
        ValidatableResponse creationResponse = client.create(user);
        accessToken = check.successIsFalse403(creationResponse);
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    public void creationFailsWithoutEmail() {
        var user = generator.random();
        user.setEmail(null);
        ValidatableResponse creationResponse = client.create(user);
        accessToken = check.successIsFalse403(creationResponse);
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void creationFailsWithoutPassword() {
        var user = generator.random();
        user.setPassword(null);
        ValidatableResponse creationResponse = client.create(user);
        accessToken = check.successIsFalse403(creationResponse);
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    public void existingUserCreationFails() {
        var user = generator.random();
        ValidatableResponse creationResponse = client.create(user);
        ValidatableResponse creationResponse1 = client.create(user);
        accessToken = check.successIsFalse403(creationResponse1);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken)
                    .contentType(ContentType.JSON)
                    .and()
                    .delete("https://stellarburgers.nomoreparties.site/api/auth/user");
        }
    }
}
