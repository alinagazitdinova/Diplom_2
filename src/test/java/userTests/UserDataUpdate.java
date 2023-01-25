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
import utils.UserNewDataGenerator;

import static io.restassured.RestAssured.given;

public class UserDataUpdate {
    protected final UserGenerator generator = new UserGenerator();
    protected final UserNewDataGenerator generatorMail = new UserNewDataGenerator();
    private final UserClient client = new UserClient();
    private final Assertions check = new Assertions();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void userDataUpdatedSuccessfully() {
        var user = generator.random();
        accessToken = client.createWithToken(user);
        var userNewData = generatorMail.random();
        ValidatableResponse response = given().log().all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(userNewData)
                .when()
                .patch("/api/auth/user").then();
        check.successIsTrue200(response);
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    public void userDataUpdateFailedWithoutAuthorization() {
        var user = generator.random();
        client.create(user);
        var userNewData = generatorMail.random();
        ValidatableResponse response = client.updateData(userNewData);
        check.successIsFalse401(response);
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


