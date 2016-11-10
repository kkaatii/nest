package photon.tube.model;

/**
 * Created by Dun Liu on 11/9/2016.
 */
public class Owner {
    private String authId;
    private Integer id;
    private String nickname;

    public Owner(String authId, String nickname) {
        this.authId = authId;
        this.nickname = nickname;
    }

    public Owner(String authId, Integer id) {
        this.authId = authId;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
}
