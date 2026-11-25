package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.List;

public class GetUserOrdersTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private String accessToken;


    @AfterEach
    void tearDown(){
        userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя: авторизованный пользователь")
    void getAuthorisedUserOrders(){
        accessToken = userSteps.getAccessToken(userSteps.createUser(userSteps.generateUser()));
        List<String> ingredientsId = orderSteps.getIngredientIds();
        List<String> ingredients = List.of(ingredientsId.get(0), ingredientsId.get(1));
        Integer orderNumber = orderSteps.getOrderNumber(orderSteps.createOrder(accessToken, ingredients));

        Response response = orderSteps.getUserOrders(accessToken);

        orderSteps.checkStatusCode(response, 200);
        orderSteps.checkBodyValue(response, "success", true);
        orderSteps.checkBodyValue(response, "orders.size()", 1);
        orderSteps.checkBodyValue(response, "orders[0].number", orderNumber);
        orderSteps.checkBodyValue(response, "orders[0].ingredients", ingredients);
        orderSteps.checkBodyNotEmpty(response, "total");
        orderSteps.checkBodyNotEmpty(response, "totalToday");
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя: неавторизованный пользователь")
    void getUnauthorisedUserOrders(){

        Response response = orderSteps.getUserOrdersWithoutAuth();

        orderSteps.checkStatusCode(response, 401);
        orderSteps.checkBodyValue(response, "success", false);
        orderSteps.checkBodyValue(response, "message", "You should be authorised");
    }
}
