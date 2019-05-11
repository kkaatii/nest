package photon.crud.mybatis;

import org.apache.ibatis.annotations.Param;
import photon.model.Arrow;
import photon.model.ArrowType;
import photon.model.FrameArrow;

import java.util.List;
import java.util.Set;

public interface ArrowMapper {
    FrameArrow selectOne(int id);

    void insert(Arrow a);

    void insertWithTargetFrame(@Param("arrow") Arrow arrow, @Param("targetFrame") String targetFrame);

    List<FrameArrow> selectByOrigin(int origin);

    List<FrameArrow> selectBetween(@Param("origin") int origin, @Param("target") int target);

    List<FrameArrow> selectActive();

    List<Integer> originIdSet();

    void delete(int id);

    void deleteSimilar(Arrow a);

    Set<Integer> neighborIdSet(@Param("origin") int origin, @Param("type") ArrowType at);

    void deleteByNode(int id);

    void reactivateByNode(int id);

    void deactivateByNode(int id);

    int updateTargetFrame(@Param("target") int target, @Param("frame") String frame);

}
