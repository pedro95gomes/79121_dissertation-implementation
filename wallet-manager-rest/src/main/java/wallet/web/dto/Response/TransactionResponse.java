package wallet.web.dto.Response;

public class TransactionResponse {

    private final String id;
    private final String bundleId;
    private final Object[] bundle;
    private final String cost;
    private final String transportType;
    private final String transportOption;
    private final String expires;
    
    public TransactionResponse(String id, String bundleId, String cost, String transportType, String transportOption, Object[] bundle, String expires) {
        this.id = id;
        this.bundleId = bundleId;
        this.cost = cost;
        this.transportType = transportType;
        this.transportOption = transportOption;
        this.bundle = bundle;
        this.expires = expires;
    }

    public String getId() {
        return id;
    }
    
    public String getBundleId() {
        return bundleId;
    }
    
    public String getCost() {
        return cost;
    }
    
    public String getTransportType() {
        return transportType;
    }
    
    public String getTransportOption() {
        return transportOption;
    }

    public Object[] getBundle() {
        return bundle;
    }
    
    public String getExpiringTime() {
        return expires;
    }
}
