package wallet.web.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.sql.ResultSet;

public class DatabaseUtils {

    private static final String url = "jdbc:sqlite:C:\\Users\\pedro.f.gomes\\wallet-manager\\wallet-manager-rest\\database.db";

    /**
     * Connect to the database
     */
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public static Boolean managerSeedExists() {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            Statement stmt = conn.createStatement();
            String sql = "SELECT COUNT(*) FROM Manager;"; 
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
            	return false;
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return true;
    }
    
    public static String getManagerSeed() {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            Statement stmt = conn.createStatement();
            String sql = "SELECT SEED FROM Manager;"; 
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
            	return rs.getString("SEED");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }
    
    public static String getSeed(String id) {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT SEED FROM Clients WHERE ID=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("SEED");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
    }
    
    public static String getServiceProviderSeed(String id) {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT SEED FROM ServiceProviders WHERE ID=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("SEED");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
    }
    
    public static void insertManagerSeed(String seed) {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "INSERT INTO Manager (SEED) VALUES (?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, seed);
            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public static String loginClient(String email, String password) {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT ID FROM Clients WHERE EMAIL=? AND PASSWORD=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("ID");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
    }
    
    public static String loginServiceProvider(String email, String password) {
    	Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT ID FROM ServiceProviders WHERE SHORT=? AND PASSWORD=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	System.out.println(rs.getString("ID"));
            	return rs.getString("ID");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}
    
    public static void createClient(String name, String email, String password, String seed, String id) {
    	Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "INSERT INTO Clients (ID,SEED,NAME,EMAIL,PASSWORD) " +
                           "VALUES (?,?,?,?,?);"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, seed);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

	public static void createServiceProvider(String shortName, String name, String type, String password, String seed, String firstAddress,
			String id) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            // Create a Service Provider
            String sql = "INSERT INTO ServiceProviders (ID,SEED,SHORT,NAME,TYPE,PASSWORD) " +
                           "VALUES(?,?,?,?,?,?);"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, seed);
            pstmt.setString(3, shortName);
            pstmt.setString(4, name);
            pstmt.setString(5, type);
            pstmt.setString(6, password);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Create a Service Address
            setServiceProviderAddress(id,firstAddress,"0");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}
	

	public static void deleteClient(String clientId) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM Clients WHERE ID=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clientId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

	public static void deleteServiceProvider(String serviceId) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM ServiceProviders WHERE ID=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serviceId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

	public static void deleteServiceProviderAddresses(String serviceId) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM ServiceProviderAddresses WHERE ID=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serviceId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}
	
	public static void addServiceProviderAddress(String id, String address, int count) {
		// Create a Service Address
        setServiceProviderAddress(id,address,Integer.toString(count));
	}

	public static Map<String, String> getProvidersWithType(String type) {
		Connection conn = null;
		Map<String, String> ids = new HashMap<String, String>();
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT * FROM ServiceProviders WHERE TYPE=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
            	ids.put(rs.getString("ID"),rs.getString("NAME"));
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return ids;
	}
	
	public static String getProviderIdFromName(String name) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT ID FROM ServiceProviders WHERE NAME=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("ID");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}
	
	public static String getProviderIdFromNameAndType(String type, String name) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT ID FROM ServiceProviders WHERE NAME=? AND TYPE=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("ID");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}

	public static String getProviderCost(String spid) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT COST FROM ServiceProviderCost WHERE ID=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
        		return rs.getString("COST");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}
	
	public static Map<String, String> getProvidersCost(Map<String, String> spids) {
		Map<String, String> dict = new HashMap<String, String>();
        for (Entry<String, String> sp : spids.entrySet()) {
             dict.put(sp.getValue(), getProviderCost(sp.getKey()));
          System.out.println("Key: "+sp.getKey() + " & Value: " + sp.getValue());
        } 
		return dict;
	}

	public static void setServiceProviderPrice(String id, String price) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "INSERT INTO ServiceProviderCost (ID,COST) VALUES (?,?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, price);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}
	
	public static void updateServiceProviderPrice(String id, String price) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "UPDATE ServiceProviderCost SET COST=? WHERE ID=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, price);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}
	
	public static void setServiceProviderAddress(String id, String address, String index) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "INSERT INTO ServiceProviderAddresses (ID,ADDRESS,COUNT,DATE) VALUES (?,?,?,?);";
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        	Calendar cal = Calendar.getInstance();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, address);
            pstmt.setString(3, index);
            pstmt.setString(4, dateFormat.format(cal.getTime()));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

	public static String getServiceProviderAddressIndex(String id) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT MAX(CAST(COUNT AS BIGINT)) FROM ServiceProviderAddresses WHERE ID=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString(1);
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}
	
	public static String getServiceProviderAddress(String id, String index) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "SELECT ADDRESS FROM ServiceProviderAddresses WHERE ID=? AND COUNT=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, index);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
            	return rs.getString("ADDRESS");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return "";
	}

	public static int calculateDistance(String source, String destination) {
		Random random = new Random();
		return random.nextInt(10 - 1 + 1) + 1;
	}

	public static void deleteClientWithEmail(String email) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM Clients WHERE EMAIL=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

	public static void deleteServiceProviderWithShortName(String shortName) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM ServiceProviders WHERE SHORT=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shortName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

	public static void deleteServiceProviderAddressesWithShortName(String shortName) {
		Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            String sql = "DELETE FROM ServiceProviderAddresses WHERE SHORT=?;"; 
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shortName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
	}

}
