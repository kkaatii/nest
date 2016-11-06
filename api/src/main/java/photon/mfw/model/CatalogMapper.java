package photon.mfw.model;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CatalogMapper {

    Catalog select(Integer articleId);

    void insert(Catalog catalog);

    List<Catalog> selectMany(@Param("articleIds") Iterable<Integer> articleIds);

    void delete(Integer articleId);
}
