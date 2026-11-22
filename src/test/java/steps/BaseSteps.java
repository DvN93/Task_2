package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSteps {

    protected RequestSpecification spec() {
        return given().baseUri(Constants.BASE_URI).contentType(ContentType.JSON);
    }

    @Step("Проверить код ответа: {expected}")
    public void checkStatusCode(Response response, int expected) {
        assertEquals(expected, response.statusCode(), "Неверный код ответа. Тело: " + response.asString());
    }

    @Step("Проверить, что в теле ответа {path} = {expected}")
    public void checkBodyValue(Response response, String path, Object expected) {
        assertEquals(expected, response.path(path), "Неверное значение в теле ответа по пути " + path);
    }

    @Step("Проверить, что в теле ответа {path} не пустое")
    public void checkBodyNotEmpty(Response response, String path) {
        Object value = response.path(path);
        assertNotNull(value, "В теле ответа нет поля " + path);
        assertFalse(value.toString().isEmpty(), "Поле " + path + " пустое");
    }

    @Step("Проверить, что в теле ответа {path} начинается с «{prefix}»")
    public void checkBodyStartsWith(Response response, String path, String prefix) {
        String value = response.path(path);
        assertNotNull(value, "В теле ответа нет поля " + path);
        assertTrue(value.startsWith(prefix), "Поле " + path + " не начинается с " + prefix + ": " + value);
    }

    @Step("Проверить, что тело ответа содержит текст «{text}»")
    public void checkBodyContainsText(Response response, String text) {
        assertTrue(response.asString().contains(text), "Тело ответа не содержит «" + text + "»: " + response.asString());
    }
}
