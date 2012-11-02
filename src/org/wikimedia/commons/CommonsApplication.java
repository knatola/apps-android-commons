package org.wikimedia.commons;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.*;

import android.accounts.*;
import android.app.Application;
import org.mediawiki.api.*;
import org.w3c.dom.Node;
import org.wikimedia.commons.auth.WikiAccountAuthenticator;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

public class CommonsApplication extends Application {

    private MWApi api;
    public static final String API_URL = "http://test.wikipedia.org/w/api.php";
   
    public static MWApi createMWApi() {
        DefaultHttpClient client = new DefaultHttpClient();
        // Because WMF servers support only HTTP/1.0. Biggest difference that
        // this makes is support for Chunked Transfer Encoding. 
        // I have this here so if any 1.1 features start being used, it 
        // throws up. 
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, 
                HttpVersion.HTTP_1_0);
        return new MWApi(API_URL, client);
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        api = createMWApi();
    }
    
    public MWApi getApi() {
        return api;
    }
    
    public Boolean revalidateAuthToken() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] allAccounts =accountManager.getAccountsByType(WikiAccountAuthenticator.COMMONS_ACCOUNT_TYPE);
        Account curAccount = allAccounts[0];
        
        accountManager.invalidateAuthToken(WikiAccountAuthenticator.COMMONS_ACCOUNT_TYPE, api.getAuthCookie());
        try {
            String authCookie = accountManager.blockingGetAuthToken(curAccount, "", false);
            api.setAuthCookie(authCookie);
            return true;
        } catch (OperationCanceledException e) {
            e.printStackTrace();
            return false;
        } catch (AuthenticatorException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getStringFromDOM(Node dom) {
       javax.xml.transform.Transformer transformer = null;
       try {
           transformer = TransformerFactory.newInstance().newTransformer();
       } catch (TransformerConfigurationException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       } catch (TransformerFactoryConfigurationError e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }

       StringWriter  outputStream = new StringWriter();
       javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(dom);
       javax.xml.transform.stream.StreamResult strResult = new javax.xml.transform.stream.StreamResult(outputStream);

       try {
        transformer.transform(domSource, strResult);
       } catch (TransformerException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       } 
       return outputStream.toString();
    }
}
