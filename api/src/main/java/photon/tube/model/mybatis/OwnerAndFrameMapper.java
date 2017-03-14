package photon.tube.model.mybatis;

import org.apache.ibatis.annotations.Param;
import photon.tube.model.Owner;

import java.util.List;

/**
 * Created by Dun Liu on 11/9/2016.
 */
public interface OwnerAndFrameMapper {

    void addOwner(Owner o);

    Owner selectOwnerByAuthId(String authId);

    void deleteOwnerById(int id);

    void addAlias(@Param("ownerId") int ownerId, @Param("authId") String authId);

    void deleteAlias(@Param("ownerId") int ownerId, @Param("authId") String authId);

    List<String> selectFramesAccessibleTo(@Param("ownerId") int ownerId, @Param("access") int access);

    Integer selectAccess(@Param("ownerId") int ownerId, @Param("frame") String frame);

}
