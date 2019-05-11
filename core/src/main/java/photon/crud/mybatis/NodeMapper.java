package photon.crud.mybatis;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import photon.model.Node;
import photon.model.Point;

import java.util.List;
import java.util.Map;

public interface NodeMapper {

    Point preselectOne(int id);

    List<Point> preselectMany(@Param("ids") Iterable<Integer> ids);

    Node selectOne(int id);

    String selectFrame(int id);

    List<Point> preselectFromFrame(String frame);

    void insert(Node node);

    int update(Node node);

    void delete(int id);

    int setActive(@Param("id") int id, @Param("active") boolean active);

    List<Point> preselectInactive();

    @MapKey("id")
    Map<Integer, Point> preselectMap(@Param("ids") Iterable<Integer> ids);

    @MapKey("id")
    Map<Integer, Point> preselectMapByOwner(int ownerId);

    List<Point> preselectByOwner(int onwerId);

}
