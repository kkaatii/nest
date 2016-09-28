package nest.data.persistence;

import java.util.*;

import nest.data.ArrowType;
import org.apache.ibatis.annotations.Param;

import nest.data.Arrow;

public interface ArrowMapper {
	Arrow selectOne(Integer id);
	
	void insert(Arrow a);
	
	List<Arrow> selectByOrigin(Integer origin);
	
	List<Arrow> selectAll();
	
	List<Integer> originIdSet();
	
	void delete(Integer id);
	
	void deleteSimilar(Arrow a);
	
	Set<Integer> neighborIdSet(@Param("origin") Integer origin, @Param("type") ArrowType at);

	void deleteByPoint(Integer id);
}
