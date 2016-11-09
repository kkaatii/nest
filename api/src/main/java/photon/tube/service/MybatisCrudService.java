package photon.tube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MybatisCrudService implements CrudService {

    private final NodeMapper nodeMapper;
    private final ArrowMapper arrowMapper;

    @Autowired
    public MybatisCrudService(NodeMapper nodeMapper, ArrowMapper arrowMapper) {
        this.nodeMapper = nodeMapper;
        this.arrowMapper = arrowMapper;
    }

    @Override
    public Node putNode(Node n) {
        n.doDigest();
        nodeMapper.insert(n);
        return n;
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

    @Override
    public void deleteNode(Integer id) {
        nodeMapper.delete(id);
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
    public void putArrow(Arrow a) {
        arrowMapper.insert(a);
        arrowMapper.insert(a.reverse());
    }

    @Override
    public List<Arrow> getAllArrowsBetween(Integer origin, Integer target) {
        return arrowMapper.selectBetween(origin, target);
    }

    @Override
    public Arrow getArrow(Integer origin, ArrowType at, Integer target) {
        List<Arrow> candidates = arrowMapper.selectBetween(origin, target);
        if (candidates != null) {
            for (Arrow arrow : candidates) {
                if (arrow.getType().equals(at)) return arrow;
            }
        }
        return null;
    }

    @Override
    public List<Arrow> getAllArrowsStartingFrom(Integer origin, ArrowType at) {
        return arrowMapper.selectByOrigin(origin).stream().filter(arrow -> arrow.getType().equals(at)).collect(Collectors.toList());
    }

    @Override
    public void deleteArrow(Arrow a) {
        arrowMapper.deleteSimilar(a);
        arrowMapper.deleteSimilar(a.reverse());
    }
}
