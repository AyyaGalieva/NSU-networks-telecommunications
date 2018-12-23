package RequestBuilders;

public abstract class RequestBuilder {
    protected String query;

    public RequestBuilder(String query) {
        this.query = query;
    }
    public abstract void build();
}
