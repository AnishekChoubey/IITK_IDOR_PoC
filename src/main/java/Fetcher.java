
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Scanner;
import java.util.Set;

public class Fetcher {


    static Gson gson = new Gson();
    public static UserDatIITK fetchInfo(int id) throws Exception{
        Duration timeout = Duration.ofMinutes(2);
        URI uri = createURI(
                "https",
                "pingala.iitk.ac.in",
                "/CYBER_UGADM/generateOrderId",
                "applicant_master_pk="+id+"&payment_type=ACC&fee_type=1&payhead=49&_=1");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(timeout);
        headers.forEach((s, strings) -> {
            if (!RESTRICTED_HEADERS.contains(s.toLowerCase())) {
                for (String string : strings) {
                    requestBuilder.header(s, string);
                }
            }else {}
        });
        requestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
        var req = requestBuilder.build();
        String s =  client.send(req, HttpResponse.BodyHandlers.ofString()).body();

        return gson.fromJson(s,UserDatIITK.class);

    }


    private static final Headers headers = new Headers();

    static {
        headers.add("Cookie"," JSESSIONID=9FF7896F0187625CE61A024813AB6949; CYBER-SERVERID=CY_1_0; SERVERID=2.02");
        headers.add("Accept-Encoding","gzip, deflate, br");
        headers.add("Priority","u=0, i");
        headers.add("Connection","keep-alive");
        headers.add("Sec-Fetch-Mode","cors");
        headers.add("Sec-Fetch-Dest","empty");
        headers.add("Referer","https://pingala.iitk.ac.in/CYBER_UGADM/logincheck");
        headers.add("X-Requested-With","XMLHttpRequest");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36");
        headers.add("Accept","*/*");
        headers.add("Sec-Fetch-Site","same-origin");
        headers.add("Sec-Ch-Ua-Platform","\"Windows\"");
        headers.add("X-Csrf-Token","b8da4821-26fe-43f9-84c8-658581ace0cc");
        headers.add("Accept-Language","en-US,en;q=0.9");
        headers.add("Sec-Ch-Ua","\"Not-A.Brand\";v=\"24\", \"Chromium\";v=\"146\"");
        headers.add("Sec-Ch-Ua-Mobile","?0");
    }


    public static final Set<String> RESTRICTED_HEADERS = Set.of(
            "host", "content-length", "transfer-encoding", "connection", "expect", "date"
    );
    private static SSLContext sslContext;
    static {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] x, String s) {
                        }

                        public void checkServerTrusted(X509Certificate[] x, String s) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static final HttpClient client = buildClient();

    private static HttpClient buildClient(){
        var b = HttpClient.newBuilder();
        b.connectTimeout(Duration.ofMinutes(100))
                .version(HttpClient.Version.HTTP_2);
        if (Main.proxySelector!=null){
            b.proxy(Main.proxySelector)
                    .sslContext(sslContext);
        }
        return b.build();
    }


    private static URI createURI(String targetScheme, String targetSite, String rawPath, String rawQuery) throws URISyntaxException {
        if (rawPath != null && !rawPath.startsWith("/")) {
            rawPath = "/" + rawPath;
        }
        return new URI(
                targetScheme,
                targetSite,
                rawPath,
                rawQuery,
                null
        );
    }


}
