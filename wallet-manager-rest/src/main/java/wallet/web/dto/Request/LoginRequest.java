package wallet.web.dto.Request;

public class LoginRequest {
    private final String email;
    private final String type;
    private final String password;

    public LoginRequest(String email, String type, String password) {
        this.email = email;
        this.password = password;
        this.type = type;
    }
    
    public String getEmail() {
    	return email;
    }
    
    public String getPassword() {
    	return password;
    }
    
    public String getType() {
    	return type;
    }
}
