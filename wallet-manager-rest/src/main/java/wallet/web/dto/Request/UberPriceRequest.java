package wallet.web.dto.Request;

public class UberPriceRequest extends PriceRequest{

    private final String source;
    private final String destination;

    public UberPriceRequest(String id, String source, String destination) {
        super(id);
        this.source = source;
        this.destination = destination;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getDestination() {
        return destination;
    }
}
