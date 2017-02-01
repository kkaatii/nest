package photon.tube.model;

public enum ArrowType {
    /*------- Be careful when editing this part as the two arrow types within ------*/
    /*------- each line is deemed automatically as the reverse to one another ------*/
    //
    // General arrow types
    //
    PARENT_OF       (Group.GENERAL),    CHILD_OF        (Group.GENERAL),

    //
    // Search-related arrow types
    //
    KEYWORD_OF      (Group.SEARCH),     WITH_KEYWORD    (Group.SEARCH),

    //
    // Directory-related arrow types
    //
    FOLDER_OF       (Group.DIRECTORY),  IN_FOLDER       (Group.DIRECTORY),
    TAGGED_BY       (Group.DIRECTORY),  TAGGING         (Group.DIRECTORY),

    //
    // Schedule-related arrow types
    //
    DEPENDENT_ON    (Group.SCHEDULE),   PREREQUISITE_OF (Group.SCHEDULE),

    /*------- End of automatic reverse pairing                                ------*/

    ANY             (Group.GENERAL),    PAIRING_WITH    (Group.GENERAL),
    ;

    public static final String REVERSE_SIGN = "^";
    private final Group group;

    ArrowType(Group group) {
        this.group = group;
    }

    public ArrowType reverse() {
        int ord = ordinal();
        switch (this) {
            case ANY:
                return ANY;
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
        return this.equals(ANY) || at.equals(ANY) || this.equals(at);
    }

    public enum Group {
        GENERAL, DIRECTORY, SCHEDULE, SEARCH,
    }
}
