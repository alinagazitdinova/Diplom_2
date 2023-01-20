package orderTests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.Order;
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
    private final String ROOT4 = "/api/orders";
    private final String ROOT1 = "/api/auth/register"; //ручка
    private final String ROOT2 = "/api/auth/user"; //ручка
    List<String> ingredients = new ArrayList<>(); //массив с ингридиентами
    private String accessToken;

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
        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(order)
                        .when()
                        .post(ROOT4)
                        .then().assertThat()
                        .body("success", is(true))
                        .and().statusCode(200);
    }
    @Test
    @DisplayName("Создание заказов без ингридиентов")
    public void orderCreationFailedWithoutIngredients() { //создаем заказ без ингридиентов
        Order order = new Order(ingredients);
        order.setIngredients(null);
        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(order)
                        .when()
                        .post(ROOT4)
                        .then().assertThat()
                        .body("success", is(false))
                        .and().statusCode(400);
    }
    @Test
    @DisplayName("Создание заказа для авторизованного пользователя")
    public void orderOfAuthorizedUserCreatedSuccessfully() {
        var user = generator.random();
        Order order = new Order(ingredients);
        Credentials creds = Credentials.from(user);
        String accessToken = given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(ROOT1)
                .then().statusCode(200)
                .extract().path("accessToken");
        given().log().all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .when()
                .post(ROOT4)
                .then().assertThat()
                .body("success", is(true))
                .and().statusCode(200);
    }
    @Test
    @DisplayName("Создание заказа для пользователя без авторизации")
    public void orderOfNonAuthorizedUserCreatedSuccessfully() {
        var user = generator.random();
        Order order = new Order(ingredients);
        Credentials creds = Credentials.from(user);
        String accessToken = given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(ROOT1)
                .then().statusCode(200)
                .extract().path("accessToken");
        given().log().all()
                .header("Authorization", "")
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .when()
                .post(ROOT4)
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
                        .post(ROOT4)
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