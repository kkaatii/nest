package photon.tube.model;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ArrowMapper {
	Arrow selectOne(Integer id);
	
	void insert(Arrow a);
	
	List<Arrow> selectByOrigin(Integer origin);
	
	List<Arrow> selectActive();
	
	List<Integer> originIdSet();
	
	void delete(Integer id);
	
	void deleteSimilar(Arrow a);
	
	Set<Integer> neighborIdSet(@Param("origin") Integer origin, @Param("type") ArrowType at);

	void deleteByNode(Integer id);

	void reactivateByNode(Integer id);

	void deactivateByNode(Integer id);
}
