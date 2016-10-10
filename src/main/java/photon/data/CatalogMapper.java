package photon.data;

import java.util.List;

public interface CatalogMapper {

    Catalog selectOne(Integer id);

    void insert(Catalog node);

    void delete(Integer id);

    List<Catalog> selectMany(Iterable<Integer> ids);

}
