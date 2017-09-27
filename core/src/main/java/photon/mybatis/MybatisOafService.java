package photon.mybatis;

import photon.auth.AccessLevel;
import photon.auth.OafService;
import photon.model.Owner;

import javax.inject.Inject;
import java.util.List;

import static photon.Conventions.*;

/**
 * In the default implementation, the authenticity of the <tt>Owner</tt> object (i.e. whether the id and the nickname
 * truly exist and match with each other) is not checked as the owner info is always provided by the server-side
 * authentication layer.
 */
public class MybatisOafService implements OafService {
    private final OwnerAndFrameMapper oafMapper;

    @Inject
    public MybatisOafService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorized(AccessLevel accessLevel, Owner owner, String frame) {
        if (frame == null) return false;
        switch (accessLevel) {
            case READ:
                return hasAccess(accessLevel, owner, frame) || oafMapper.selectAccess(GUEST_OWNER_ID, frame) != null;
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
        if (frame.split("@")[1].equals(owner.getNickname()))
            return true;
        else {
            Integer a = oafMapper.selectAccess(owner.getId(), frame);
            return ((a == null) ? AccessLevel.NONE.getValue() : a) >= access.getValue();
        }
    }
}
