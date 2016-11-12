package photon.tube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Service;
import photon.tube.model.Owner;
import photon.tube.model.OwnerAndFrameMapper;

@Service
public class DefaultOafService implements OafService {
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public DefaultOafService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorizedRead(Owner owner, String frame) {
        return hasAccess(READ_ACCESS, owner, frame) || (oafMapper.selectAccess(GUEST_ID, frame) != null);
    }

    @Override
    public boolean authorizedDelete(Owner owner, String frame) {
        return hasAccess(DELETE_ACCESS, owner, frame);
    }

    @Override
    public boolean authorizedCreate(Owner owner, String frame) {
        return hasAccess(CREATE_ACCESS, owner, frame);
    }

    private boolean hasAccess(int access, Owner owner, String frame) {
        if (frame.split("@")[1].equals(owner.getNickname()))
            return true;
        else {
            Integer a = oafMapper.selectAccess(owner.getId(), frame);
            return (access & ((a == null) ? 0 : a)) != 0;
        }
    }
}
