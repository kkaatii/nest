package photon.tube.model;

/**
 * Created by Dun Liu on 11/9/2016.
 */
public class Owner {
    private Integer id;
    private String nickname;

    protected Owner() {

    }

    public Owner(Integer id, String nickname) {
        this.id = id;
        this.nickname = nickname;
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

}
