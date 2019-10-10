package stepDefinitions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import cucumber.api.java.en.When;

import static io.restassured.RestAssured.*;

import org.json.simple.JSONObject;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.Map;

public class weatherForeCastSteps {
    public String apiKey, id;
    public Response response;
    public JSONObject requestParams = new JSONObject();

    @Given("^I access the weather station creation url \"([^\"]*)\"$")
    public void i_access_the_weather_station_creation_url(String arg1) throws Throwable {
        baseURI = arg1;
    }

    @Then("^Submit the request without api key and get the response code as (\\d+)$")
    public void submit_the_request_without_api_key_and_get_the_response_code_as(int arg1) throws Throwable {
        response = RestAssured.given().contentType(ContentType.JSON)
                .body(requestParams.toJSONString()).post("/stations");
        response.then().assertThat().statusCode(arg1);
        response.then().assertThat().toString().contains("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.");
    }

    @When("^the following header is sent$")
    public void the_following_header_is_sent(DataTable arg1) throws Throwable {
        List<Map<String, String>> maps = arg1.asMaps(String.class, String.class);
        for (Map<String, String> map : maps) {
            requestParams.put("external_id", map.get("external_id"));
            requestParams.put("name", map.get("name"));
            requestParams.put("latitude", Float.parseFloat(map.get("latitude")));
            requestParams.put("longitude", Float.parseFloat(map.get("longitude")));
            requestParams.put("altitude", Float.parseFloat(map.get("altitude")));
        }
    }

    @Then("^Submit the request and  get the response code as (\\d+)$")
    public void submit_the_request_and_get_the_response_code_as(int arg1) throws Throwable {
        response = RestAssured.given().contentType(ContentType.JSON)
                .body(requestParams.toJSONString()).post("/stations?appid=" + apiKey);
        response.then().assertThat().statusCode(arg1);
    }

    @And("^Send the API keys \"([^\"]*)\"$")
    public void sendTheAPIKeys(String arg0) throws Throwable {
        apiKey = arg0;
    }

    @Then("^the following stations should be retrieved from DB$")
    public void the_following_stations_should_be_retrieved_from_DB(DataTable arg1) throws Throwable {
        List<Map<String, String>> maps = arg1.asMaps(String.class, String.class);
        for (Map<String, String> map : maps) {
            response.then().assertThat().toString().contains(map.get("external_id"));
            response.then().assertThat().toString().contains(map.get("name"));
            response.then().assertThat().toString().contains(map.get("latitude"));
            response.then().assertThat().toString().contains(map.get("longitude"));
            response.then().assertThat().toString().contains(map.get("altitude"));
        }
    }

    @When("^Submit the request in GET method and  get the response code as (\\d+)$")
    public void submit_the_request_in_GET_method_and_get_the_response_code_as(int arg1) throws Throwable {
        response = RestAssured.given().get("/stations?appid=" + apiKey);
        response.then().assertThat().statusCode(arg1);
    }

    @When("^I try to delete the following stations from DB and get the response code as (\\d+)$")
    public void i_try_to_delete_the_following_stations_from_DB_and_get_the_response_code_as(int arg1, DataTable arg2) throws Throwable {
        response = RestAssured.given().get("/stations?appid=" + apiKey);
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(response.asString());
        List<Map<String, String>> maps = arg2.asMaps(String.class, String.class);
        for (Map<String, String> map : maps) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                if ((jsonObject.get("external_id").getAsString()).equals(map.get("external_id"))) {
                    id = jsonObject.get("id").getAsString();
                    response = RestAssured.given().delete("/stations/" + id + "?appid=" + apiKey);
                    response.then().assertThat().statusCode(arg1);
                }
            }
        }
    }

    @Then("^Submit the previous delete request again and verify the response code as (\\d+)$")
    public void submit_the_previous_delete_request_again_and_verify_the_response_code_as(int arg1) throws Throwable {
        response = RestAssured.given().delete("/stations/" + id + "?appid=" + apiKey);
        response.then().assertThat().statusCode(arg1);
        response.then().assertThat().toString().contains("Station not found");
    }
}



