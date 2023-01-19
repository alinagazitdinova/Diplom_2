package orderTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.Order;
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

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        ingredients.add("60d3b41abdacab0026a733c6");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");

    }

    @Test

    public void orderCreatedSuccessfullyWithIngredients() { //создаем заказ c ингридиентами
        Order order = new Order(ingredients);
        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(order)
                        .when()
                        .post(ROOT4)
                        .then().log().all().statusCode(200)
                        .body("success", is(true));

    }

    @Test

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
                        .then().log().all().statusCode(400)
                        .body("success", is(false));
    }

    @Test
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
                .then().log().all().statusCode(200)
                .body("success", is(true));
    }

    @Test
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
                .then().log().all().statusCode(200)
                .body("success", is(true));
    }

    @Test

    public void orderNotCreatedWithWrongIngredients() { //создаем заказ c ингридиентами

        ValidatableResponse response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body("{\"ingredients\": \"rkjdvfhlrkjvhfbkb\"}")
                        .when()
                        .post(ROOT4)
                        .then().log().all().statusCode(500);

    }
}