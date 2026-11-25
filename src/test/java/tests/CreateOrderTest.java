package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.List;
import java.util.UUID;

public class CreateOrderTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private String accessToken;

    @AfterEach
    void tearDown(){
        userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и с ингредиентами")
    void createAuthorisedUserOrderWithIngredients(){
        accessToken = userSteps.getAccessToken(userSteps.createUser(userSteps.generateUser()));
        List<String> ingredientsId = orderSteps.getIngredientIds();
        List<String> ingredients = List.of(ingredientsId.get(0), ingredientsId.get(1));

        Response response = orderSteps.createOrder(accessToken, ingredients);

        orderSteps.checkStatusCode(response, 200);
        orderSteps.checkBodyValue(response, "success", true);
        orderSteps.checkBodyNotEmpty(response, "name");
        orderSteps.checkBodyNotEmpty(response, "order.number");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и с ингредиентами")
    void createUnauthorisedUserOrderWithIngredients(){
        List<String> ingredientsId = orderSteps.getIngredientIds();
        List<String> ingredients = List.of(ingredientsId.get(0), ingredientsId.get(1));

        Response response = orderSteps.createOrderWithoutAuth(ingredients);

        orderSteps.checkStatusCode(response, 200);
        orderSteps.checkBodyValue(response, "success", true);
        orderSteps.checkBodyNotEmpty(response, "name");
        orderSteps.checkBodyNotEmpty(response, "order.number");
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и без ингредиентов")
    void createAuthorisedUserOrderWithoutIngredients(){
        accessToken = userSteps.getAccessToken(userSteps.createUser(userSteps.generateUser()));

        Response response = orderSteps.createOrder(accessToken, List.of());

        orderSteps.checkStatusCode(response, 400);
        orderSteps.checkBodyValue(response, "success", false);
        orderSteps.checkBodyValue(response, "message", "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и без ингредиентов")
    void createUnauthorisedUserOrderWithoutIngredients(){

        Response response = orderSteps.createOrderWithoutAuth(List.of());

        orderSteps.checkStatusCode(response, 400);
        orderSteps.checkBodyValue(response, "success", false);
        orderSteps.checkBodyValue(response, "message", "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и невалидным хэшем ингредиентов")
    void createAuthorisedUserOrderWithInvalidHashIngredients(){
        accessToken = userSteps.getAccessToken(userSteps.createUser(userSteps.generateUser()));
        String invalidHashIngredients = UUID.randomUUID().toString();

        Response response = orderSteps.createOrder(accessToken, List.of(invalidHashIngredients));

        orderSteps.checkStatusCode(response, 500);
        orderSteps.checkBodyContainsText(response, "Internal Server Error");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и невалидным хэшем ингредиентов")
    void createUnauthorisedUserOrderWithInvalidHashIngredients(){
        String invalidHashIngredients = UUID.randomUUID().toString();

        Response response = orderSteps.createOrderWithoutAuth(List.of(invalidHashIngredients));

        orderSteps.checkStatusCode(response, 500);
        orderSteps.checkBodyContainsText(response, "Internal Server Error");
    }

}
