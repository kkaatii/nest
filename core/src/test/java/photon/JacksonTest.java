package photon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Test how jackson library works.
 */
public class JacksonTest {
    public static void main(String[] args) throws IOException {
        String query = "{\"a\":[{\"b\":2},3]}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode nodes = objectMapper.readTree(query).get("a");
        for (JsonNode node : nodes) {
            if (node.fieldNames().hasNext()) {
                String field = node.fieldNames().next();
                System.out.println("Next node is: " + field + " "
                        + node.get(field).asText());
            } else System.out.println("Next node is: " + node.asText());
        }
    }
}
