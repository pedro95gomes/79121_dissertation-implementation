package wallet.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.iota.jota.dto.response.GetAccountDataResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wallet.web.dto.Request.BalanceRequest;
import wallet.web.dto.Request.RegisterRequest;
import wallet.web.dto.Request.SetPriceRequest;
import wallet.web.dto.Request.UpdateAddressRequest;
import wallet.web.dto.Response.BalanceResponse;
import wallet.web.dto.Response.RegisterResponse;
import wallet.web.dto.Response.SetPriceResponse;
import wallet.web.dto.Response.UpdateAddressResponse;
import wallet.web.error.GenericServerException;
import wallet.web.utils.DatabaseUtils;
import wallet.web.utils.IotaUtils;

@RestController
public class ServiceProviderController {

    private static final int seedLength = 81;
    private static final int idLength = 32;
    
    /*===================================================================*/
    /* SERVICE PROVIDER */
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/service/register")
    public RegisterResponse register(RegisterRequest registerr) {
    	System.out.println("-------------------- Service Register Request --------------------");
    	try {
	    	String seed = IotaUtils.createSeed(seedLength);
	    	String id = IotaUtils.createSeed(idLength);
	    	String firstAddress = IotaUtils.getAvailableAddress(seed);
	    	DatabaseUtils.connect();
	    	DatabaseUtils.createServiceProvider(registerr.getShortName(), registerr.getName(), registerr.getType() , registerr.getPassword(), seed, firstAddress, id);
	        DatabaseUtils.setServiceProviderPrice(id, "1");
	    	return new RegisterResponse(id);
    	} catch(Exception e) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		e.printStackTrace(pw);
    		throw new GenericServerException(13, sw.toString());
    	}
    }
  
    @CrossOrigin(origins = "*")
    @RequestMapping("/service/balance")
    public BalanceResponse providerbalance(BalanceRequest balancer) {
    	System.out.println("-------------------- Balance Request --------------------");
    	try {
	    	String seed = DatabaseUtils.getServiceProviderSeed(balancer.getId());
	    	System.out.println(seed);
	    	List<GetAccountDataResponse> gadr = IotaUtils.getAccountData(seed, 100);
	        return new BalanceResponse(balancer.getId(), gadr.get(1).getAddresses(), gadr.get(1).getTransfers(), gadr.get(1).getInput(), gadr.get(1).getBalance(), gadr.get(0).getBalance());
    	} catch(Exception e) {
    		throw new GenericServerException(14, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/service/updateprice")
    public SetPriceResponse updateprice(SetPriceRequest pricer) {
    	System.out.println("-------------------- Update Price Request --------------------");
    	try {
	    	DatabaseUtils.updateServiceProviderPrice(pricer.getId(), pricer.getPrice());
	        return new SetPriceResponse(pricer.getId());
    	} catch(Exception e) {
    		throw new GenericServerException(15, e.getMessage());
    	}
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping("/service/updateaddress")
    public UpdateAddressResponse updateaddress(UpdateAddressRequest upaddr) {
    	System.out.println("-------------------- Update Address Request --------------------");
    	try {
	    	String seed = DatabaseUtils.getServiceProviderSeed(upaddr.getId());
	    	System.out.println(seed);
	    	String index = DatabaseUtils.getServiceProviderAddressIndex(upaddr.getId());
	    	String address = IotaUtils.generateNewAddress(seed, index);
	    	DatabaseUtils.addServiceProviderAddress(upaddr.getId(), address, Integer.parseInt(index)+1);
	    	System.out.println("New address: "+address);
	        return new UpdateAddressResponse(upaddr.getId(), address);
    	} catch(Exception e) {
    		throw new GenericServerException(16, e.getMessage());
    	}
    }
}