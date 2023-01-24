package model;

import io.restassured.response.ValidatableResponse;

import static org.hamcrest.Matchers.is;

public class Assertions {
    public String successIsTrue200(ValidatableResponse response) {
        return response.assertThat()
                .body("success", is(true))
                .and().statusCode(200)
                .extract().path("accessToken");
    }

    public String successIsFalse403(ValidatableResponse response) {
        return response.assertThat()
                .body("success", is(false))
                .and().statusCode(403)
                .extract().path("accessToken");
    }

    public String successIsFalse401(ValidatableResponse response) {
        return response.assertThat()
                .body("success", is(false))
                .and().statusCode(401)
                .extract().path("accessToken");
    }

    public String successIsFalse400(ValidatableResponse response) {
        return response.assertThat()
                .body("success", is(false))
                .and().statusCode(400)
                .extract().path("accessToken");
    }
}
