package photon.tube.service;

/**
 * Created by Dun Liu on 11/11/2016.
 */
public interface OafService {
    int READ_ACCESS=1;
    int DELETE_ACCESS=2;
    int CREATE_ACCESS=4;
    boolean authorizedRead(Integer ownerId, String frame);
    boolean authorizedDelete(Integer ownerId, String frame);
    boolean authorizedCreate(Integer ownerId, String frame);
}
