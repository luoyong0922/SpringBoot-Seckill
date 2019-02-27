package com.roy.exam1.utils;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 用于进行Https请求的HttpClient
 * @ClassName: SSLClient
 * @Description: TODO
 * @author Devin <xxx>
 * @date 2017年2月7日 下午1:42:07
 *
 */
public class SSLClient extends DefaultHttpClient {
    public SSLClient() throws Exception{
        super();
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }


        public static void main(String[] args) {
            final String text = "Base64 finally in Java 8!";
            final String text2 = "QThGRUFDODAyQzA0REM4MThCMjA1Q0MyNEYzNzlCNUIucWFibzAyYQ==";

            final String encoded = Base64
                    .getEncoder()
                    .encodeToString( text.getBytes( StandardCharsets.UTF_8 ) );
            System.out.println( encoded );

            final String decoded = new String(
                    Base64.getDecoder().decode( encoded ),
                    StandardCharsets.UTF_8 );
              System.out.println( decoded );

    }
}