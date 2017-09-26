package photon.tube.model;

public enum ArrowType {
    /*------- Be careful when editing this part as the two arrow types within ------*/
    /*------- each line is deemed automatically as the reverse to one another ------*/
    //
    // General arrow types
    //
    PARENT_OF(Group.GENERAL), CHILD_OF(Group.GENERAL),

    //
    // Search-related arrow types
    //
    KEYWORD_OF(Group.SEARCH), WITH_KEYWORD(Group.SEARCH),

    //
    // Directory-related arrow types
    //
    FOLDER_OF(Group.DIRECTORY), IN_FOLDER(Group.DIRECTORY),
    TAGGED_BY(Group.DIRECTORY), TAGGING(Group.DIRECTORY),

    //
    // Schedule-related arrow types
    //
    DEPENDENT_ON(Group.SCHEDULE), PREREQUISITE_OF(Group.SCHEDULE),

    /*------- End of automatic reverse pairing                                ------*/

    WILDCARD(Group.GENERAL), PAIRING_WITH(Group.GENERAL),;

    public static final char REVERSE_SIGN = '^';
    private final Group group;

    ArrowType(Group group) {
        this.group = group;
    }

    public ArrowType reverse() {
        int ord = ordinal();
        switch (this) {
            case WILDCARD:
                return WILDCARD;
            case PAIRING_WITH:
                return PAIRING_WITH;
            default:
                return (ord % 2 == 0) ?
                        ArrowType.values()[ord + 1] :
                        ArrowType.values()[ord - 1];
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public Group group() {
        return group;
    }

    public boolean isType(ArrowType at) {
        return this.equals(WILDCARD) || at.equals(WILDCARD) || this.equals(at);
    }

    public static ArrowType extendedValueOf(String atString) {
        if (atString == null)
            return WILDCARD;
        else
            return atString.startsWith(Character.toString(REVERSE_SIGN))
                    ? valueOf(atString.substring(1).toUpperCase()).reverse()
                    : valueOf(atString.toUpperCase());
    }

    public enum Group {
        GENERAL, DIRECTORY, SCHEDULE, SEARCH,
    }
}
