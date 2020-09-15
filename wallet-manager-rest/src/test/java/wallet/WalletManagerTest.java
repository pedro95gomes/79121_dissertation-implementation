package wallet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import wallet.web.controller.ClientController;
import wallet.web.controller.ServiceProviderController;
import wallet.web.dto.Request.BuyRequest;
import wallet.web.utils.DatabaseUtils;
import wallet.web.utils.IotaUtils;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest({ClientController.class, ServiceProviderController.class})
@AutoConfigureRestDocs(outputDir = "target/snippets")
@TestInstance(Lifecycle.PER_CLASS)
public class WalletManagerTest {
	/*	 IOTA NODE AND COORDINATOR NEED TO BE RUNNING ON ip */

	@Autowired
	private MockMvc mockMvc;
	static String clientId;
	static String serviceId;
	static String clientIdTest;
	static String serviceIdTest;
	static final int seedLength = 81;
    private static final int idLength = 32;
    static final String ip = "192.168.0.40";
    static String serviceName = "mockservice";
	
	@BeforeAll
	public void setup() throws Exception{
		IotaUtils.getApiInstance(ip);
    	DatabaseUtils.connect();
    	if(!DatabaseUtils.managerSeedExists()) {
    		DatabaseUtils.insertManagerSeed(IotaUtils.createSeed(seedLength));
    	}
    	String seed = DatabaseUtils.getManagerSeed();
    	try {
    		IotaUtils.getAvailableAddress(seed);
    	} catch(Exception e) {
    		System.out.println("It was not possible to connect to IOTA Node");
    	}
    	String cseed = IotaUtils.createSeed(seedLength);
    	clientId = IotaUtils.createSeed(idLength);
    	DatabaseUtils.connect();
    	DatabaseUtils.createClient("mockclient", "mockclient@mockclient.com", "mockclient", cseed, clientId);

    	String sseed = IotaUtils.createSeed(seedLength);
    	serviceId = IotaUtils.createSeed(idLength);
    	String firstAddress = IotaUtils.getAvailableAddress(seed);
    	DatabaseUtils.connect();
    	DatabaseUtils.createServiceProvider(serviceName, serviceName, "Metro" , "mockservice", sseed, firstAddress, serviceId);
        DatabaseUtils.setServiceProviderPrice(serviceId, "1");
        ClientController cr = new ClientController();
        BuyRequest br = new BuyRequest(clientId, "Visa", "20");
        cr.buy(br);
        
        Thread.sleep(90000);
	}
	
	@AfterAll
	public void terminal() throws Exception{
		// Delete Mock User and Service
    	DatabaseUtils.connect();
    	DatabaseUtils.deleteClient(clientId);

    	DatabaseUtils.connect();
    	DatabaseUtils.deleteServiceProvider(serviceId);
        DatabaseUtils.deleteServiceProviderAddresses(serviceId);
        
        // Delete Test User and Service
    	DatabaseUtils.connect();
    	DatabaseUtils.deleteClientWithEmail("username@test.com");

    	DatabaseUtils.connect();
    	DatabaseUtils.deleteServiceProviderWithShortName("Servicename");
        DatabaseUtils.deleteServiceProviderAddressesWithShortName("Servicename");
	}

	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello World")))
				.andDo(document("home"));
	}

    /*===================================================================*/
    /* LOGIN AND REGISTER */	
	
	@Test
	public void testUserCreate() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("name", "User Name");
		params.add("email", "username@test.com");
		params.add("password", "test");
		this.mockMvc.perform(post("/create").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("create"));
	}
	
	@Test
	public void testServiceRegister() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("shortName", "Servicename");
		params.add("name", "Service Name");
		params.add("type", "Metro");
		params.add("password", "test");
		this.mockMvc.perform(post("/service/register").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("serviceregister"));
	}
	
	@Test
	public void testUserLogin() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("email", "username@test.com");
		params.add("password", "test");
		params.add("type", "Client");
		this.mockMvc.perform(post("/login").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("login"));
	}
	
	/*===================================================================*/
    /* CLIENT */
	
	@Test
	public void testUserBuyTokens() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", clientId);
		params.add("paymentInfo", "test");
		params.add("value", "20");
		this.mockMvc.perform(post("/buy").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("buy"));		
	}
	
	@Test
	public void testUserBalance() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", clientId);
		this.mockMvc.perform(get("/balance").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("balance"));
	}
	
	@Test
	public void testUserPrice() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", "Metro");
		this.mockMvc.perform(get("/price").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.price").isMap())
				.andDo(document("price"));
	}
	
	@Test
	public void testUserPriceUber() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", "Uber");
		params.add("source", "Lisboa");
		params.add("destination", "Porto");
		this.mockMvc.perform(get("/priceuber").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.price").isMap())
				.andDo(document("priceuber"));
	}
	
	@Test
	public void testUserTransactionRequest() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", clientId);
		params.add("destType", "Metro");
		params.add("dest", serviceName);
		this.mockMvc.perform(get("/transaction").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andExpect(jsonPath("$.bundleId").isString())
				.andDo(document("transaction"));
	}
	
	@Test
	public void testUserCheckinTransactionRequest() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", clientId);
		params.add("destType", "Taxi");
		params.add("dest", serviceId);
		this.mockMvc.perform(get("/checkintransaction").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andExpect(jsonPath("$.bundleId").isString())
				.andDo(document("checkintransaction"));
	}
	
	@Test
	public void testUserCheckoutTransactionRequest() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", clientId);
		params.add("destType", "Taxi");
		params.add("dest", serviceId);
		params.add("elapsedTime", "1");
		params.add("branchTransaction", null);
		params.add("trunkTransaction", null);
		this.mockMvc.perform(get("/checkouttransaction").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andExpect(jsonPath("$.bundleId").isString())
				.andDo(document("checkouttransaction"));
	}
	
    /*===================================================================*/
    /* SERVICE PROVIDER */
	
	@Test
	public void testServiceBalance() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", serviceId);;
		this.mockMvc.perform(get("/service/balance").params(params))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("servicebalance"));
	}
	
	@Test
	public void testServiceUpdatePrice() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", serviceId);
		params.add("price", "1");
		this.mockMvc.perform(put("/service/updateprice").params(params))
		.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andDo(document("serviceupdateprice"));
	}
	
	@Test
	public void testServiceUpdateAddress() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", serviceId);
		this.mockMvc.perform(put("/service/updateaddress").params(params))
		.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isString())
				.andExpect(jsonPath("$.address").isString())
				.andDo(document("serviceupdateaddress"));
	}
}