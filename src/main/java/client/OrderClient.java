package client;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    public ValidatableResponse createOrder(Order order) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .when()
                .post("/api/orders").then();
    }

    public ValidatableResponse createWrongIngreds(Order order) {
        return given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body("{\"ingredients\": \"rkjdvfhlrkjvhfbkb\"}")
                .when()
                .post("/api/orders")
                .then();
    }
}



