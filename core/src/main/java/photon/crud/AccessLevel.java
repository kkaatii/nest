package photon.crud;

public enum AccessLevel {
    NONE, READ, CONNECT, WRITE;

    public int getValue() {
        return this.ordinal();
    }
}
