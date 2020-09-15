package wallet.web.dto.Request;

public class BuyRequest {

    private final String id;
    private final String paymentInfo;
    private final String value;

    public BuyRequest(String id, String paymentInfo, String value) {
        this.id = id;
        this.paymentInfo = paymentInfo;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getpaymentInfo() {
        return paymentInfo;
    }
    
    public String getValue() {
        return value;
    }
}
