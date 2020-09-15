package wallet.web.dto.Response;

public class SetPriceResponse {

    private final String id;

    public SetPriceResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
