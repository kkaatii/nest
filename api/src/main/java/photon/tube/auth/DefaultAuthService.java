package photon.tube.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.model.Owner;
import photon.tube.model.mybatis.OwnerAndFrameMapper;

@Service
public class DefaultAuthService implements AuthService {
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public DefaultAuthService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorizedRead(Owner owner, String frame) {
        return hasAccess(READ_ACCESS, owner, frame) || (oafMapper.selectAccess(GUEST_ID, frame) != null);
    }

    @Override
    public boolean authorizedConnect(Owner owner, String frame) {
        return hasAccess(CONNECT_ACCESS, owner, frame);
    }

    @Override
    public boolean authorizedWrite(Owner owner, String frame) {
        return hasAccess(WRITE_ACCESS, owner, frame);
    }

    private boolean hasAccess(int access, Owner owner, String frame) {
        if (frame.split("@")[1].equals(owner.getNickname()))
            return true;
        else {
            Integer a = oafMapper.selectAccess(owner.getId(), frame);
            return ((a == null) ? 0 : a) >= access;
        }
    }
}
