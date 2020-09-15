package wallet.web.utils;

import java.util.Random;

import org.iota.jota.IotaAPI;
import org.iota.jota.builder.AddressRequest;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

import org.iota.jota.dto.response.FindTransactionResponse;
import org.iota.jota.dto.response.GetAccountDataResponse;
import org.iota.jota.dto.response.GetBalancesAndFormatResponse;
import org.iota.jota.dto.response.GetBalancesResponse;
import org.iota.jota.dto.response.GetNewAddressResponse;
import org.iota.jota.dto.response.GetTransactionsToApproveResponse;
import org.iota.jota.dto.response.GetTransferResponse;
import org.iota.jota.dto.response.SendTransferResponse;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Input;
import org.iota.jota.model.Transaction;
import org.iota.jota.model.Transfer;
import org.iota.jota.pow.pearldiver.PearlDiverLocalPoW;
import org.iota.jota.utils.Checksum;
import org.iota.jota.utils.Constants;
import org.iota.jota.utils.InputValidator;
import org.iota.jota.utils.IotaAPIUtils;
import org.iota.jota.utils.StopWatch;

public class IotaUtils {
	
    private static final int securityLevel = 1;
    private static final int depth = 3;
    private static final int minWeightMagnitude = 9;
	private static IotaAPI api;
	
    public static IotaAPI getApiInstanceLocalPow(String ip) {
        if (api == null) {
            api = new IotaAPI.Builder()
        	        .protocol("http")
        	        .host(ip)
        	        .port(14265)
        	        .localPoW(new PearlDiverLocalPoW())
        	        .build();
        }
        return api;
    }
    
    public static IotaAPI getApiInstance(String ip) {
        if (api == null) {
            api = new IotaAPI.Builder()
        	        .protocol("http")
        	        .host(ip)
        	        .port(14265)
        	        .build();
        }
        return api;
    }

	public static String createSeed(int length) {
		final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ9"; 
		final Random random = new SecureRandom();
		final char[] buf;
		buf = new char[length];
		
		for (int idx = 0; idx < buf.length; ++idx) {
			buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
		}
		return new String(buf);
	}
	
	public static String getAvailableAddress(String seed) {
		AddressRequest addressRequest = new AddressRequest.Builder(seed, securityLevel).amount(0).checksum(true).build();
		GetNewAddressResponse gnar = null;
		try {
			gnar = api.generateNewAddresses(addressRequest);
			//attachAddressToTangle(seed, gnar.first());
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		return gnar.first();
	}
	
	public static String[] getBalances(List<String> addresses) {
		GetBalancesResponse gbr = null;
		String[] balances = null;
		try {
			gbr = api.getBalances(100, addresses);
			balances = gbr.getBalances();
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		return balances;
	}	
	
	public static List<GetAccountDataResponse> getAccountData(String seed, int numAddresses) {
		try {
			ArrayList<GetAccountDataResponse> gadrs = new ArrayList<GetAccountDataResponse>();
			GetAccountDataResponse gadr = api.getAccountData(seed, securityLevel, 0, false, numAddresses, true, 0, numAddresses, true, 0);
			GetAccountDataResponse gadrtips = getAccountDataTips(seed, securityLevel, 0, false, numAddresses, true, 0, numAddresses, true, 0);
			gadrs.add(gadr);
			gadrs.add(gadrtips);
			return gadrs;
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SendTransferResponse sendTransfer(String managerSeed, List<Transfer> transfers, List<Input> inputs, String remainerAddress, String clientAddress) throws ArgumentException {
		return api.sendTransfer(managerSeed, securityLevel, depth, minWeightMagnitude, transfers, inputs, remainerAddress, false, false, null);		
	}
	
	public static Object[] createBundle(String seed, List<Transfer> transfers, List<Input> inputs, String remainderAddress, 
                                             boolean validateInputs, boolean validateInputAddresses, List<Transaction> tips) throws ArgumentException {
        List<String> trytes = api.prepareTransfers(seed, securityLevel, transfers, remainderAddress, null, tips, validateInputs);
        
        if (validateInputAddresses) {
            api.validateTransfersAddresses(seed, securityLevel, trytes);
        }
        String reference = tips != null && tips.size() > 0 ? tips.get(0).getHash(): null;
        GetTransactionsToApproveResponse txs = api.getTransactionsToApprove(depth, reference);
        
		return new Object[]{trytes, txs};
	}

	public static List<Transfer> buildTransfers(String clientseed, List<Input> inputAddresses, long value, String dest) {
		List<Transfer> transfers = new ArrayList<Transfer>();
		if(value == 0) {
			transfers.add(new Transfer(dest, 0, "TESTMESSAGE", "TESTTAG"));
			return transfers;
		}
		/*for(Input input: inputAddresses) {
			transfers.add(new Transfer(input.getAddress(), -value, "TESTMESSAGE", "TESTTAG"));
			transfers.add(new Transfer(input.getAddress(), input.getBalance()-value, "TESTMESSAGE", "TESTTAG"));
		}*/
		transfers.add(new Transfer(dest, value, "TESTMESSAGE", "TESTTAG"));
		
		return transfers;
	}

	public static List<Input> getAddressesWithValue(String clientseed, long value){
		GetBalancesAndFormatResponse inputs;
		try {
			inputs = api.getInputs(clientseed, securityLevel, 0, 0, value);
			return inputs.getInputs();
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean isAddressSpent(String newAddress, boolean checksum) {
        final String address = checksum ? newAddress : Checksum.addChecksum(newAddress);
        final FindTransactionResponse response = api.findTransactionsByAddresses(address);

        if (response.getHashes().length == 0) {
            Boolean state = api.checkWereAddressSpentFrom(address);
            return state;
        }

        return true;
    }
	
	private static List<String> getFiveUnusedAddresses(String seed, int securityLevel, int index, boolean checksum) {
        int mIndex = index;
        int fiver = 0;
        List<String> newAddresses = new ArrayList<String>();
        do {
            final String newAddress = IotaAPIUtils.newAddress(seed, securityLevel, mIndex, checksum, api.getCurl());
            if (!isAddressSpent(newAddress, checksum)) {
                newAddresses.add(newAddress);
                fiver++;
            }
            if(fiver == 5) {
            	return newAddresses;
            }
            mIndex++;
        } while (true);
    }
	
	public static String getRemainerAddress(String seed, List<Input> inputAddresses) {
		List<String> fiveUnusedAddresses = getFiveUnusedAddresses(seed, securityLevel, 0, true);
		for(Input inputaddress: inputAddresses) {
			if(fiveUnusedAddresses.contains(inputaddress.getAddress())){
				fiveUnusedAddresses.remove(inputaddress.getAddress());
			}
		}
		attachAddressToTangle(seed, fiveUnusedAddresses.get(0));
		return fiveUnusedAddresses.get(0);
		
	}
	
	public static GetAccountDataResponse getAccountDataTips(String seed, int security, int index, boolean checksum, int total, 
            boolean returnAll, int start, int end, boolean inclusionStates, long threshold) throws ArgumentException {

        if (!InputValidator.isValidSecurityLevel(security)) {
            throw new ArgumentException(Constants.INVALID_SECURITY_LEVEL_INPUT_ERROR);
        }
        
        if (start < 0 || start > end || end > (start + 1000)) {
            throw new ArgumentException(Constants.INVALID_INPUT_ERROR);
        }

        StopWatch stopWatch = new StopWatch();

        AddressRequest addressRequest = new AddressRequest.Builder(seed, security)
                .index(index)
                .checksum(checksum)
                .amount(total)
                .addSpendAddresses(returnAll)
                .build();

        GetNewAddressResponse gna = api.generateNewAddresses(addressRequest);
        GetTransferResponse gtr = api.getTransfers(seed, security, start, end, inclusionStates);
        GetTransactionsToApproveResponse tips = api.getTransactionsToApprove(3);
        GetBalancesAndFormatResponse gbr = api.getInputs(seed, security, start, end, threshold, tips.getBranchTransaction(), tips.getTrunkTransaction());

        return GetAccountDataResponse.create(gna.getAddresses(), gtr.getTransfers(), gbr.getInputs(), gbr.getTotalBalance(), stopWatch.getElapsedTimeMili());
    }
	
	public static String generateNewAddress(String seed, String index) {
		AddressRequest addressRequest = new AddressRequest.Builder(seed, securityLevel).checksum(true).amount(1).index(Integer.parseInt(index)+1).build();
		GetNewAddressResponse gnar = null;
		try {
			gnar = api.generateNewAddresses(addressRequest);
			attachAddressToTangle(seed, gnar.first());
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		return gnar.first();
	}

	public static List<Transaction> getTipTransactionsFromAddresses(String branchTransaction,
			String trunkTransaction) {
		String[] hashes = {branchTransaction, trunkTransaction};
		List<Transaction> tips = api.findTransactionsObjectsByHashes(hashes);
		return tips;
	}

	public static void attachAddressToTangle(String seed, String address) {
		Transfer zeroValueTransaction = new Transfer(address, 0, "ATTACHADDRESS", "ATTACHADDRESS");
		ArrayList<Transfer> transfers = new ArrayList<Transfer>();
		transfers.add(zeroValueTransaction);
		try { 
		    SendTransferResponse response = api.sendTransfer(seed, securityLevel, depth, minWeightMagnitude, transfers, null, null, false, false, null);
		    System.out.println(response.getTransactions());
		} catch (ArgumentException e) { 
		    // Handle error
		    e.printStackTrace(); 
		 }
	}
}
