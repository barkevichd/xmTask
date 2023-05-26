package tests;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.CustomException;

public class ApiTest {

    @Test()
    public void apiTest() throws JsonProcessingException {
        //using rest assured
        //normally should be splitted into several methods but for test task it does not matters

        //Find film with a title ”A New Hope”
        JsonNode entries = makeGetRequest("https://swapi.dev/api/films").get("results");
        JsonNode film = null;
        for (JsonNode n : entries) {
            if (n.get("title").asText().equals("A New Hope")) {
                film = n;
                break;
            }
        }
        //Using previous response (1) find person with name “Biggs Darklighter” among
        //the characters that were part of that film.
        if (film != null) {
            JsonNode characters = film.get("characters");
            JsonNode person = null;
            for (JsonNode n : characters) {
                JsonNode actualObj = makeGetRequest(n.asText());
                if (makeGetRequest(n.asText()).get("name").asText().equals("Biggs Darklighter")) {
                    person = actualObj;
                    break;
                }
            }
            //Using previous response (2) find which starship he/she was flying on.
            if (person != null) {
                if (person.get("starships").size() > 1) {
                    System.out.println("There are more than one ship found! First one will be taken!");
                }
                if (person.get("starships").size() < 1) {
                    throw new CustomException("No starships was found!");
                }

                //Using previous response (3) check next:
                //a. starship class is “Starfighter”
                //b. “Luke Skywalker” is among pilots that were also flying this kind of starship
                JsonNode ship = makeGetRequest(person.get("starships").get(0).asText());
                Assert.assertEquals(ship.get("starship_class").asText(), "Starfighter",
                        "Expected Starship class not found");
                boolean pilotFound = false;
                for (JsonNode pilot : ship.get("pilots")) {
                    if (makeGetRequest(pilot.asText()).get("name").asText().equals("Luke Skywalker")) {
                        pilotFound = true;
                    }
                }
                Assert.assertTrue(pilotFound, "Pilot was not found");
            } else {
                throw new CustomException("person was not found!");
            }
        } else {
            throw new CustomException("film was not found!");
        }
    }

    private JsonNode makeGetRequest(String url) throws JsonProcessingException {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .extract().response();

        Assert.assertEquals(response.statusCode(), 200);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body().asString());
    }
}
