package tests;

import io.restassured.response.Response;
import models.User;
import models.UserLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import steps.UserSteps;

import java.util.Map;

public class UpdateUserTest {

    private final UserSteps userSteps = new UserSteps();
    private User user;
    private String accessToken;

    @BeforeEach
    void setUp(){
        user = userSteps.generateUser();
        accessToken = userSteps.getAccessToken(userSteps.createUser(user));
    }

    @AfterEach
    void tearDown(){
        userSteps.deleteUser(accessToken);
    }

    @ParameterizedTest(name = "Измененное поле: {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Изменение данных пользователя: с авторизацией")

    void authorisedUserUpdate(String field){
        User newUser = userSteps.generateUser();
        String newFieldValue = newUser.getFieldValue(field);

        Response response = userSteps.updateUser(accessToken, Map.of(field, newFieldValue));

        String expectedEmail = "email".equals(field) ? newFieldValue : user.getEmail();
        String expectedName = "name".equals(field) ? newFieldValue : user.getName();
        String expectedPassword = "password".equals(field) ? newFieldValue : user.getPassword();

        userSteps.checkStatusCode(response, 200);
        userSteps.checkBodyValue(response, "success", true);
        userSteps.checkBodyValue(response, "user.email", expectedEmail);
        userSteps.checkBodyValue(response, "user.name", expectedName);

        Response loginResponse = userSteps.login(new UserLogin(expectedEmail, expectedPassword));
        userSteps.checkStatusCode(loginResponse, 200);
        userSteps.checkBodyValue(loginResponse, "success", true);
        userSteps.checkBodyValue(loginResponse, "user.email", expectedEmail);
        userSteps.checkBodyValue(loginResponse, "user.name", expectedName);
    }

    @ParameterizedTest(name = "Измененное поле: {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Изменение данных пользователя: без авторизации")

    void unAuthorisedUserUpdate(String field){
        User newUser = userSteps.generateUser();
        String newFieldValue = newUser.getFieldValue(field);

        Response response = userSteps.updateUserWithoutAuth(Map.of(field, newFieldValue));

        userSteps.checkStatusCode(response, 401);
        userSteps.checkBodyValue(response, "success", false);
        userSteps.checkBodyValue(response, "message", "You should be authorised");

    }
}
