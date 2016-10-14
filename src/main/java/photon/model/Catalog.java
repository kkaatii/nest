package photon.model;

import java.util.Date;

public class Catalog {
    private Integer cid; // Serve as the Primary Key (ArticleId) for DynamoDB
    private String country; // Parsed by Google Maps API from article destination
    private Date created;
    private Date date;

    public void setCid(Integer cid) {
        this.cid = cid;
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

    public Integer getCid() {
        return cid;
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
