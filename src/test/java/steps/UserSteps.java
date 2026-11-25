package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import models.UserLogin;

import java.util.Map;
import java.util.UUID;

public class UserSteps extends BaseSteps{

    @Step("Сгенерировать уникальные данные пользователя")
    public User generateUser() {
        String unique = UUID.randomUUID().toString().substring(0, 6);
        return new User("test-" + unique + "@yandex.ru", "password-" + unique, "name-" + unique);
    }

    @Step("Создать пользователя")
    public Response createUser(User user) {
        return spec().body(user).post(Constants.REGISTER_API);
    }

    @Step("Авторизовать пользователя")
    public Response login(UserLogin userLogin) {
        return spec().body(userLogin).post(Constants.LOGIN_API);
    }

    @Step("Изменить данные пользователя с авторизацией")
    public Response updateUser(String accessToken, Map<String, String> changes) {
        return spec().header("Authorization", accessToken).body(changes).patch(Constants.USER_API);
    }

    @Step("Изменить данные пользователя без авторизации")
    public Response updateUserWithoutAuth(Map<String, String> changes) {
        return spec().body(changes).patch(Constants.USER_API);
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null) {
            spec().header("Authorization", accessToken).delete(Constants.USER_API);
        }
    }

    @Step("Получить accessToken из ответа")
    public String getAccessToken(Response response) {
        return response.path("accessToken");
    }
}
