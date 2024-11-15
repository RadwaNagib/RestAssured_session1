package tests;
import Pojo.Login;
import Pojo.UserRegister;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import static Utilities.DataUtils.getEnvironmentPropertyValue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.time.Instant;

import Utilities.Utility;

@Slf4j
public class User {

    @BeforeClass
    public static void setup()
    {
        RestAssured.baseURI=getEnvironmentPropertyValue("BASE_URL");
    }

    @Test
    public void testRegisterUser() throws IOException
    {
        //1- assign user from json to DTO object USER
        UserRegister user= Utility.readJsonFromFile("src/main/resources/testData.user/user.json", UserRegister.class);

        //2- user Randomize Email
        user.setEmail("test" + Instant.now().toEpochMilli()+"@gmail.com");
        log.info(String.format("User Data {}:%s", user));

        final String name_json=user.getName();
        final String email_json=user.getEmail();

        Response response = given().
                contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/register")
                .then().log().body()
                .statusCode(201).body("data.name", equalTo(name_json))
                .body("data.email",equalTo(email_json))
                .extract().response();

        //extract specific parameter from response body
        JsonPath path=response.jsonPath();

        //extract email from response body
        String email=path.getString("data.email");

        //create object from polo login class
        Login login=new Login();

        // use the email and password from the previous request for the current request.
        // pass data to login pojo class
        login.setEmail(email);
        login.setPassword(user.getPassword());

        //login request
        String get_token=given().contentType(ContentType.JSON)
                .body(login)
                .when().post("/users/login")
                .then().statusCode(200).
                log().body()
                .body("message",equalTo("Login successful"))
                .extract().path("data.token");

        System.out.println("Token :" +get_token);

        //retrieve user profile info with use token
        Response response1=given()
                .header("x-auth-token",get_token)
                .contentType(ContentType.JSON)
                .when().get("/users/profile").then().statusCode(200)
                .log().all().body("message",equalTo("Profile successful"))
                .extract().response();

//        //user logout
//        Response response2=
//                given().header("x-auth-token",get_token)
//                        .contentType(ContentType.JSON)
//                        .when().delete("/users/logout")
//                        .then().log().all().statusCode(200)
//                        .body("message",equalTo("User has been successfully logged out"))
//                        .extract().response();

        //delete user profile from database

        Response response3=
                given().header("x-auth-token",get_token)
                        .contentType(ContentType.JSON)
                        .when().delete("/users/delete-account")
                        .then().log().all().statusCode(200)
                        .body("message",equalTo("Account successfully deleted"))
                        .extract().response();

    }

    @Test
    public void testLoginUserWithValidData()
    {
        String loginData = "{ \"email\": \"radwa.nageb1@gmail.com\", \"password\": \"my.name@11\" }";

        Response response=given().contentType(ContentType.JSON).body(loginData)
                .when().post("/users/login")
                .then().statusCode(200).extract().response();

        System.out.println("Response Body: " + response.asString());

    }

    @Test
    public void testLogoutUser()
    {
        Response response=given().contentType(ContentType.JSON)
                .when().delete("/users/logout")
                .then().statusCode(200)
                .extract().response();
        System.out.println("Response Body: " + response.asString());

    }

    @Test
    public void testDeleteUser()
    {
        Response response=given().contentType(ContentType.JSON)
                .when().delete("/users/delete-account")
                .then().statusCode(200)
                .extract().response();
    }

}
