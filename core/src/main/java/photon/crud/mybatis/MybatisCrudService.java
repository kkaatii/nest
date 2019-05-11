package photon.crud.mybatis;

import photon.model.*;
import photon.crud.CrudService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MybatisCrudService implements CrudService {

    private final NodeMapper nodeMapper;
    private final ArrowMapper arrowMapper;

    @Inject
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
            String prevFrame = nodeMapper.selectFrame(n.getId());
            int c = nodeMapper.update(n);
            if (c > 0 && !prevFrame.equals(n.getFrame())) {
                arrowMapper.updateTargetFrame(n.getId(), n.getFrame());
            }
            return n;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Node getNode(int id) {
        return nodeMapper.selectOne(id);
    }

    @Override
    public String getNodeFrame(int id) {
        return nodeMapper.selectFrame(id);
    }

    @Override
    public Point getPoint(int id) {
        return nodeMapper.preselectOne(id);
    }

    @Override
    public List<Point> listPoints(Iterable<Integer> ids) {
        return nodeMapper.preselectMany(ids);
    }

    @Override
    public Map<Integer, Point> pointMapOf(Collection<Integer> ids) {
        return nodeMapper.preselectMap(ids);
    }

    @Override
    public void deleteNode(int id) {
        nodeMapper.delete(id);
    }

    @Override
    public void activateNode(int id, boolean toBeActive) {
        int c = nodeMapper.setActive(id, toBeActive);
        if (c > 0) {
            if (toBeActive) {
                arrowMapper.reactivateByNode(id);
            } else {
                arrowMapper.deactivateByNode(id);
            }
        }
    }

    @Override
    public void putArrow(Arrow a) {
        arrowMapper.insertWithTargetFrame(a, nodeMapper.selectFrame(a.getTarget()));
        arrowMapper.insertWithTargetFrame(a.reverse(), nodeMapper.selectFrame(a.getOrigin()));
    }

    @Override
    public List<FrameArrow> listFrameArrowsBetween(int origin, int target) {
        return arrowMapper.selectBetween(origin, target);
    }

    @Override
    public Arrow getArrow(int origin, ArrowType at, int target) {
        List<FrameArrow> candidates = arrowMapper.selectBetween(origin, target);
        if (candidates != null) {
            for (Arrow arrow : candidates) {
                if (arrow.getType().isType(at)) return arrow;
            }
        }
        return null;
    }

    @Override
    public List<FrameArrow> listFrameArrowsStartingFrom(int origin, ArrowType at) {
        return arrowMapper.selectByOrigin(origin).stream().filter(arrow -> arrow.getType().isType(at)).collect(Collectors.toList());
    }

    @Override
    public void deleteArrow(Arrow a) {
        arrowMapper.deleteSimilar(a);
        arrowMapper.deleteSimilar(a.reverse());
    }

    @Override
    public List<Point> listPointsInFrame(String f) {
        return nodeMapper.preselectFromFrame(f);
    }

    @Override
    public Map<Integer, Point> pointMapOwnedBy(int oid) {
        return nodeMapper.preselectMapByOwner(oid);
    }

    @Override
    public List<Point> listPointsOwnedBy(int oid) {
        return nodeMapper.preselectByOwner(oid);
    }
}
