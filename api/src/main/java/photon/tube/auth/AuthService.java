package photon.tube.auth;

import photon.tube.model.Owner;

import java.util.List;

public interface AuthService {
    Integer GUEST_ID = 1;

    boolean authorizedRead(Owner owner, String frame);

    boolean authorizedConnect(Owner owner, String frame);

    boolean authorizedWrite(Owner owner, String frame);

    List<String> getAccessibleFrames(Owner owner, AccessLevel access);

    Owner getOwnerByAuthId(String authId);
}
