package photon.model;

public class ViewLog {
    private Integer viewLogId;
    private Integer articleId;
    private Integer userId;
    private int count;

    public ViewLog(Integer articleId, Integer userId) {
        this.articleId = articleId;
        this.userId = userId;
        count = 0;
    }

    public Integer getViewLogId() {
        return viewLogId;
    }

    public void setViewLogId(Integer viewLogId) {
        this.viewLogId = viewLogId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
