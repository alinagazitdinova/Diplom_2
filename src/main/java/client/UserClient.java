package client;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.User;
import model.UserNewData;

import static io.restassured.RestAssured.given;

public class UserClient {
    private final String apiRegister = "/api/auth/register";
    private final String apiUser = "/api/auth/user";
    private final String apiLogin = "/api/auth/login";
    private UserNewData userNewData;

    public ValidatableResponse create(User user) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(apiRegister)
                .then().log().all();
    }

    public ValidatableResponse login(User user) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(apiLogin)
                .then().log().all();
    }

    public ValidatableResponse loginWithCreds(Credentials creds) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(apiLogin)
                .then().log().all();
    }

    public ValidatableResponse updateData(UserNewData userNewData) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(userNewData)
                .when()
                .patch(apiUser)
                .then().log().all();
    }

    public ValidatableResponse createWithCreds(Credentials creds) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(apiLogin)
                .then().log().all();
    }

    public String createWithToken(User user) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .when()
                .post(apiRegister)
                .then().statusCode(200)
                .extract().path("accessToken");
    }
}