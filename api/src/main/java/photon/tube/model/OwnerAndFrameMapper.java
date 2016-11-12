package photon.tube.model;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Dun Liu on 11/9/2016.
 */
public interface OwnerAndFrameMapper {
    void addOwner(Owner o);
    Owner selectByAuthId(String authId);
    void deleteOwnerById(Integer id);
    void addAlias(@Param("ownerId") Integer ownerId, @Param("authId") String authId);
    void deleteAlias(@Param("ownerId") Integer ownerId, @Param("authId") String authId);
    Integer selectAccess(@Param("ownerId") Integer ownerId, @Param("frame") String frame);
}
