package wallet.web.error;

@SuppressWarnings("serial")
public class GenericServerException extends RuntimeException {

    private long id;
    private String message;

    public GenericServerException(long id, String message) {
        this.id = id;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
