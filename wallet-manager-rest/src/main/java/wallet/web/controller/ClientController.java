package wallet.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.iota.jota.dto.response.GetAccountDataResponse;
import org.iota.jota.dto.response.SendTransferResponse;
import org.iota.jota.error.ArgumentException;
import org.iota.jota.model.Input;
import org.iota.jota.model.Transfer;
import org.iota.jota.utils.InputValidator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wallet.web.dto.Request.BalanceRequest;
import wallet.web.dto.Request.BuyRequest;
import wallet.web.dto.Request.CheckoutTransactionRequest;
import wallet.web.dto.Request.CreateRequest;
import wallet.web.dto.Request.LoginRequest;
import wallet.web.dto.Request.PriceRequest;
import wallet.web.dto.Request.TransactionRequest;
import wallet.web.dto.Request.UberPriceRequest;
import wallet.web.dto.Response.BalanceResponse;
import wallet.web.dto.Response.BuyResponse;
import wallet.web.dto.Response.CreateResponse;
import wallet.web.dto.Response.LoginResponse;
import wallet.web.dto.Response.PriceResponse;
import wallet.web.dto.Response.TransactionResponse;
import wallet.web.error.GenericServerException;
import wallet.web.utils.DatabaseUtils;
import wallet.web.utils.IotaUtils;

@RestController
public class ClientController {

    private static final int seedLength = 81;
    private static final int idLength = 32;
    private static final double UBER_COMISSION = 1.04;
    private static final double SCOOTER_COMISSION = 1.01;
    private static final double TAXI_COMISSION = 1.06;
    
    /*===================================================================*/
    /* CLIENT */
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/")
    public String home() {
    	System.out.println("-------------------- Test Request --------------------");
    	return "Hello World";
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/create")
    public CreateResponse create(CreateRequest creater) {
    	System.out.println("-------------------- Create Request --------------------");
    	try {
	    	String seed = IotaUtils.createSeed(seedLength);
	    	String id = IotaUtils.createSeed(idLength);
	    	DatabaseUtils.connect();
	    	DatabaseUtils.createClient(creater.getName(), creater.getEmail(), creater.getPassword(), seed, id);
	        return new CreateResponse(id);
    	} catch(Exception e) {
    		throw new GenericServerException(0, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/login")
    public LoginResponse create(LoginRequest loginr) {
    	System.out.println("-------------------- Login Request --------------------");
    	String id = null;
    	try {
	    	DatabaseUtils.connect();
	    	if(loginr.getType().equals("Client")) {
	    		id = DatabaseUtils.loginClient(loginr.getEmail(), loginr.getPassword());
	    	} else if(loginr.getType().equals("Service")) {
	    		id = DatabaseUtils.loginServiceProvider(loginr.getEmail(), loginr.getPassword());
	    	}
	    	if(id=="") {
	    		throw new GenericServerException(1, "Wrong username or password");
	    	}
	        return new LoginResponse(id);
    	} catch(Exception e) {
    		throw new GenericServerException(2, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/buy")
    public BuyResponse buy(BuyRequest buyr) {
    	System.out.println("-------------------- Buy Request --------------------");
    	try {
	    	String clientseed = DatabaseUtils.getSeed(buyr.getId());
	    	String clientAddress = IotaUtils.getAvailableAddress(clientseed);
	    	String managerSeed = DatabaseUtils.getManagerSeed();
	    	List<Input> inputAddresses = IotaUtils.getAddressesWithValue(managerSeed, Integer.parseInt(buyr.getValue()));
	    	System.out.println("From "+inputAddresses.toString());
	    	List<Transfer> transfers = IotaUtils.buildTransfers(clientseed, inputAddresses, Integer.parseInt(buyr.getValue()), clientAddress);
	    	// create remainer address
	    	String remainerAddress = IotaUtils.getRemainerAddress(managerSeed, inputAddresses);
	    	System.out.println(remainerAddress);
    		if(InputValidator.isTransfersCollectionValid(transfers)){
    			System.out.println("Sending transfers and doing POW");
	    		SendTransferResponse str = IotaUtils.sendTransfer(managerSeed, transfers, inputAddresses, remainerAddress, clientAddress);
	    		System.out.println("Transfer sent!");
	    		return new BuyResponse(buyr.getId(), str.toString()+" com sucesso: "+str.getSuccessfully(), transfers);
    		}
    		return new BuyResponse(buyr.getId(), transfers.toString(), null);
		} catch (Exception e) {
			throw new GenericServerException(3, e.getMessage());
		}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/balance")
    public BalanceResponse balance(BalanceRequest balancer) {
    	System.out.println("-------------------- Balance Request --------------------");
    	try {
	    	String seed = DatabaseUtils.getSeed(balancer.getId());
	    	System.out.println(seed);
	    	List<GetAccountDataResponse> gadr = IotaUtils.getAccountData(seed, 50);
	        return new BalanceResponse(balancer.getId(), gadr.get(1).getAddresses(), gadr.get(1).getTransfers(), gadr.get(1).getInput(), gadr.get(1).getBalance(), gadr.get(0).getBalance());
    	} catch(Exception e) {
    		throw new GenericServerException(4, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/price")
    public PriceResponse price(PriceRequest pricer) {
    	System.out.println("-------------------- Price Request --------------------");
    	try {
	    	System.out.println("Request price for transport of type "+pricer.getId());
	    	Map<String, String> spids = DatabaseUtils.getProvidersWithType(pricer.getId());
	    	Map<String, String> price = DatabaseUtils.getProvidersCost(spids);
	        return new PriceResponse(price);
    	} catch(Exception e) {
    		throw new GenericServerException(5, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/priceuber")
    public PriceResponse priceuber(UberPriceRequest pricer) {
    	System.out.println("-------------------- Price Request --------------------");
    	try {
	    	System.out.println("Request price for transport of type "+pricer.getId());
	    	int distance = DatabaseUtils.calculateDistance(pricer.getSource(), pricer.getDestination());
	    	Map<String, String> spids = DatabaseUtils.getProvidersWithType(pricer.getId());
	    	System.out.println(spids);
	    	Map<String, String> price = DatabaseUtils.getProvidersCost(spids);
	    	Map<String, String> pricefinal = new HashMap<String,String>();
	    	for (Map.Entry<String, String> entry : price.entrySet()) {
	    		if(!entry.getValue().isEmpty()) {
		    		double uberprice = Double.parseDouble(entry.getValue())*UBER_COMISSION*distance;
		    		pricefinal.put(entry.getKey(), String.valueOf(uberprice));
	    		} else {
	    			pricefinal.put(entry.getKey(), null);
	    		}
	    	}
	        return new PriceResponse(pricefinal);
    	} catch(Exception e) {
    		throw new GenericServerException(6, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/transaction")
    public TransactionResponse transaction(TransactionRequest transactionr) {
    	System.out.println("-------------------- Transaction Request --------------------");
    	try {
	    	Random random = new Random();
	    	int bundleId = random.nextInt(999998)+1;
	    	Object[] bundle = null;
	    	String clientseed = DatabaseUtils.getSeed(transactionr.getId());
	    	System.out.println("CLient seed: "+clientseed);
	    	System.out.println(transactionr.getDest());
	    	String spid = DatabaseUtils.getProviderIdFromNameAndType(transactionr.getDestType(), transactionr.getDest());
	    	String spaddressindex = DatabaseUtils.getServiceProviderAddressIndex(spid);
	    	String spaddress = DatabaseUtils.getServiceProviderAddress(spid, spaddressindex);
	    	String value = DatabaseUtils.getProviderCost(spid);
	    	List<Input> inputAddresses = IotaUtils.getAddressesWithValue(clientseed, Long.parseLong(value));
	    	System.out.println("Inputs");
	    	System.out.println(inputAddresses.toString());
	    	for(Input i: inputAddresses) {
	    		System.out.println(i.toString());
	    	}
	    	System.out.println("Transaction to address: "+spaddress);
	    	List<Transfer> transfers = IotaUtils.buildTransfers(clientseed, inputAddresses, Long.parseLong(value), spaddress);
	    	System.out.println(transfers);
	    	// create remainer address
	    	String remainerAddress = IotaUtils.getRemainerAddress(clientseed, inputAddresses);
	    	System.out.println(remainerAddress);
	    	try {
				bundle = IotaUtils.createBundle(clientseed, transfers, inputAddresses, remainerAddress, true, true, null);
				System.out.println(bundle);
			} catch (ArgumentException e) {
				throw new GenericServerException(7, e.getMessage());
			}
	        return new TransactionResponse(transactionr.getId(), String.valueOf(bundleId), value, transactionr.getDestType(), transactionr.getDest(), bundle, "TODO");
    	} catch(Exception e) {
    		throw new GenericServerException(8, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/checkintransaction")
    public TransactionResponse checkintransaction(TransactionRequest transactionr) {
    	System.out.println("-------------------- Checkin Transaction Request --------------------");
    	try {
	    	Random random = new Random();
	    	int bundleId = random.nextInt(999998)+1;
	    	Object[] bundle = null;
	    	String clientseed = DatabaseUtils.getSeed(transactionr.getId());
	    	System.out.println("CLient seed: "+clientseed);
	    	System.out.println(transactionr.getDest());
	    	String spid = transactionr.getDest();
	    	String spaddressindex = DatabaseUtils.getServiceProviderAddressIndex(spid);
	    	String spaddress = DatabaseUtils.getServiceProviderAddress(spid, spaddressindex);
	    	List<Input> inputAddresses = IotaUtils.getAddressesWithValue(clientseed, Long.parseLong("1"));
	    	System.out.println("Inputs");
	    	System.out.println(inputAddresses.toString());
	    	for(Input i: inputAddresses) {
	    		System.out.println(i.toString());
	    	}
	    	System.out.println("Transaction to address: "+spaddress);
	    	List<Transfer> transfers = IotaUtils.buildTransfers(clientseed, inputAddresses, 0, spaddress);
	    	System.out.println(transfers);
	    	try {
				bundle = IotaUtils.createBundle(clientseed, transfers, inputAddresses, null, true, true, null);
				System.out.println(bundle.toString());
			} catch (ArgumentException e) {
				throw new GenericServerException(9, e.getMessage());
			}
	        return new TransactionResponse(transactionr.getId(), String.valueOf(bundleId), "0", transactionr.getDestType(), transactionr.getDest(), bundle, "TODO");
    	} catch(Exception e) {
    		throw new GenericServerException(10, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/checkouttransaction")
    public TransactionResponse checkouttransaction(CheckoutTransactionRequest transactionr) {
    	System.out.println("-------------------- Checkout Transaction Request --------------------");
    	try {
	    	Random random = new Random();
	    	int bundleId = random.nextInt(999998)+1;
	    	Object[] bundle = null;
	    	String clientseed = DatabaseUtils.getSeed(transactionr.getId());
	    	String spid = transactionr.getDest();
	    	String spaddressindex = DatabaseUtils.getServiceProviderAddressIndex(spid);
	    	String spaddress = DatabaseUtils.getServiceProviderAddress(spid, spaddressindex);
	    	
	    	String price = DatabaseUtils.getProviderCost(spid);
	    	double comission = 1.5;
	    	if(transactionr.getDestType().equals("Taxi")) {
	    		comission = TAXI_COMISSION;
	    	} else if(transactionr.getDestType().equals("Scooter")) {
	    		comission = SCOOTER_COMISSION;
	    	}
	    	double finalprice = Double.parseDouble(price)+comission*((Integer.valueOf(transactionr.getElapsedTime())/1000)/60);
	    	
	    	List<Input> inputAddresses = IotaUtils.getAddressesWithValue(clientseed, Double.valueOf(finalprice).longValue());
	    	List<Transfer> transfers = IotaUtils.buildTransfers(clientseed, inputAddresses, Double.valueOf(finalprice).longValue(), spaddress);
	    	System.out.println(finalprice);
	    	System.out.println(transfers);
	    	// create remainer address
	    	String remainerAddress = IotaUtils.getRemainerAddress(clientseed, inputAddresses);
	    	System.out.println(remainerAddress);
	    	try {
				bundle = IotaUtils.createBundle(clientseed, transfers, inputAddresses, remainerAddress, true, true, null);
				System.out.println(bundle.toString());
			} catch (ArgumentException e) {
				throw new GenericServerException(11, e.getMessage());
			}
	        return new TransactionResponse(transactionr.getId(), String.valueOf(bundleId), String.valueOf(finalprice), transactionr.getDestType(), transactionr.getDest(), bundle, "TODO");
    	} catch(Exception e) {
    		throw new GenericServerException(12, e.getMessage());
    	}
    }
}