package server.https;

import com.sun.net.httpserver.*;

import server.http.GroupHandler;
import server.http.LoginHandler;
import server.http.ProductHandler;
import utils.Routes;

import javax.net.ssl.*;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

//https://www.charlesproxy.com/documentation/faqs/localhost-ssl-traffic-fails-with-err_connection_closed/
//https://support.code42.com/CP/Admin/On-premises/6/Configuring/Install_a_CA-signed_SSL_certificate_for_HTTPS_console_access
public class SimpleHttpsServer {

    private HttpsServer httpsServer;

    public SimpleHttpsServer() {

        try {
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(8766);

            // initialise the HTTPS server
            httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "123456".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("keyStore/localhost.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            HttpContext context = httpsServer.createContext(Routes.PRODUCT_ROUTE, new ProductHandler());
            HttpContext context2 = httpsServer.createContext(Routes.GROUP_ROUTE, new GroupHandler());
            httpsServer.createContext(Routes.LOGIN_ROUTE, new LoginHandler());
            
            context.setAuthenticator(new ServerAuth());
            context2.setAuthenticator(new ServerAuth());
            
            httpsServer.setExecutor(Executors.newCachedThreadPool());
            httpsServer.start();

        } catch (Exception exception) {
            System.out.println("Failed to create HTTPS server on port " + 8766 + " of localhost");
            exception.printStackTrace();

        }
    }

    public void stop() {
        httpsServer.stop(2000);
    }

    public static void main(String[] args) throws Exception {
        new SimpleHttpsServer();
        System.out.println("HTTPS Server has been started..");
    }
    

}


