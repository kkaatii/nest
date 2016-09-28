package nest.data;

import java.util.Date;

public class Point {
		
	private Integer id;
	
	private String name;
	private Date timestamp;
	private NodeType type;
	private NodeState state ;
	private String digest;
	
	Point() {}

    public Point(int id) {
        this.id = id;
    }

    public Point(Node node) {
        id = node.getId();
        name = node.getName();
        timestamp = node.getTimestamp();
        type = node.getType();
        state = node.getState();
        digest = node.getDigest();
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
	
	public Date getTimestamp() {
		return timestamp;
	}

	public NodeState getState() {
		return state;
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
