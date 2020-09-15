package wallet.web.dto.Request;

public class HistoryRequest {

    private final String id;

    public HistoryRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
