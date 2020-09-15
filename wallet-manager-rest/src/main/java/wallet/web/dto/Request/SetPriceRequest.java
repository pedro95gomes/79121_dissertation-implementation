package wallet.web.dto.Request;

public class SetPriceRequest {

    private final String id;
    private final String price;

    public SetPriceRequest(String id, String price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }
    
    public String getPrice() {
        return price;
    }
}
