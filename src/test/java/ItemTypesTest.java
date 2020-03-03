import Pojo.Category;
import Pojo.ItemTypes;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

public class ItemTypesTest {
    private String name;

    @Test
    public void itemType() {

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

        // Create an item type

        ItemTypes itypes = new ItemTypes();
        Category category = new Category();
        category.setId("5e1d26a94e85d930fc4508c5");
        itypes.setActive(true);
        itypes.setName(name);
        itypes.setSchoolId("5e035f8c9ea1a129f71ac585");
        itypes.setCategory(category);

        String itypeId = given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(itypes)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/item-type")
                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        // Search our item type

        Map<String, String> searchItemType = new HashMap<>();
        searchItemType.put("name", itypes.getName());
        searchItemType.put("schoolId", itypes.getSchoolId());
        searchItemType.put("itemCategoryId", category.getId());


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(searchItemType)
                .when()
                .post("https://test-basqar.mersys.io/school-service/api/item-type/search")
                .then().log().everything()
                .body("id", hasItem(itypeId))
        ;

        // Delete the item type

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://test-basqar.mersys.io/school-service/api/item-type/" + itypeId)
                .then()
                .statusCode(204);
    }

    private String getAlphaNumericString(int n) {
        // choose a Character random from this String
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
