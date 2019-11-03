
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;

public class Database {
    public static Statement statement;

    public Database() throws SQLException {
        String hostName = "localhost";
        String dbName = "DNS";
        String userName = "root";
        String password = "";
        Connection connection = getMySQLConnection(hostName, dbName, userName, password);
        statement = connection.createStatement();
    }
    public static void main(String[] args) throws SQLException {
        new Database();
        String url = "125.212.211.142";
//        72.167.232.155
        System.out.println("Result = " + get(url));
    }

    public static Connection getMySQLConnection(String hostName, String dbName, String userName, String password) throws SQLException {
    	String url = "jdbc:mysql://localhost:3306/dns?serverTimezone=UTC";
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Connection conn = DriverManager.getConnection(url, userName, password);
        return conn;
    }

    public static String get(String str) throws SQLException {
        String result = "";
        str = finalString(str);
        if (isIP(str)){
            System.out.println("This is IP");
            result = returnURL(str);
        }
        else{
            System.out.println("This is URL");
            result = returnIP(str);
        }
        System.out.println("Result = " + result);
        return result;
    }

    public static boolean isIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String finalString(String str){
        String finalstring = "";
        finalstring = str.replaceAll("\\s","");
        finalstring = finalstring.replace("http://","");
        finalstring = finalstring.replace("https://","");
        finalstring = finalstring.replace("www.","");
        finalstring = finalstring.replace("/","");
        return finalstring;

    }
    public static String returnIP(String url) throws SQLException {
        String ipAddress = "";
        String query = "SELECT IP FROM DNS WHERE URL = '" + url + "'";
        ResultSet result = statement.executeQuery(query);
        if (result.next()){
            ipAddress = result.getString(1);
        }
        else {
            System.out.println("K co trong CSDL");
            addIPfromURL(url);
            ResultSet result2 = statement.executeQuery(query);
            if (result2.next()){
                ipAddress = result2.getString(1);
            }
        }
        return ipAddress;
    }

	private static void addIPfromURL(String url) {
        String getip = "";
        System.out.println("Getting IP...");
        getip = getIPfromURL(url);
        if(getip != null && !getip.isEmpty()){
            String query = "INSERT INTO `DNS`(`URL`, `IP`) VALUES ('" + url + "', '" + getip + "');";
            try {
                int result = statement.executeUpdate(query);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }


    private static String getIPfromURL(String url) {
        try {
            InetAddress host = InetAddress.getByName(url);
            System.out.println("OK\n"+host.getHostAddress());
            return host.getHostAddress();
        }
        catch (UnknownHostException e){
            System.out.println(e);
            return "";
        }
    }

    public static String returnURL(String ip) throws SQLException {
        String URL = "";
        String query = "SELECT URL FROM DNS WHERE IP = '" + ip + "'";
        ResultSet result = statement.executeQuery(query);
        if (result.next()){
            URL = result.getString(1);
        }
        else URL = "";
        return URL;
    }
//
//    private static void addURLfromIP(String ip) {
//        String geturl = "";
//        geturl = getURLfromIP(ip);
//        if(geturl != null && !geturl.isEmpty()){
//            String query = "INSERT INTO `DNS`(`URL`, `IP`) VALUES ('" + geturl + "', '" + ip + "');";
////            System.out.println(query);
//            try {
//                int result = statement.executeUpdate(query);
////                if (result > 0) {
////                    System.out.println("Insert " + result);
////                }
//            }
//            catch (Exception e){
//                System.out.println(e);
//            }
//        }
//    }
//
//    private static String getURLfromIP(String ip) {
//        try {
//            InetAddress host = InetAddress.getByName(ip);
//            System.out.println("Host = " + host.getHostName());
//            return host.getHostName();
//        } catch (UnknownHostException ex) {
//            ex.printStackTrace();
//            return "";
//        }
//    }
}