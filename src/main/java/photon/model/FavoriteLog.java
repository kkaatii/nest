package photon.model;

public class FavoriteLog {
    private Integer uid;
    private Integer cid;

    public FavoriteLog(Integer uid, Integer cid) {
        this.uid = uid;
        this.cid = cid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }
}
