package photon.tube.model;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public enum ArrowType {
    PARENT_OF, CHILD_OF,
    TAGGED_BY, TAGGING,
    KEYWORD_OF, HAVING_KEYWORD,
    TYPE, DEPENDED_BY,

    UNSPECIFIED, PAIRED, ;

    public ArrowType reverse() {
        int ord = ordinal();
        switch (this) {
            case UNSPECIFIED:
                return UNSPECIFIED;
            case PAIRED:
                return PAIRED;
            default:
                return (ord % 2 == 0) ?
                        ArrowType.values()[ord + 1] :
                        ArrowType.values()[ord - 1];
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case PAIRED:
                return "~";
            default:
                return super.toString().toLowerCase();
        }
    }
}
