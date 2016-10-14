package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.model.*;

import java.util.*;

/**
 * Created by Dun Liu on 10/10/2016.
 */

@Service
public class CrudService {

    private final CatalogMapper catalogMapper;
    private final ViewLogMapper viewLogMapper;
    private final FavoriteLogMapper favMapper;

    @Autowired
    public CrudService(CatalogMapper catalogMapper, ViewLogMapper viewLogMapper, FavoriteLogMapper favMapper) {
        this.catalogMapper = catalogMapper;
        this.viewLogMapper = viewLogMapper;
        this.favMapper = favMapper;
    }

    public Catalog put(Catalog catalog) {
        catalogMapper.insert(catalog);
        viewLogMapper.init(catalog.getCid());
        return catalog;
    }

    public void delete(Integer cid) {
        try {
            Dynamo.helper().deleteItem(cid);
            catalogMapper.delete(cid);
        } catch (Exception e) {
            System.err.print("Cannot remove item #" + cid + " from DynamoDB");
        }
    }

    public void markFavorite(Integer cid, boolean favorite) {
        favMapper.insert(new FavoriteLog(1, cid));
    }

    public void putAll(Catalog[] catalogs) {
        if (catalogs != null)
            Arrays.stream(catalogs).forEach(this::put);
    }

    public List<Catalog> randomBuffer() {
        return catalogMapper.randomCollection();
    }
}
