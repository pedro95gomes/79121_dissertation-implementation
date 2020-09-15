package wallet.web.dto.Response;

import java.util.List;

import org.iota.jota.model.Bundle;
import org.iota.jota.model.Input;

public class BalanceResponse {

    private final String id;
    private final List<String> addresses;
    private final Bundle[] transfers;
    private final List<Input> inputs;
    private final long balance;
    private final long contabilisticbalance;

    public BalanceResponse(String id, List<String> addresses, Bundle[] transfers, List<Input> inputs, long balance, long contabilisticbalance) {
        this.id = id;
        this.addresses = addresses;
        this.transfers = transfers;
        this.inputs = inputs;
        this.balance = balance;
        this.contabilisticbalance = contabilisticbalance;
    }
    
    public BalanceResponse(String id, List<String> addresses, long balance, long contabilisticbalance) {
        this.id = id;
        this.addresses = addresses;
        this.transfers = null;
        this.inputs = null;
        this.balance = balance;
        this.contabilisticbalance = contabilisticbalance;
    }

    public String getId() {
        return id;
    }

    public List<String> getAddresses() {
        return addresses;
    }
    
    public Bundle[] getTransfers() {
    	return transfers;
    }
    
    public List<Input> getInputs(){
    	return inputs;
    }
    
    public long getBalance() {
        return balance;
    }
    
    public long getContabilisticBalance() {
    	return contabilisticbalance;
    }
}
