package wallet.web.dto.Request;

public class ServicesRequest {

    private final String id;

    public ServicesRequest(String id, String content) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
