package photon.auth;

public enum AccessLevel {
    NONE, READ, CONNECT, WRITE;

    public int getValue() {
        return this.ordinal();
    }
}
