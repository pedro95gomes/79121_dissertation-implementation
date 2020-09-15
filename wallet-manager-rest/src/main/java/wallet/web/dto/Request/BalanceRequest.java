package wallet.web.dto.Request;

public class BalanceRequest {

    private final String id;

    public BalanceRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
