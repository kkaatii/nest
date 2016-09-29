package photon.data.persistence;

import java.util.List;
import java.util.Map;

import photon.data.NodeState;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import photon.data.Node;
import photon.data.Point;

public interface NodeMapper {

    Point preselectOne(Integer id);

    List<Point> preselectMany(@Param("ids") Iterable<Integer> ids);

    Node selectOne(Integer id);

    void insert(Node node);

    void delete(Integer id);

    void changeState(@Param("id") Integer id, @Param("state") NodeState state);

    List<Point> preselectByState(NodeState state);

    @MapKey("id")
    Map<Integer, Point> preselectMap(@Param("ids") Iterable<Integer> ids);
}
