package photon.gallery;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import org.springframework.stereotype.Service;
import photon.service.GalleryService;

import java.security.SecureRandom;
import java.util.*;

@Service
public class MfwGalleryService implements GalleryService {

    private AmazonDynamoDBClient client;
    private Table table;
    private List<Item> items;
    private Set<Integer> displayedItemPos;

    private static SecureRandom rnd = new SecureRandom();
    private static String TABLE_NAME = "mafengwo-pic-gallery";

    public MfwGalleryService() {
        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);
        items = new ArrayList<>();
        displayedItemPos = new HashSet<>();
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

    @Override
    public boolean permanentRemove(PanelId panelId) {
        return false;
    }

    @Override
    public boolean init() {
        DynamoDB db = new DynamoDB(client);
        table = db.getTable(TABLE_NAME);

        ScanSpec scanSpec = new ScanSpec();
        try {
            ItemCollection<ScanOutcome> itemCollection = table.scan(scanSpec);
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
