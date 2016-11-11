package photon.tube.model;

/**
 * Created by Dun Liu on 11/9/2016.
 */
public interface OwnerAndFrameMapper {
    void addOwner(Owner o);
    void addAlias(Owner o);
    Integer getIdByAuthId(String authId);
    void deleteOwnerById(Integer id);
    void deleteAlias(Owner o);
}
