package photon.tube.model.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.auth.AccessLevel;
import photon.tube.auth.OafService;
import photon.tube.model.Owner;

import java.util.List;

/**
 * In the default implementation, the authenticity of the <tt>Owner</tt> object (i.e. whether the id and the nickname
 * truly exist and match with each other) is not checked as the object info is always passed from an authentication
 * layer.
 */
@Service
public class MybatisOafService implements OafService {
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public MybatisOafService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorized(AccessLevel accessLevel, Owner owner, String frame) {
        switch (accessLevel) {
            case READ:
                return frame != null && (hasAccess(accessLevel, owner, frame) || (oafMapper.selectAccess(GUEST_ID, frame) != null));
            case CONNECT:
            case WRITE:
                return hasAccess(accessLevel, owner, frame);
            default:
                return false;
        }
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
