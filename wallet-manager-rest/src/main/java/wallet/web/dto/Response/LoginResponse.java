package wallet.web.dto.Response;

public class LoginResponse {
	private final String id;

    public LoginResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
