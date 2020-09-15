package wallet.web.dto.Request;

public class PriceRequest {

    private final String id;

    public PriceRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
