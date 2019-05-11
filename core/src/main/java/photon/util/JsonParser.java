package photon.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by dan on 16/10/2017.
 */
public class JsonParser {
    private static final JsonParser INSTANCE = new JsonParser();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public JsonParser get() {
        return INSTANCE;
    }



    private JsonParser() {}
}
