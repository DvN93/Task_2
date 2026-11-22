package models;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private final String email;
    private final String password;
    private final String name;

    public User(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getFieldValue(String field){
        switch (field) {
            case "email":
                return email;
            case "password":
                return password;
            case "name":
                return name;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
    }

    public User withoutRequiredField(String field) {
        return new User(
                "email".equals(field) ? null : email,
                "password".equals(field) ? null : password,
                "name".equals(field) ? null : name);
    }
}
