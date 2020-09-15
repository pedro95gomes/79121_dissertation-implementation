package wallet.web.dto.Response;

public class CreateResponse {

    private final String id;

    public CreateResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
