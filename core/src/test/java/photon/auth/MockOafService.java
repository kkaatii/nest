package photon.auth;

import photon.auth.AccessLevel;
import photon.auth.OafService;
import photon.model.Owner;

import java.util.List;

public class MockOafService implements OafService {
    private boolean policy = true;

    public void setPolicy(boolean policy) {
        this.policy = policy;
    }

    @Override
    public boolean authorized(AccessLevel accessLevel, Owner owner, String frame) {
        return frame != null && policy;
    }

    @Override
    public List<String> getAccessibleFrames(Owner owner, AccessLevel access) {
        return null;
    }

    @Override
    public Owner getOwnerByAuthId(String authId) {
        return null;
    }
}
