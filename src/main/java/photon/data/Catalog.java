package photon.data;

import java.util.Date;

/**
 * Created by Dun Liu on 10/10/2016.
 */
public class Catalog {
    String articleUrl; // Serve as Primary Key for Dynamodb
    String country; // Parsed by Google Maps API from article destination
    Date created;



}
