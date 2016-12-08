package photon.tube.model.mybatis;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import photon.tube.model.Node;
import photon.tube.model.Point;

import java.util.List;
import java.util.Map;

public interface NodeMapper {

    Point preselectOne(Integer id);

    List<Point> preselectMany(@Param("ids") Iterable<Integer> ids);

    Node selectOne(Integer id);

    String selectFrame(Integer id);

    List<Point> preselectFromFrame(String frame);

    void insert(Node node);

    int update(Node node);

    void delete(Integer id);

    int setActive(@Param("id") Integer id, @Param("active") boolean active);

    List<Point> preselectInactive();

    @MapKey("id")
    Map<Integer, Point> preselectMap(@Param("ids") Iterable<Integer> ids);

    @MapKey("id")
    Map<Integer, Point> preselectMapByOwner(Integer ownerId);

    List<Point> preselectByOwner(Integer onwerId);
}
