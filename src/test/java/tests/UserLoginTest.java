package tests;

import io.restassured.response.Response;
import models.User;
import models.UserLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import steps.UserSteps;

public class UserLoginTest {

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

    @Test
    @DisplayName("Логин под существующим пользователем")
    void existedUserLogin(){
        Response response = userSteps.login(new UserLogin(user.getEmail(), user.getPassword()));

        userSteps.checkStatusCode(response,200);
        userSteps.checkBodyValue(response, "success", true);
        userSteps.checkBodyValue(response, "user.email", user.getEmail());
        userSteps.checkBodyValue(response, "user.name", user.getName());
        userSteps.checkBodyStartsWith(response, "accessToken", "Bearer ");
        userSteps.checkBodyNotEmpty(response, "refreshToken");
    }

    @ParameterizedTest(name = "Логин с неверным email: {0}, логин с неверным паролем: {1}")
    @CsvSource({"true, false", "false, true", "true, true"})
    @DisplayName("Логин с неверным логином и паролем")
    void userLoginWithWrongFields(boolean isWrongEmail, boolean isWrongPassword){
        User wrongUser = userSteps.generateUser();
        String email = isWrongEmail ? wrongUser.getEmail() : user.getEmail();
        String password = isWrongPassword ? wrongUser.getPassword() : user.getPassword();

        Response response = userSteps.login(new UserLogin(email, password));

        userSteps.checkStatusCode(response, 401);
        userSteps.checkBodyValue(response, "success", false);
        userSteps.checkBodyValue(response, "message", "email or password are incorrect");
        System.out.println(email + " / " + password);
    }
}
