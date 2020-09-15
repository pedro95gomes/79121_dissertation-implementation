package wallet;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import wallet.web.utils.DatabaseUtils;
import wallet.web.utils.IotaUtils;

@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class Application {

    private static final int seedLength = 81;
    private static final String ip = "192.168.0.40";
	
    public static void main(String[] args) {
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
    	SpringApplication.run(Application.class, args);
    }
}
