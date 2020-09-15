package wallet.web.dto.Response;

public class ServicesResponse {

    private final long id;
    private final String content;

    public ServicesResponse(long id, String content) {
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
