package photon.crud;

import photon.model.Owner;

import java.util.List;

/**
 * OAF stands for Owner And Frame. The service is responsible for determining which frames the current user/owner has
 * access to.
 */
public interface OafService {
    int GUEST_OWNER_ID = 1;

    boolean authorized(AccessLevel accessLevel, Owner owner, String frame);

    List<String> getAccessibleFrames(Owner owner, AccessLevel access);

    Owner getOwnerByAuthId(String authId);
}
