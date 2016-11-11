package photon.mfw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.mfw.model.*;

import java.util.*;

@Service
public class CrudService {

    private final CatalogMapper catalogMapper;
    private final ViewLogMapper viewLogMapper;
    private final FavoriteLogMapper favMapper;
    private final UserMapper userMapper;

    private static final int MAX_VIEW_COUNT = 255;

    @Autowired
    public CrudService(CatalogMapper catalogMapper, ViewLogMapper viewLogMapper, FavoriteLogMapper favMapper, UserMapper userMapper) {
        this.catalogMapper = catalogMapper;
        this.viewLogMapper = viewLogMapper;
        this.favMapper = favMapper;
        this.userMapper = userMapper;
    }

    public Catalog put(Catalog catalog) {
        if (catalogMapper.select(catalog.getArticleId()) != null) return null;
        catalogMapper.insert(catalog);
        //viewLogMapper.insert(new ViewLog(catalog.getArticleId(), User.DEFAULT_USER_ID));
        viewLogMapper.initViewLog(catalog.getArticleId());
        return catalog;
    }

    public void delete(Integer articleId) {
        try {
            DynamoService.helper().deleteItem(articleId);
            catalogMapper.delete(articleId);
        } catch (Exception e) {
            System.err.print("Cannot remove item #" + articleId + " from DynamoDB");
        }
    }

    public void markFavorite(Integer articleId, Integer userId, boolean favorite) {
        favMapper.insert(new FavoriteLog(articleId, userId));
        viewLogMapper.incrementTo(articleId, userId, MAX_VIEW_COUNT);
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

    public void noshow(Integer articleId, Integer userId) {
        viewLogMapper.incrementTo(articleId, userId, MAX_VIEW_COUNT);
    }

    public Integer findUserId(String name) {
        return userMapper.find(name);
    }
}
