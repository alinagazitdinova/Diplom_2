package orderTests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.Order;
import model.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class OrderCreationTest {

    protected final Order order = new Order();
    protected final UserGenerator generator = new UserGenerator();
    private final String apiOrders = "/api/orders";
    List<String> ingredients = new ArrayList<>(); //массив с ингридиентами
    private String accessToken;
    private final UserClient client = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final Assertions check = new Assertions();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        ingredients.add("60d3b41abdacab0026a733c6");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
    }

    @Test
    @DisplayName("Создание заказа с ингридиентами")
    public void orderCreatedSuccessfullyWithIngredients() { //создаем заказ c ингридиентами
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.createOrder(order);
        check.successIsTrue200(response);
    }

    @Test
    @DisplayName("Создание заказов без ингридиентов")
    public void orderCreationFailedWithoutIngredients() { //создаем заказ без ингридиентов
        Order order = new Order(ingredients);
        order.setIngredients(null);
        ValidatableResponse response = orderClient.createOrder(order);
        check.successIsFalse400(response);
    }

    @Test
    @DisplayName("Создание заказа для авторизованного пользователя")
    public void orderOfAuthorizedUserCreatedSuccessfully() {
        var user = generator.random();
        Order order = new Order(ingredients);
        accessToken = client.createWithToken(user);
        ValidatableResponse response = given().log().all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .when()
                .post(apiOrders).then();
        check.successIsTrue200(response);
    }

    @Test
    @DisplayName("Создание заказа для пользователя без авторизации")
    public void orderOfNonAuthorizedUserCreatedSuccessfully() {
        var user = generator.random();
        Order order = new Order(ingredients);
        accessToken = client.createWithToken(user);
        given().log().all()
               // .header("Authorization", "")
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .when()
                .post(apiOrders)
                .then().assertThat()
                .body("success", is(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с несуществующими ингридиентами")
    public void orderNotCreatedWithWrongIngredients() {

        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body("{\"ingredients\": \"rkjdvfhlrkjvhfbkb\"}")
                        .when()
                        .post(apiOrders)
                        .then().assertThat()
                        .statusCode(500);
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