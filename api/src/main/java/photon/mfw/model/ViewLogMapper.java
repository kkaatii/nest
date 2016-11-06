package photon.mfw.model;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface ViewLogMapper {

    void increment(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    void incrementTo(@Param("articleId") Integer articleId, @Param("userId") Integer userId, @Param("count") int count);

    void batchIncrement(@Param("articleIds") Iterable<Integer> articleIds, @Param("userId") Integer userId);

    void insert(ViewLog log);

    void init(@Param("articleId") Integer articleId);

    Collection<Integer> randomCollection(@Param("userId") Integer userId, @Param("size") int size, @Param("threshold") int viewThreshold);
}
