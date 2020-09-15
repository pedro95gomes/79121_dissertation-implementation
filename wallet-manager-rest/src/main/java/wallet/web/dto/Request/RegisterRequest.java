package wallet.web.dto.Request;

public class RegisterRequest {
	private final String shortName;
	private final String name;
    private final String type;
    private final String password;

    public RegisterRequest(String shortName, String name, String type, String password) {
        this.shortName = shortName;
        this.name = name;
        this.type = type;
        this.password = password;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getShortName() {
    	return shortName;
    }
    
    public String getType() {
    	return type;
    }
    
    public String getPassword() {
    	return password;
    }
}
