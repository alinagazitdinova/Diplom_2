package usertests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Credentials;
import model.User;
import model.UserNewData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.UserGenerator;
import utils.UserNewDataGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserDataUpdate {
    protected final UserGenerator generator = new UserGenerator();
    protected final UserNewDataGenerator generatorMail = new UserNewDataGenerator();
    private final String apiRegister = "/api/auth/register";
    private final String apiUser = "/api/auth/user";
    private User user;
    private UserNewData userNewData;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void userDataUpdatedSuccessfully() {
        var user = generator.random();
        Credentials creds = Credentials.from(user);
        String accessToken = given().log().all()
                .contentType(ContentType.JSON)
                .and()
                .body(creds)
                .when()
                .post(apiRegister)
                .then().statusCode(200)
                .extract().path("accessToken");
        var userNewData = generatorMail.random();
        given().log().all()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .and()
                .body(userNewData)
                .when()
                .patch(apiUser)
                .then().assertThat()
                .body("success", is(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    public void userDataUpdateFailedWithoutAuthorization() {
        var user = generator.random();
        String accessToken = //создали пользователя
                given().log().all()
                        .contentType(ContentType.JSON)
                        .and()
                        .body(user)
                        .when()
                        .post(apiRegister)
                        .then().statusCode(200)
                        .extract().path("accessToken");
        var userNewData = generatorMail.random();
        given().log().all()
                .header("Authorization", "")
                .contentType(ContentType.JSON)
                .and()
                .body(userNewData)
                .when()
                .patch(apiUser)
                .then().assertThat()
                .body("success", is(false))
                .and().statusCode(401);
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


