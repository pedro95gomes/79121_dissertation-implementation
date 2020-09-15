package wallet.web.dto.Response;

public class HistoryResponse {

    private final long id;
    private final String content;

    public HistoryResponse(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
