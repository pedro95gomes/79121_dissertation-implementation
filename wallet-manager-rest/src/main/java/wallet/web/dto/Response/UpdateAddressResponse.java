package wallet.web.dto.Response;

public class UpdateAddressResponse {

    private final String id;
    private final String address;

    public UpdateAddressResponse(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }
    
    public String getAddress() {
        return address;
    }
}
