package wallet.web.dto.Request;

public class CheckoutTransactionRequest {

    private final String id;
    private final String destType;
    private final String dest;
    private final String elapsedTime;
    private final String branchTransaction;
    private final String trunkTransaction;

    public CheckoutTransactionRequest(String id, String destType, String dest, String elapsedTime, String branchTransaction, String trunkTransaction) {
        this.id = id;
        this.destType = destType;
        this.dest = dest;
        this.elapsedTime = elapsedTime;
        this.branchTransaction = branchTransaction;
        this.trunkTransaction = trunkTransaction;
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
    
    public String getBranchTransaction() {
        return branchTransaction;
    }
    
    public String getTrunkTransaction() {
        return trunkTransaction;
    }
    
    public String getElapsedTime() {
        return elapsedTime;
    }
}
