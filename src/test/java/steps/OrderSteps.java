package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class OrderSteps extends BaseSteps{

    @Step("Получить список id ингредиентов")
    public List<String> getIngredientIds() {
        return spec().get(Constants.INGREDIENTS_API).jsonPath().getList("data._id");
    }

    @Step("Создать заказ с авторизацией")
    public Response createOrder(String accessToken, List<String> ingredients) {
        return spec().header("Authorization", accessToken)
                .body(Map.of("ingredients", ingredients))
                .post(Constants.ORDERS_API);
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuth(List<String> ingredients) {
        return spec().body(Map.of("ingredients", ingredients)).post(Constants.ORDERS_API);
    }

    @Step("Получить заказы пользователя с авторизацией")
    public Response getUserOrders(String accessToken) {
        return spec().header("Authorization", accessToken).get(Constants.ORDERS_API);
    }

    @Step("Получить заказы пользователя без авторизации")
    public Response getUserOrdersWithoutAuth() {
        return spec().get(Constants.ORDERS_API);
    }

    @Step("Получить номер созданного заказа")
    public Integer getOrderNumber(Response response) {
        return response.path("order.number");
    }
}
