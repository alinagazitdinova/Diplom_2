package orderTests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Credentials;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class NumberOfOrdersTest {

    protected final UserGenerator generator = new UserGenerator();
    private final String ROOT1 = "/api/auth/register"; //ручка
    private final String ROOT2 = "/api/auth/user"; //ручка
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Проверка получения заказа сгенерированного пользователя")
    public void numberOfOrdersReceivedSuccessfully() {
        var user = generator.random();
        Credentials creds = Credentials.from(user);
        String accessToken = given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(ROOT1)
                .then().statusCode(200)
                .extract().path("accessToken");
        given()
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .get("/api/orders")
                .then().assertThat()
                .body("success", is(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка получения заказа сгенерированного пользователя без авторизации")
    public void numberOfOrdersNotReceivedWithoutAuthorization() {
        var user = generator.random();
        Credentials creds = Credentials.from(user);
        String accessToken = given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(ROOT1)
                .then().statusCode(200)
                .extract().path("accessToken");
        given()
                .auth().oauth2(" ")
                .get("/api/orders")
                .then().assertThat()
                .body("success", is(false))
                .and().statusCode(401);;
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
