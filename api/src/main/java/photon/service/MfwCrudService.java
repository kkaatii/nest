package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.model.*;

import java.util.*;

@Service
public class MfwCrudService {

    private final CatalogMapper catalogMapper;
    private final ViewLogMapper viewLogMapper;
    private final FavoriteLogMapper favMapper;
    private final UserMapper userMapper;

    @Autowired
    public MfwCrudService(CatalogMapper catalogMapper, ViewLogMapper viewLogMapper, FavoriteLogMapper favMapper, UserMapper userMapper) {
        this.catalogMapper = catalogMapper;
        this.viewLogMapper = viewLogMapper;
        this.favMapper = favMapper;
        this.userMapper = userMapper;
    }

    public Catalog put(Catalog catalog) {
        catalogMapper.insert(catalog);
        //viewLogMapper.insert(new ViewLog(catalog.getArticleId(), User.DEFAULT_USER_ID));
        viewLogMapper.init(catalog.getArticleId());
        return catalog;
    }

    public void delete(Integer articleId) {
        try {
            Dynamo.helper().deleteItem(articleId);
            catalogMapper.delete(articleId);
        } catch (Exception e) {
            System.err.print("Cannot remove item #" + articleId + " from DynamoDB");
        }
    }

    public void markFavorite(Integer articleId, Integer userId, boolean favorite) {
        favMapper.insert(new FavoriteLog(articleId, userId));
    }

    public void putAll(Catalog[] catalogs) {
        if (catalogs != null)
            Arrays.stream(catalogs).forEach(this::put);
    }

    public List<Catalog> randomBuffer(Integer userId, int size, int viewThreshold) {
        Collection<Integer> randomArticleIds = new HashSet<>(viewLogMapper.randomCollection(userId, size, viewThreshold));
        return catalogMapper.selectMany(randomArticleIds);
    }

    public void incrementViewCount(Integer articleId, Integer userId) {
        viewLogMapper.increment(articleId, userId);
    }

    public void batchIncrementViewCount(Iterable<Integer> articleIds, Integer userId) {
        viewLogMapper.batchIncrement(articleIds, userId);
    }

    public void noshow(Integer articleId, String name) {
        viewLogMapper.incrementTo(articleId, userMapper.find(name), 255);
    }

    public Integer findUserId(String name) {
        return userMapper.find(name);
    }
}
