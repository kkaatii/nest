package photon.tube.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Node {
    private static final int DIGEST_LENGTH = 32;

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
    private String content;

    public Node() {
        this(NodeType.ARTICLE, "Unnamed article", "");
    }

    public Node(String name) {
        this(NodeType.ARTICLE, name, "");
    }

    public Node(NodeType type, String name) {
        this(type, name, null);
    }

    public Node(NodeType type, String name, String content) {
        this.name = name;
        this.active = true;
        this.type = type;
        this.content = content;
        doDigest();
        this.created = new Date();
        this.updated = this.created;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        stampUpdate();
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        doDigest();
        stampUpdate();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getDigest() {
        return digest;
    }

    public void doDigest() {
        if (content == null) {
            digest = null;
            return;
        }
        digest = (content.length() > DIGEST_LENGTH) ? content.substring(0, DIGEST_LENGTH - 3) + "..." : content;
    }

    private void stampUpdate() {
        this.updated = new Date();
    }
}
