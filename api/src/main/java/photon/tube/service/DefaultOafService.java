package photon.tube.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.model.OwnerAndFrameMapper;

/**
 * Created by Dun Liu on 11/11/2016.
 */
@Service
public class DefaultOafService implements OafService {
    private final OwnerAndFrameMapper oafMapper;

    @Autowired
    public DefaultOafService(OwnerAndFrameMapper oafMapper) {
        this.oafMapper = oafMapper;
    }

    @Override
    public boolean authorizedRead(Integer ownerId, String frame) {
        return (READ_ACCESS & access(ownerId, frame)) != 0;
    }

    @Override
    public boolean authorizedDelete(Integer ownerId, String frame) {
        return (DELETE_ACCESS & access(ownerId, frame)) != 0;
    }

    @Override
    public boolean authorizedCreate(Integer ownerId, String frame) {
        return (CREATE_ACCESS & access(ownerId, frame)) != 0;
    }

    private int access(Integer ownerId, String frame) {
        Integer a = oafMapper.selectAccess(ownerId, frame);
        return (a == null) ? 0 : a;
    }
}
