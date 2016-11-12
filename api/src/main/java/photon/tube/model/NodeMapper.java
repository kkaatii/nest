package photon.tube.model;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NodeMapper {

    Point preselectOne(Integer id);

    List<Point> preselectMany(@Param("ids") Iterable<Integer> ids);

    Node selectOne(Integer id);

    String selectFrame(Integer id);

    List<Point> preselectFromFrame(String frame);

    void insert(Node node);

    void update(Node node);

    void delete(Integer id);

    void setActive(@Param("id") Integer id, @Param("active") boolean active);

    List<Point> preselectInactive();

    @MapKey("id")
    Map<Integer, Point> preselectMap(@Param("ids") Iterable<Integer> ids);
}
