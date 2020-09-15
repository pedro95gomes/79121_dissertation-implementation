package wallet.web.dto.Response;

import java.util.Map;

public class PriceResponse {

    private final Map<String, String> price;

    public PriceResponse(Map<String, String> price) {
        this.price = price;
    }

    public Map<String, String> getPrice() {
        return price;
    }
}
