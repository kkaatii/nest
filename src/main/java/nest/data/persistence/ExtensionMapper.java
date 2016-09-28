package nest.data.persistence;


import org.apache.ibatis.annotations.Param;

import nest.data.Extension;

import java.util.List;

public interface ExtensionMapper {

	void insert(Extension ext);

	List<Extension> selectMany(@Param("ids") Iterable<Integer> ids);

	void delete(Integer id);
}
