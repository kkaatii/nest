package photon.tube.model;

import java.util.Date;

public class Point {

    private Integer id;

    private String name;
    private String frame;
    private boolean active;
    private NodeType type;
    private Date created;
    private Date updated;
    private String digest;

    Point() {
    }

    public Point(int id) {
        this.id = id;
    }

    public Point(Node node) {
        id = node.getId();
        name = node.getName();
        created = node.getCreated();
        updated = node.getUpdated();
        type = node.getType();
        active = node.isActive();
        digest = node.getDigest();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setId(int id) {
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
