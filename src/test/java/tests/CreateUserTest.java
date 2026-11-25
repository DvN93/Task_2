package tests;

import io.restassured.response.Response;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import steps.UserSteps;

public class CreateUserTest {

    private final UserSteps userSteps = new UserSteps();
    private String accessToken;

    @AfterEach
    void tearDown(){
        userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUser(){
        User user = userSteps.generateUser();

        Response response = userSteps.createUser(user);
        accessToken = userSteps.getAccessToken(response);

        userSteps.checkStatusCode(response,200);
        userSteps.checkBodyValue(response, "success", true);
        userSteps.checkBodyValue(response, "user.email", user.getEmail());
        userSteps.checkBodyValue(response, "user.name", user.getName());
        userSteps.checkBodyStartsWith(response, "accessToken", "Bearer ");
        userSteps.checkBodyNotEmpty(response, "refreshToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    void createAlreadyRegisteredUser() {
        User user = userSteps.generateUser();
        Response first = userSteps.createUser(user);
        accessToken = userSteps.getAccessToken(first);

        Response second = userSteps.createUser(user);

        userSteps.checkStatusCode(second, 403);
        userSteps.checkBodyValue(second, "success", false);
        userSteps.checkBodyValue(second, "message", "User already exists");
    }

    @ParameterizedTest(name = "Создание пользователя без поля: {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Создание пользователя без обязательного поля")
    void createUserWithoutRequiredField(String field) {
        User incompleteUser = userSteps.generateUser().withoutRequiredField(field);

        Response response = userSteps.createUser(incompleteUser);

        userSteps.checkStatusCode(response, 403);
        userSteps.checkBodyValue(response, "success", false);
        userSteps.checkBodyValue(response, "message", "Email, password and name are required fields");
    }
}
