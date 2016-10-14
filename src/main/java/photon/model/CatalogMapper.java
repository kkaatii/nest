package photon.model;

import java.util.List;

public interface CatalogMapper {

    Catalog select(Integer cid);

    void insert(Catalog catalog);

    List<Catalog> selectMany(Iterable<Integer> cids);

    void delete(Integer cid);

    List<Catalog> randomCollection();
}
