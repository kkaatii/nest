package photon.tube.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Point {

    private Integer id;
    private String name;
    private Integer ownerId;
    private String frame;
    private boolean active;
    private NodeType type;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date created;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updated;
    private String digest;

    protected Point() {
    }

    public Point(Integer id) {
        this.id = id;
    }

    public Point(Node node) {
        id = node.getId();
        name = node.getName();
        ownerId = node.getOwnerId();
        frame = node.getFrame();
        active = node.isActive();
        type = node.getType();
        created = node.getCreated();
        updated = node.getUpdated();
        digest = node.getDigest();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public String getFrame() {
        return frame;
    }

    public NodeType getType() {
        return type;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public boolean isActive() {
        return active;
    }

    public String getDigest() {
        return digest;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return id != null ? id.equals(point.id) : point.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
