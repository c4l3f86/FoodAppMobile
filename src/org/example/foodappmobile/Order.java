package org.example.foodappmobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Order extends Activity implements OnClickListener{
	
	final String url = "http://192.168.2.6:3000/rest/product";
	String result = "";
	/* Array di stringhe per i nomi dei prodotti*/
	String[] name;
	/* Array di stringhe per le descrizioni dei prodotti*/
	String[] desc;
	/* Array di stringhe per i prezzi dei prodotti*/
	String[] price;
	/* Cookie usato per mantenere la sessione*/
	Cookie mycookie = null;
	/* Context Http locale per aggiungere i cookie alla richiesta POST*/
	HttpContext localContext = null;
	
	/** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        result = callWebService(url);
        System.out.println(result);
        //ordview = new OrderView(this, result);
        //setContentView(ordview);
        //ordview.requestFocus();
        
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.background);
        
        /* Definisco una vista ScrollView per 'attaccargli' il LinearLayout in modo che tutti i prodotti vengano visualizzati*/
        ScrollView sv = new ScrollView(this); 
                
        ProductList prodList = null; //Variabile per salvare la lista di prodotti
		int numProd = 0;	//Variabile per contare il numero di prodotti
		
		String jsonData = result;
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		JSONObject j;
		
		try {
			j = new JSONObject(jsonData);
			ProductList temp = gson.fromJson(j.toString(), ProductList.class);
			prodList = temp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (prodList == null) {
			System.out.println("Errore nella deserializzazione JSON");
		}
		
		/* Ciclo che serve per contare il numero dei prodotti*/ 
		for (Product prod : prodList.getProds()) {
			numProd = numProd + 1;
			//System.out.println(numProd);
		}
		
		/* Array di stringhe per i nomi dei prodotti*/
		name = new String[numProd];
		/* Array di stringhe per le descrizioni dei prodotti*/
		desc = new String[numProd];
		/* Array di stringhe per i prezzi dei prodotti*/
		price = new String[numProd];
		/* Array di TextView per visualizzare i nomi dei prodotti*/
		TextView[] prodName = new TextView[numProd];
		/* Array di TextView per visualizzare le descrizioni dei prodotti*/
		TextView[] prodDesc = new TextView[numProd];
		/* Array di TextView per visualizzare i prezzi dei prodotti*/
		TextView[] prodPrice = new TextView[numProd];
		
		/* Definisco una vista piccola per separare i vari prodotti*/
		View[] sep = new View[numProd];
		
		/* Array di Button per creare bottoni che servono per aggiungere il relativo prodotto
		 * nel carrello */
		Button[] btn = new Button[numProd];
		
		/* Ciclo per inizializzare gli array di Stringhe */
		int i=0;
		for (Product prod : prodList.getProds()) {
			name[i] = prod.getName();
			desc[i] = prod.getDescription();
			price[i] = prod.getPrice();
			i++;
		}
		
		/* Ciclo per settare i TextView da visualizzare*/
		for (i=0; i < numProd; i++) {
			prodName[i] = new TextView(this);
			prodName[i].setText(name[i]);
			ll.addView(prodName[i]);
			prodDesc[i] = new TextView(this);
			prodDesc[i].setText(desc[i]);
			ll.addView(prodDesc[i]);
			prodPrice[i] = new TextView(this);
			prodPrice[i].setText(price[i]);
			ll.addView(prodPrice[i]);
			btn[i] = new Button(this);
			btn[i].setText("Aggiungi al carrello");
			btn[i].setId(i);
			btn[i].setOnClickListener(this);
			btn[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
			ll.addView(btn[i]);	
			sep[i] = new View(this);
			sep[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			sep[i].setBackgroundResource(R.color.separator);
			sep[i].setMinimumHeight(3);
			ll.addView(sep[i]);
		} 
		
		/* Aggiungo il LinearLayout alla ScrollView e poi la visualizzo */
		sv.addView(ll);
		setContentView(sv);
    } //fine onCreate
    
    
    public String callWebService(String url) {
    	
		HttpClient httpclient = new DefaultHttpClient();  
		HttpGet request = new HttpGet(url);  
		BasicResponseHandler handler = new BasicResponseHandler(); 
		try {  
			result = httpclient.execute(request, handler);  }
		catch (ClientProtocolException e) {	e.printStackTrace(); } 
		catch (IOException io) { io.printStackTrace();  } 
		httpclient.getConnectionManager().shutdown();
		
		return result;
    		
	}

    public void onClick (View v) {
		int id = v.getId();
		//System.out.println("Cliccato il bottone "+ id);
		
		//Serve per fare in modo che il metodo POST venga gestito tramite la 
    	// versione di HTTP 1.1; in questo modo la risposta è molto più performante
    	HttpParams params = new BasicHttpParams();
    	params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    	
    	//Crea un nuovo HttpClient e POST Header
    	DefaultHttpClient httpclient = new DefaultHttpClient(params);
    	HttpPost httppost = new HttpPost("http://192.168.2.6:3000/cart/add");
    	
    	if (mycookie != null) {
    		// Creo un'istanza locale di CookieStore
    		CookieStore cookieStore = new BasicCookieStore();
    		cookieStore.addCookie(mycookie);
    	
    		// Creo un context HTTP locale
    		localContext = new BasicHttpContext();
    		// Lego il cookie store al context locale
    		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    	}
    	
    	try {  
            // Aggiungo i parametri da passare con la richiesta POST 
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);  
            nameValuePairs.add(new BasicNameValuePair("sku", name[id]));  
            nameValuePairs.add(new BasicNameValuePair("description", desc[id]));
            nameValuePairs.add(new BasicNameValuePair("price", price[id]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
      
            // Execute HTTP Post Request
            if (localContext != null) {
            	HttpResponse response = httpclient.execute(httppost, localContext);
            }
            else {
            	HttpResponse response = httpclient.execute(httppost);
            }
            // Se tutto va bene viene ritornato il codice 200
            //System.out.println(response.getStatusLine().getStatusCode());
            
            /* Controllo se ci sono dei cookie nella risposta */
            if (mycookie == null) {
            	List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            	mycookie = cookies.get(0);
            	if (cookies.isEmpty()) {
            		System.out.println("Nessun Cookie");
            	} else {
            		for (int i = 0; i < cookies.size(); i++) {
            			System.out.println("- " + cookies.get(i).toString());
            		}
            	}
            }
              
        } catch (ClientProtocolException e) {            
        } catch (IOException e) {  }
	}
}
