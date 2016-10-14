package photon.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Catalog {
    private Integer articleId; // Serve as the Primary Key (ArticleId) for DynamoDB
    private String country; // Parsed by Google Maps API from article destination

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date created;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public String getCountry() {
        return country;
    }

    public Date getCreated() {
        return created;
    }

    public Date getDate() {
        return date;
    }
}
