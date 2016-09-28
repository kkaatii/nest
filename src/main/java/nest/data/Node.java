package nest.data;

import java.util.Date;

public class Node {

	private Integer id;

	private String name;
	private NodeType type;
	private Date timestamp;

	private NodeState state = NodeState.ACTIVE;

	private String digest;
	private String content;
	
	public Node(String name, NodeType type, Date timestamp) {
		this.name = name;
		this.type = type;
		this.timestamp = timestamp;
	}

	public Node(String name, NodeType type) {
        this(name, type, new Date());
    }

	public Node() {
		this("", NodeType.NODE, new Date());
	}

    public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public NodeType getType() {
		return type;
	}
	
	public NodeState getState() {
		return state;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public String getDigest() {
		return digest;
	}
	
	public String getContent() {
		return content;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setDigest(String digest) {
		this.digest = digest;
	}
	
	public void setState(NodeState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return this.getType().toString();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return id != null ? id.equals(node.id) : node.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
