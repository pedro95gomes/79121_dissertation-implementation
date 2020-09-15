package wallet.web.dto.Request;

public class TransactionRequest {

    private final String id;
    private final String destType;
    private final String dest;

    public TransactionRequest(String id, String destType, String dest) {
        this.id = id;
        this.destType = destType;
        this.dest = dest;
    }

    public String getId() {
        return id;
    }
    
    public String getDestType() {
        return destType;
    }
    
    public String getDest() {
        return dest;
    }
    
}
