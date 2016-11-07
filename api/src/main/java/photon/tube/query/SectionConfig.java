package photon.tube.query;

public class SectionConfig {
    public static final String DO_NOT_SECTION = "none";
    public static final String BY_RANK = "rank";
    public static final String BY_DEPTH = "depth";

    public String sectionMode;
    public int leftLimit;
    public int rightLimit;
    public boolean leftInclusive;
    public boolean rightInclusive;

    public SectionConfig(String sectionMode, int leftLimit, int rightLimit, boolean leftInclusive, boolean rightInclusive) {
        this.sectionMode = sectionMode;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.leftInclusive = leftInclusive;
        this.rightInclusive = rightInclusive;
    }
    
    public Section applyOn(GraphContainer container) {
        switch (sectionMode) {
            case DO_NOT_SECTION:
                return container.export();
            case BY_RANK:
                return container.sectionByRank(leftLimit, rightLimit, leftInclusive, rightInclusive).export();
            case BY_DEPTH:
                return container.sectionByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive).export();
            default:
                throw new RuntimeException("Unsupported section mode \"" + sectionMode + "\"!");
        }
    }
}