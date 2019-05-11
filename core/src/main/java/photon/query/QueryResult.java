package photon.query;

public class QueryResult {
    private long id;
    private String content;

    public QueryResult(String content) {
        this.content = content;
    }

    public String asJson() {
        return content;
    }
}
