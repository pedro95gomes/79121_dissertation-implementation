package wallet.web.dto.Response;

public class RegisterResponse{
	
	private final String id;

    public RegisterResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
