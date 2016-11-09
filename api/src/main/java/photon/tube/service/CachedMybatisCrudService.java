package photon.tube.service;

import photon.tube.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.model.cache.ArrowCacheCatalog;
import photon.tube.query.KeywordIndexer;

import java.util.List;
import java.util.Map;

public class CachedMybatisCrudService implements CrudService {

    private final ArrowCacheCatalog catalog = new ArrowCacheCatalog();

    private NodeMapper nodeMapper;
    private ArrowMapper arrowMapper;
    private KeywordIndexer keywordIndexer;

    @Autowired
    public void setKeywordIndexer(KeywordIndexer keywordIndexer) {
        this.keywordIndexer = keywordIndexer;
    }

    private void precache(Integer id, List<Arrow> arrows) {
        catalog.cache(id, arrows);
    }

    @Autowired
    public CachedMybatisCrudService(NodeMapper nodeMapper, ArrowMapper arrowMapper) {
        this.nodeMapper = nodeMapper;
        this.arrowMapper = arrowMapper;
    }

    @Override
    public Node putNode(Node node) {
        node.doDigest();
        nodeMapper.insert(node);

        // Create index for search
        String content;
        if ((content = node.getContent()) != null) {
            keywordIndexer.anatomize(content).forEach(
                    keyword -> putArrow(new Arrow(putNode(new Node(NodeType.SEARCH_KEYWORD, keyword)),
                            ArrowType.KEYWORD_OF,
                            node))
            );
        }

        return node;
    }

    @Override
    public Node getNode(Integer id) {
        return nodeMapper.selectOne(id);
    }

    @Override
    public Point getPoint(Integer id) {
        return nodeMapper.preselectOne(id);
    }

    @Override
    public List<Point> getPoints(Iterable<Integer> ids) {
        return nodeMapper.preselectMany(ids);
    }

    @Override
    public Map<Integer, Point> getPointMap(Iterable<Integer> ids) {
        return nodeMapper.preselectMap(ids);
    }

    //TODO add cache change
    @Override
    public void deleteNode(Integer id) {
        nodeMapper.delete(id);
        arrowMapper.deleteByNode(id);
    }

    @Override
    public void activateNode(Integer id, boolean toBeActive) {
        nodeMapper.setActive(id, toBeActive);
        if (toBeActive) {
            arrowMapper.reactivateByNode(id);
        } else {
            arrowMapper.deactivateByNode(id);
        }
    }

    @Override
    public void putArrow(Arrow arrow) {
        arrowMapper.insert(arrow);
        arrowMapper.insert(arrow.reverse());
        catalog.recordArrow(arrow);
        catalog.recordArrow(arrow.reverse());
    }

    @Override
    public List<Arrow> getAllArrowsBetween(Integer origin, Integer target) {
        if (!catalog.contains(origin))
            precache(origin, arrowMapper.selectByOrigin(origin));
        return catalog.arrowsBetween(origin, target);
    }

    @Override
    public List<Arrow> getAllArrowsStartingFrom(Integer origin, ArrowType at) {
        if (!catalog.contains(origin))
            precache(origin, arrowMapper.selectByOrigin(origin));
        return catalog.arrowsByType(origin, at);
    }

    @Override
    public Arrow getArrow(Integer origin, ArrowType at, Integer target) {
        if (!catalog.contains(origin))
            precache(origin, arrowMapper.selectByOrigin(origin));
        return catalog.arrow(origin, at, target);
    }

    @Override
    public void deleteArrow(Arrow arrow) {
        if (!catalog.contains(arrow.getOrigin()))
            precache(arrow.getOrigin(), arrowMapper.selectByOrigin(arrow.getOrigin()));
        arrowMapper.deleteSimilar(arrow);
        arrowMapper.deleteSimilar(arrow.reverse());
        catalog.eraseArrow(arrow);
        catalog.eraseArrow(arrow.reverse());
    }

}
