package photon.tube.service;

import photon.tube.model.Owner;

/**
 * Created by Dun Liu on 11/11/2016.
 */
public interface AuthService {
    Integer GUEST_ID = 1;
    int READ_ACCESS = 1;
    int CONNECT_ACCESS = 2;
    int WRITE_ACCESS = 3;

    boolean authorizedRead(Owner owner, String frame);

    boolean authorizedConnect(Owner owner, String frame);

    boolean authorizedWrite(Owner owner, String frame);
}
