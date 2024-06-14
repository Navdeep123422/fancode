package sdet.sdetfancode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class FanCodeUsersSteps {

    private List<Map<String, Object>> users;
    private List<Map<String, Object>> todos;

    static {
        RestAssured.baseURI = "http://jsonplaceholder.typicode.com";
    }

    @Given("User has the todo tasks")
    public void user_has_the_todo_tasks() {
        Response response = given()
                .when().get("/todos")
                .then().statusCode(200)
                .extract().response();

        todos = response.jsonPath().getList("$");
    }

    @Given("User belongs to the city FanCode")
    public void user_belongs_to_the_city_fancode() {
        Response response = given()
                .when().get("/users")
                .then().statusCode(200)
                .extract().response();

        users = response.jsonPath().getList("$");
    }

    @Then("User Completed task percentage should be greater than {int}%")
    public void user_completed_task_percentage_should_be_greater_than(int percentage) {
        users.stream()
                .filter(this::isFanCodeCity)
                .forEach(user -> {
                    int userId = (int) user.get("id");
                    List<Map<String, Object>> userTodos = todos.stream()
                            .filter(todo -> todo.get("userId").equals(userId))
                            .collect(Collectors.toList());

                    long totalTasks = userTodos.size();
                    long completedTasks = userTodos.stream()
                            .filter(todo -> (boolean) todo.get("completed"))
                            .count();

                    double completedPercentage = (completedTasks * 100.0) / totalTasks;

                    Assertions.assertTrue(completedPercentage > percentage, "User ID: " + userId + " has less than " + percentage + "% completed tasks.");
                });
    }

    private boolean isFanCodeCity(Map<String, Object> user) {
        Map<String, Object> address = (Map<String, Object>) user.get("address");
        Map<String, String> geo = (Map<String, String>) address.get("geo");
        float lat = Float.parseFloat(geo.get("lat"));
        float lng = Float.parseFloat(geo.get("lng"));
        return lat > -40 && lat < 5 && lng > 5 && lng < 100;
    }
}
