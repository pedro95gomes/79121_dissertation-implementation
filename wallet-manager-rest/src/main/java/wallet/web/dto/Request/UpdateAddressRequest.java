package wallet.web.dto.Request;

public class UpdateAddressRequest {

    private final String id;

    public UpdateAddressRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
}
