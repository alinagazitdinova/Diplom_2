package orderTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import model.Credentials;
import model.User;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;

import static io.restassured.RestAssured.given;

public class NumberOfOrdersTest {

    protected final UserGenerator generator = new UserGenerator();
    private final String ROOT1 = "/api/auth/register"; //ручка
    private final String ROOT2 = "/api/auth/user"; //ручка
    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
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
                .then().statusCode(200);

    }

    @Test
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
                .then().statusCode(401);

    }
}
