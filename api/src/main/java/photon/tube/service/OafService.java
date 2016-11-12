package photon.tube.service;

import photon.tube.model.Owner;

/**
 * Created by Dun Liu on 11/11/2016.
 */
public interface OafService {
    Integer GUEST_ID = 1;
    int READ_ACCESS = 1;
    int DELETE_ACCESS = 2;
    int CREATE_ACCESS = 4;

    boolean authorizedRead(Owner owner, String frame);

    boolean authorizedDelete(Owner owner, String frame);

    boolean authorizedCreate(Owner owner, String frame);
}
