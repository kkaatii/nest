package photon.tube.auth;

/**
 * Created by Dun Liu on 12/11/2016.
 */
public enum AccessLevel {
    NONE, READ, CONNECT, WRITE;

    public int getValue() {
        return this.ordinal();
    }
}
