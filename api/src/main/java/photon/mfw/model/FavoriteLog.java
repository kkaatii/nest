package photon.mfw.model;

public class FavoriteLog {
    private Integer articleId;
    private Integer userId;

    public FavoriteLog(Integer articleId, Integer userId) {
        this.userId = userId;
        this.articleId = articleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }
}
