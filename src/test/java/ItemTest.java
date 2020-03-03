import Pojo.Item;
import io.restassured.http.Cookies;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

public class ItemTest {
    private Cookies cookies;
    private String name;

    @Test
    public void itemSet() {
        name = getAlphaNumericString(30);
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

        // Create an item category
        Item item = new Item();
        List<String> role = new ArrayList<>();

        role.add("5b8bedceb1e0bfc07b00882a");
        item.setRoles(role);
        item.setName(name);
        item.setActive(true);
        item.setSchoolId("5e035f8c9ea1a129f71ac585");


        String itemId = given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(item)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/item-category")
                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        // Search our item category

        Map<String, String> searchItem = new HashMap<>();
        searchItem.put("name", item.getName());
        searchItem.put("schoolId", item.getSchoolId());

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(searchItem)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/item-category/search")
                .then().log().everything()
                .body("id", hasItem(itemId))
        ;

        // Delete the item

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://test-basqar.mersys.io/school-service/api/item-category/" + itemId)
                .then()
                .statusCode(204);

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
