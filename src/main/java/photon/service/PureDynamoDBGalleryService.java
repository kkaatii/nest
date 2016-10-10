package photon.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.springframework.stereotype.Service;
import photon.gallery.Panel;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PureDynamoDBGalleryService implements GalleryService {

    private Table table;
    private List<Item> items;
    private Set<Integer> displayedItemPos;
    private LocalDate yesterday;
    private Index index;

    private static SecureRandom rnd = new SecureRandom();
    private static String TABLE_NAME = "mfw-gallery";
    private static String INDEX_NAME = "CreatedIndex";

    public PureDynamoDBGalleryService() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);
        items = new ArrayList<>();
        displayedItemPos = new HashSet<>();
        DynamoDB db = new DynamoDB(client);
        table = db.getTable(TABLE_NAME);
        yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        //yesterday = LocalDate.now();
        index = table.getIndex(INDEX_NAME);
    }

    @Override
    public boolean init() {
        int createdAtYesterday = yesterday.getYear() * 13 * 32 + yesterday.getMonthValue() *32 + yesterday.getDayOfMonth();
        return queryWithCreatedIndex(createdAtYesterday);
    }

    @Override
    public Panel[] nextBatch(int batchSize) {
        Panel[] p = new Panel[batchSize];

        if (!items.isEmpty()) {

            int size = items.size();
            int pos = rnd.nextInt(size);
            for (int i = 0; i < batchSize; i++) {
                if (displayedItemPos.size() == size) displayedItemPos.clear();
                while (!displayedItemPos.add(pos)) pos = rnd.nextInt(size);
                p[i] = new Panel(items.get(pos).toJSON());
            }
        }

        return p;
    }

    private boolean queryWithCreatedIndex(int indexValue) {
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("Created = :v_created")
                .withValueMap(new ValueMap().withInt(":v_created", indexValue));
        try {
            ItemCollection<QueryOutcome> itemCollection = index.query(querySpec);
            Iterator<Item> iter = itemCollection.iterator();
            items.clear();
            while (iter.hasNext()) {
                items.add(iter.next());
            }
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }
}
