package orderTests;

import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;
import static io.restassured.RestAssured.given;

public class NumberOfOrdersTest {
    protected final UserGenerator generator = new UserGenerator();
    private final String apiOrders = "/api/orders"; //ручка
    private User user;
    private final UserClient client = new UserClient();
    private final Assertions check = new Assertions();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Проверка получения заказа сгенерированного пользователя")
    public void numberOfOrdersReceivedSuccessfully() {
        var user = generator.random();
        String accessToken = client.createWithToken(user);
        ValidatableResponse response = given()
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .get(apiOrders).then();
        check.successIsTrue200(response);
    }
    @Test
    @DisplayName("Проверка получения заказа сгенерированного пользователя без авторизации")
    public void numberOfOrdersNotReceivedWithoutAuthorization() {
        var user = generator.random();
        String accessToken = client.createWithToken(user);
        ValidatableResponse response = given()
                .auth().oauth2(" ")
                .get("/api/orders").then();
        check.successIsFalse401(response)
        ;
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
