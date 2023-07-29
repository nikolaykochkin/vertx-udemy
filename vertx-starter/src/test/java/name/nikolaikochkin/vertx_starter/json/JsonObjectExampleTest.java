package name.nikolaikochkin.vertx_starter.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectExampleTest {
    @Test
    void shouldEncodeAndDecodeJsonObjectFromString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id", 1);
        jsonObject.put("name", "Alice");
        jsonObject.put("loves_vertx", true);

        String encoded = jsonObject.encode();
        assertEquals("{\"id\":1,\"name\":\"Alice\",\"loves_vertx\":true}", encoded);

        JsonObject decodedJsonObject = new JsonObject(encoded);
        assertEquals(jsonObject, decodedJsonObject);
    }

    @Test
    void shouldEncodeAndDecodeJsonObjectFromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "Alice");
        map.put("loves_vertx", true);

        JsonObject jsonObject = new JsonObject(map);

        assertEquals(map, jsonObject.getMap());
    }

    @Test
    void shouldEncodeAndDecodeJsonArrayFromString() {
        JsonArray jsonArray = new JsonArray()
                .add(new JsonObject().put("id", 1))
                .add(new JsonObject().put("id", 2))
                .add(new JsonObject().put("id", 3));

        String encoded = jsonArray.encode();
        assertEquals("[{\"id\":1},{\"id\":2},{\"id\":3}]", encoded);

        JsonArray decodedJsonArray = new JsonArray(encoded);
        assertEquals(jsonArray, decodedJsonArray);
    }

    @Test
    void shouldMapJavaObjectToJsonObject() {
        Person alice = new Person(1, "Alice", true);
        JsonObject jsonObject = JsonObject.mapFrom(alice);
        assertEquals(alice, jsonObject.mapTo(Person.class));
    }
}