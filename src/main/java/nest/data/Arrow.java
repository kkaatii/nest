package nest.data;

public class Arrow {

    private Integer id;

    private int origin;
    private int target;
    private ArrowType type;
    private int extension;

    private static final int NO_EXTENSION = -1;

    protected Arrow() { }

    public Arrow(int source, ArrowType type, int target) {
        this(source, type, target, NO_EXTENSION);
    }

    public Arrow(Node source, ArrowType type, Node target) {
        this(source.getId(), type, target.getId(), NO_EXTENSION);
    }

    public Arrow(Node source, ArrowType type, Node target, Extension extension) {
        this(source.getId(), type, target.getId(), extension.getId());
    }

    public Arrow(int source, ArrowType type, int target, int extension) {
        this.origin = source;
        this.type = type;
        this.target = target;
        this.extension = extension;
    }

    public Arrow reverse() {
        return new Arrow(target, type.reverse(), origin, extension);
    }

    public Integer getId() {
        return id;
    }

    public int getOrigin() {
        return origin;
    }

    public int getTarget() {
        return target;
    }

    public ArrowType getType() {
        return type;
    }

    public int getExtension() {
        return extension;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExtension(int extId) {
        this.extension = extId;
    }

    public boolean hasExtension() {
        return ! (extension == NO_EXTENSION);
    }

    public boolean similarTo(Arrow a) {
        return (target == a.getTarget()) && (type.equals(a.getType()));
    }

    @Override
    public String toString() {
        return String.format("%d %s %d", origin, type.toString(), target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arrow arrow = (Arrow) o;
        return (id != null) && (arrow.id != null) ? id.equals(arrow.id) : origin == arrow.origin && target == arrow.target && type == arrow.type;
    }

    @Override
    public int hashCode() {
        int result = origin;
        result = 31 * result + target;
        result = 31 * result + type.hashCode();
        return result;
    }

}
