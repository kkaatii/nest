package photon.service;

import photon.data.*;
import photon.data.cache.ArrowCacheCatalog;
import photon.data.persistence.ArrowMapper;
import photon.data.persistence.ExtensionMapper;
import photon.data.persistence.NodeMapper;
import photon.query.KeywordIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.service.CrudService;

import java.util.List;
import java.util.Map;

@Service
public class CachedMybatisCrudService implements CrudService {

    private final ArrowCacheCatalog catalog = new ArrowCacheCatalog();

    private NodeMapper nodeMapper;
    private ArrowMapper arrowMapper;
    private ExtensionMapper extensionMapper;
    private KeywordIndexer keywordIndexer;

    @Autowired
    public void setKeywordIndexer(KeywordIndexer keywordIndexer) {
        this.keywordIndexer = keywordIndexer;
    }

    private void precache(Integer id, List<Arrow> arrows) {
        catalog.cache(id, arrows);
    }

    @Autowired
    public CachedMybatisCrudService(NodeMapper nodeMapper, ArrowMapper arrowMapper, ExtensionMapper extensionMapper) {
        this.nodeMapper = nodeMapper;
        this.arrowMapper = arrowMapper;
        this.extensionMapper = extensionMapper;
    }

    @Override
    public Node putNode(Node node) {
        Digest.digest(node);
        nodeMapper.insert(node);

        // Create index for search
        String content;
        if ((content = node.getContent()) != null) {
            keywordIndexer.anatomize(content).forEach(
                    kw -> putArrow(new Arrow(putNode(new Node(kw, NodeType.SEARCH_KEYWORD)),
                            ArrowType.KEYWORD_OF,
                            node))
            );
        }

        return node;
    }

    @Override
    public Node getNode(int id) {
        return nodeMapper.selectOne(id);
    }

    @Override
    public Point getPoint(int id) {
        return nodeMapper.preselectOne(id);
    }

    @Override
    public List<Point> getPoints(Iterable<Integer> nodeIdSet) {
        return nodeMapper.preselectMany(nodeIdSet);
    }

    @Override
    public Map<Integer, Point> getPointMap(Iterable<Integer> nodeIdSet) {
        return nodeMapper.preselectMap(nodeIdSet);
    }

    @Override
    public void deleteNode(int id) {
        nodeMapper.delete(id);
        arrowMapper.deleteByPoint(id);
    }

    @Override
    public void updateNodeState(int id, NodeState ns) {
        nodeMapper.changeState(id, ns);
    }

    @Override
    public void putArrow(Arrow arrow) {
        arrowMapper.insert(arrow);
        arrowMapper.insert(arrow.reverse());
        catalog.recordArrow(arrow);
        catalog.recordArrow(arrow.reverse());
    }

    @Override
    public void putArrow(Arrow arrow, Extension ext) {
        if (ext != null) {
            extensionMapper.insert(ext);
            arrow.setExtension(ext.getId());
        }
        putArrow(arrow);
    }

    @Override
    public List<Arrow> getAllArrowsBetween(Integer origin, Integer target) {
        if (!catalog.contains(origin))
            precache(origin, arrowMapper.selectByOrigin(origin));
        return catalog.arrowsBetween(origin, target);
    }

    @Override
    public List<Arrow> getAllArrowsOriginatingFrom(Integer origin, ArrowType at) {
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
        extensionMapper.delete(arrow.getExtension());
        catalog.eraseArrow(arrow);
        catalog.eraseArrow(arrow.reverse());
    }

    @Override
    public List<Extension> getExtensions(Iterable<Integer> extIdSet) {
        return extensionMapper.selectMany(extIdSet);
    }

    @Override
    public void updateExtension(Extension ext) {
        extensionMapper.insert(ext);
    }

}