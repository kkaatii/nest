package photon.tube.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.model.Owner;
import photon.tube.model.mybatis.OwnerAndFrameMapper;

import java.util.List;

/**
 * In the default implementation, the authenticity of the <tt>Owner</tt> object (i.e. whether the id and the nickname
 * truly exist and match with each other) is not checked as the object info is always passed from an authentication
 * layer.
 */
@Service
public class DefaultAuthService implements AuthService {
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public DefaultAuthService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorizedRead(Owner owner, String frame) {
        return frame != null && (hasAccess(AccessLevel.READ, owner, frame) || (oafMapper.selectAccess(GUEST_ID, frame) != null));
    }

    @Override
    public boolean authorizedConnect(Owner owner, String frame) {
        return hasAccess(AccessLevel.CONNECT, owner, frame);
    }

    @Override
    public boolean authorizedWrite(Owner owner, String frame) {
        return hasAccess(AccessLevel.WRITE, owner, frame);
    }

    @Override
    public List<String> getAccessibleFrames(Owner owner, AccessLevel access) {
        List<String> explicitlyAccessibleFrames = oafMapper.selectFramesAccessibleTo(owner.getId(), access.getValue());
        // The private frame for each owner is manually added as its access authorization is not stored in DB
        explicitlyAccessibleFrames.add(0, "<Private>@" + owner.getNickname());
        return explicitlyAccessibleFrames;
    }

    @Override
    public Owner getOwnerByAuthId(String authId) {
        return oafMapper.selectOwnerByAuthId(authId);
    }

    private boolean hasAccess(AccessLevel access, Owner owner, String frame) {
        if (frame == null) return false;
        if (frame.split("@")[1].equals(owner.getNickname()))
            return true;
        else {
            Integer a = oafMapper.selectAccess(owner.getId(), frame);
            return ((a == null) ? 0 : a) >= access.getValue();
        }
    }
}
