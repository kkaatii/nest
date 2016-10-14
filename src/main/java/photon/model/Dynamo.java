package photon.model;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper singleton for accessing DynamoDB.
 */
public class Dynamo {
    private static Dynamo ourInstance = new Dynamo();

    private final DynamoDB db;
    private final Table table;

    private static String TABLE_NAME = "mfw-gallery-v2";
    private static String PK_NAME = "ArticleId";

    private static SecureRandom rnd = new SecureRandom();

    public static Dynamo helper() {
        return ourInstance;
    }

    private Dynamo() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);
        db = new DynamoDB(client);
        table = db.getTable(TABLE_NAME);
    }

    public boolean deleteItem(Integer cid) {
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(PK_NAME, cid);
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<String> batchGetAsJson(Object[] cids) {
        try {
            TableKeysAndAttributes kaa = new TableKeysAndAttributes(TABLE_NAME);
            kaa.addHashOnlyPrimaryKeys(PK_NAME, cids);
            BatchGetItemOutcome outcome = db.batchGetItem(kaa);
            return outcome.getTableItems().get(TABLE_NAME).stream().map(Item::toJSON).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Failed to get a batch of articles!");
        }
        return new ArrayList<>();
    }
}
