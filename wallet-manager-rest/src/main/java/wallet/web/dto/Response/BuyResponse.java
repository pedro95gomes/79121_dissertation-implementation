package wallet.web.dto.Response;

import java.util.List;

import org.iota.jota.model.Transfer;

public class BuyResponse {

    private final String id;
    private final String content;
    private final List<Transfer> transfers;

    public BuyResponse(String id, String content, List<Transfer> transfers) {
        this.id = id;
        this.content = content;
        this.transfers = transfers;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    
    public List<Transfer> getTransfers(){
    	return transfers;
    }
}
