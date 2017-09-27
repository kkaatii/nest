package photon.model;

import java.util.Objects;

public class Arrow {

    private Integer id;

    protected Integer origin;
    protected Integer target;
    protected boolean active;
    protected ArrowType type;

    protected Arrow() {
    }

    public Arrow(Node origin, ArrowType type, Node target) {
        this(origin.getId(), type, target.getId());
    }

    public Arrow(Integer origin, ArrowType type, Integer target) {
        this.origin = origin;
        this.type = type;
        this.target = target;
        this.active = true;
    }

    public Arrow reverse() {
        return new Arrow(target, type.reverse(), origin);
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrigin() {
        return origin;
    }

    public Integer getTarget() {
        return target;
    }

    public boolean isActive() {
        return active;
    }

    public ArrowType getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean similarTo(Arrow a) {
        return (Objects.equals(target, a.getTarget())) && (type.equals(a.getType()));
    }

    @Override
    public String toString() {
        return String.format("[%d]> %s [%d]", origin, type.toString(), target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arrow arrow = (Arrow) o;
        return (id != null) && (arrow.id != null) ? id.equals(arrow.id) : Objects.equals(origin, arrow.origin) && Objects.equals(target, arrow.target) && type == arrow.type;
    }

    @Override
    public int hashCode() {
        int result = origin;
        result = 31 * result + target;
        result = 31 * result + type.hashCode();
        return result;
    }

}
