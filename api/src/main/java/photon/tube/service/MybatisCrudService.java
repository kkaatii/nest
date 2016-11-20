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
    public Node updateNode(Node n) {
        n.doDigest();
        try {
            nodeMapper.update(n);
            return n;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Node getNode(Integer id) {
        return nodeMapper.selectOne(id);
    }

    @Override
    public String getNodeFrame(Integer id) {
        return nodeMapper.selectFrame(id);
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
        arrowMapper.insertWithTargetFrame(a, nodeMapper.selectFrame(a.getTarget()));
        arrowMapper.insertWithTargetFrame(a.reverse(), nodeMapper.selectFrame(a.getOrigin()));
    }

    @Override
    public List<FrameArrow> getAllArrowsBetween(Integer origin, Integer target) {
        return arrowMapper.selectBetween(origin, target);
    }

    @Override
    public Arrow getArrow(Integer origin, ArrowType at, Integer target) {
        List<FrameArrow> candidates = arrowMapper.selectBetween(origin, target);
        if (candidates != null) {
            for (Arrow arrow : candidates) {
                if (arrow.getType().equals(at)) return arrow;
            }
        }
        return null;
    }

    @Override
    public List<FrameArrow> getAllArrowsStartingFrom(Integer origin, ArrowType at) {
        return arrowMapper.selectByOrigin(origin).stream().filter(arrow -> arrow.getType().equals(at)).collect(Collectors.toList());
    }

    @Override
    public void deleteArrow(Arrow a) {
        arrowMapper.deleteSimilar(a);
        arrowMapper.deleteSimilar(a.reverse());
    }

    @Override
    public List<Point> getAllFromFrame(String f) {
        return nodeMapper.preselectFromFrame(f);
    }

    @Override
    public Map<Integer, Point> getPointMapOwnedBy(Integer oid) {
        return nodeMapper.preselectMapByOwner(oid);
    }

    @Override
    public List<Point> getPointsOwnedBy(Integer oid) {
        return nodeMapper.preselectByOwner(oid);
    }
}
