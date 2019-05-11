package photon.model;

import java.time.LocalDateTime;

import static photon.util.Utils.html2text;
import static photon.util.Utils.kvPair2Json;

public class Node {
    private static final int DIGEST_LENGTH = 200;

    private Integer id;
    private String name;
    private Integer ownerId;
    private String frame;
    private boolean active = true;
    private NodeType type;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime updated = created;
    private String digest;
    private String content;

    protected Node() {
    }

    public Node(String name) {
        this(NodeType.ARTICLE, name, "");
    }

    public Node(NodeType type, String name) {
        this(type, name, null);
    }

    public Node(NodeType type, String name, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
        doDigest();
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

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
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
        String strippedText = html2text(content);
        digest = (strippedText.length() > DIGEST_LENGTH) ? strippedText.substring(0, DIGEST_LENGTH - 3) + "..." : strippedText;
    }

    private void stampUpdate() {
        this.updated = LocalDateTime.now();
    }

    public String toJson() {
        return kvPair2Json(
                "id", id,
                "name", name,
                "ownerId", ownerId,
                "frame", frame,
                "type", type,
                "active", active,
                // "created", created.toString(),
                // "updated", updated.toString(),
                "content", content,
                "digest", digest
        );
    }

    @Override
    public String toString() {
        return toJson();
    }

}
