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


public class SimpleHttpsServer {

    private HttpsServer httpsServer;

    public SimpleHttpsServer() {

        try {
            InetSocketAddress address = new InetSocketAddress(HttpsServerConfig.PORT);

            httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance(HttpsServerConfig.PROTOCOL);

            char[] password = HttpsServerConfig.KEY_STORE_PASS.toCharArray();
            KeyStore ks = KeyStore.getInstance(HttpsServerConfig.KEY_STORE_TYPE);
            FileInputStream fis = new FileInputStream(HttpsServerConfig.KEY_STORE_PATH);
            ks.load(fis, password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(HttpsServerConfig.ALGORITHM);
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(HttpsServerConfig.ALGORITHM);
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

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
            System.out.println("Failed to create HTTPS server on port " + HttpsServerConfig.PORT + " of localhost");
            exception.printStackTrace();

        }
    }

    public void stop() {
        httpsServer.stop(2000);
    } 

}


