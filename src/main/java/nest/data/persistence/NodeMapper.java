package nest.data.persistence;

import java.util.List;
import java.util.Map;

import nest.data.NodeState;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import nest.data.Node;
import nest.data.Point;

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
