package nest.data;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public enum ArrowType {
    parent_of, child_of,
    tagged_by, tagging,
    keyword_of, containing_keyword,
    depending_on, depended_by,

    unspecified, paired, ;

    public ArrowType reverse() {
        int ord = ordinal();
        switch (this) {
            case unspecified:
                return unspecified;
            case paired:
                return paired;
            default:
                return (ord % 2 == 0) ?
                        ArrowType.values()[ord + 1] :
                        ArrowType.values()[ord - 1];
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case paired:
                return "~";
            default:
                return super.toString().toLowerCase();
        }
    }
}
