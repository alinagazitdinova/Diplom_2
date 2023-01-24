package userTests;

import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.User;
import model.Assertions;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserLoginTest {
    protected final UserGenerator generator = new UserGenerator();

    private User user;
    private final UserClient client = new UserClient();
    private final Assertions check = new Assertions();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Авторизация пользователя")
    public void userLoggedInSuccessfully() {
        var user = generator.random();
        client.create(user);
        Credentials creds = Credentials.from(user);
        ValidatableResponse response =  client.loginWithCreds(creds); //given().log().all()
        check.successIsTrue200(response);
    }

    @Test
    @DisplayName("Авторизация пользователя с несуществующей почтой")
    public void userWithFakeEmailLoggedInFailed() {
        var user = generator.random();
        client.create(user);
        user.setEmail("sosiska@gmail.com");
        user.setPassword("1234");
        ValidatableResponse response = client.login(user);
        check.successIsFalse401(response);
    }

}
