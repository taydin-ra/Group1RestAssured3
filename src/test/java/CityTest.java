import Pojo.City;
import Pojo.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CityTest {

    private String name;
    private String code;
    private String countryId;
    private Cookies cookies;


    @BeforeClass

    public void initial() {
        name = getAlphaNumericString(30);
        code = getAlphaNumericString(10);

        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("username", "nigeria_tenant_admin");
        loginCredentials.put("password", "TnvLOl54WxR75vylop2A");

        cookies = given()
                .contentType(ContentType.JSON)
                .body(loginCredentials)
                .when()
                .post("https://test-basqar.mersys.io/auth/login")
                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies();


        Country country = new Country();
        country.setName(name);
        country.setCode(code);
        // creating country
        countryId = given()
                .cookies(cookies)
                .body(country)
                .contentType(ContentType.JSON)
                .when()
                .log().body()
                .post("https://test-basqar.mersys.io/school-service/api/countries")
                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id");
    }

    @Test(priority = 1)
    public void createCity() {

        Integer randomNumber = new Random().nextInt(5);

        for (int i = 0; i < randomNumber; i++) {
            createCity(countryId);

        }
        // search the cities with the countryId
        Map<String, String> searchCity = new HashMap<>();
        searchCity.put("name", "");
        searchCity.put("countryId", countryId);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(searchCity)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/cities/search")
                .then().log().everything()
                .body("id", hasSize(randomNumber))
                .body("country.id", everyItem(equalTo(countryId)))
        ;

        // Extract the list of cities
        Map<String, String> searchCities = new HashMap<>();
        searchCities.put("name", "");
        searchCities.put("countryId", countryId);

        List<String> myCitiesID = given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(searchCities)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/cities/search")
                .then().log().everything()
                .extract().jsonPath().getList("id", String.class);


        // delete the cities
        for (int i = 0; i < myCitiesID.size(); i++) {
            deleteTheCity(myCitiesID.get(i));
        }
    }

    @Test(priority = 2)
    public void deleteTheCountry() {
        given()
                .cookies(cookies)
                .when()
                .log().body()
                .delete("https://test-basqar.mersys.io/school-service/api/countries/" + countryId)
                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    private void deleteTheCity(String s) {
        given()
                .cookies(cookies)
                .when()
                .log().body()
                .delete("https://test-basqar.mersys.io/school-service/api/cities/" + s)
                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    private void createCity(String countryId) {
        name = getAlphaNumericString(30);
        code = getAlphaNumericString(10);
        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("username", "nigeria_tenant_admin");
        loginCredentials.put("password", "TnvLOl54WxR75vylop2A");

        Cookies cookies = given()
                .contentType(ContentType.JSON)
                .body(loginCredentials)
                .when()
                .post("https://test-basqar.mersys.io/auth/login")
                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies();
        // create a city
        City city = new City();

        Country c1 = new Country();

        city.setName(name);
        c1.setId(countryId);
        city.setCountry(c1);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(city)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/cities")
                .then()
                .log().body()
                .statusCode(201)
        ;


    }

    private String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
