package photon.model;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface ViewLogMapper {

    void increment(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    void batchIncrement(@Param("articleIds") Iterable<Integer> articleIds, @Param("userId") Integer userId);

    void init(ViewLog log);

    Collection<Integer> randomCollection(@Param("userId") Integer userId, @Param("size") int size, @Param("threshold") int viewThreshold);
}
