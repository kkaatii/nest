package photon.tube.auth;

import photon.tube.model.Owner;

import java.util.List;

public interface OafService {
    Integer GUEST_ID = 1;

    boolean authorized(AccessLevel accessLevel, Owner owner, String frame);

    List<String> getAccessibleFrames(Owner owner, AccessLevel access);

    Owner getOwnerByAuthId(String authId);
}
