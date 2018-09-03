package com.sd.lib.http.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryProvider
{
    public static SSLSocketFactory getTrustedFactory() throws NoSuchAlgorithmException, KeyManagementException
    {
        final TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType)
            {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType)
            {
            }
        }};

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, new SecureRandom());
        return context.getSocketFactory();
    }

    public static SSLSocketFactory get(InputStream... certificatesInput) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException
    {
        return get(null, null, certificatesInput);
    }

    public static SSLSocketFactory get(InputStream keyStoreInput, char[] keyStorePassword, InputStream... certificatesInput) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException
    {
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);

        int index = 0;
        for (InputStream item : certificatesInput)
        {
            index++;
            keyStore.setCertificateEntry(String.valueOf(index), certificateFactory.generateCertificate(item));

            if (item != null)
                item.close();
        }

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);


        KeyManager[] keyManagers = null;
        if (keyStoreInput != null && keyStorePassword != null)
        {
            final KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(keyStoreInput, keyStorePassword);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, keyStorePassword);

            keyManagers = keyManagerFactory.getKeyManagers();
        }


        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext.getSocketFactory();
    }
}
