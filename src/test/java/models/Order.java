package models;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    private final List<String> ingredients;

    public Order(List<String> ingredients){
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
