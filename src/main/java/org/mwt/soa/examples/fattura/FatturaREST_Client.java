package org.mwt.soa.examples.fattura;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;


public class FatturaREST_Client {

    private static final String baseURI = "http://localhost:8080/FatturaREST/rest";
    //private static final String baseURI = "http://localhost:8080/Fattura_REST_Server_Servlet_Maven/rest";
    //private static final String baseURI = "http://localhost/Fattura_REST_Server_PHP/public";

    //una entry di esempio, già serializzata in JSON (come farebbe Google Gson, per esempio)  
    private static final String dummy_json_entry = "{\"venditore\":{\"ragioneSociale\":\"MWT\",\"partitaIVA\":\"123456789\",\"via\":\"\",\"citta\":\"L'Aquila\",\"civico\":\"\"},\"intestazione\":{\"ragioneSociale\":\"Acquirente 505\",\"partitaIVA\":\"123456789505\",\"via\":\"\",\"citta\":\"Roma\",\"civico\":\"\"},\"numero\":1234,\"data\":[2020,10,8],\"prodotti\":[{\"codice\":\"P505-0\",\"descrizione\":\"Prodotto P505-0\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":0.0,\"sconto\":0,\"prezzoTotale\":0.0,\"iva\":22},{\"codice\":\"P505-1\",\"descrizione\":\"Prodotto P505-1\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":10.0,\"sconto\":0,\"prezzoTotale\":10.0,\"iva\":22},{\"codice\":\"P505-2\",\"descrizione\":\"Prodotto P505-2\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":20.0,\"sconto\":0,\"prezzoTotale\":20.0,\"iva\":22},{\"codice\":\"P505-3\",\"descrizione\":\"Prodotto P505-3\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":30.0,\"sconto\":0,\"prezzoTotale\":30.0,\"iva\":22},{\"codice\":\"P505-4\",\"descrizione\":\"Prodotto P505-4\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":40.0,\"sconto\":0,\"prezzoTotale\":40.0,\"iva\":22}],\"modalitaPagamento\":\"CONTANTI\",\"totali\":{\"22\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":0.0}},\"totaleGenerale\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":100.0}}";
    //versione modificata per data in formato millisecondi (metodo base usato anche da jackson)
    //private static final String dummy_json_entry = "{\"venditore\":{\"ragioneSociale\":\"MWT\",\"partitaIVA\":\"123456789\",\"via\":\"\",\"citta\":\"L'Aquila\",\"civico\":\"\"},\"intestazione\":{\"ragioneSociale\":\"Acquirente 505\",\"partitaIVA\":\"123456789505\",\"via\":\"\",\"citta\":\"Roma\",\"civico\":\"\"},\"numero\":1234,\"data\":1586766766714,\"prodotti\":[{\"codice\":\"P505-0\",\"descrizione\":\"Prodotto P505-0\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":0.0,\"sconto\":0,\"prezzoTotale\":0.0,\"iva\":22},{\"codice\":\"P505-1\",\"descrizione\":\"Prodotto P505-1\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":10.0,\"sconto\":0,\"prezzoTotale\":10.0,\"iva\":22},{\"codice\":\"P505-2\",\"descrizione\":\"Prodotto P505-2\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":20.0,\"sconto\":0,\"prezzoTotale\":20.0,\"iva\":22},{\"codice\":\"P505-3\",\"descrizione\":\"Prodotto P505-3\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":30.0,\"sconto\":0,\"prezzoTotale\":30.0,\"iva\":22},{\"codice\":\"P505-4\",\"descrizione\":\"Prodotto P505-4\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":40.0,\"sconto\":0,\"prezzoTotale\":40.0,\"iva\":22}],\"modalitaPagamento\":\"CONTANTI\",\"totali\":{\"22\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":0.0}},\"totaleGenerale\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":100.0}}";

    //usiamo Apache Httpclient perchè molto più intuitivo della classi Java.net...
    CloseableHttpClient client = HttpClients.createDefault();

    private void logRequest(ClassicHttpRequest request) {
        try {
            System.out.println("* Metodo: " + request.getMethod());
            System.out.println("* URL: " + request.getRequestUri());
            if (request.getFirstHeader("Accept") != null) {
                System.out.println("* " + request.getFirstHeader("Accept"));
            }
            System.out.println("* Headers: ");
            Header[] request_headers = request.getHeaders();
            for (Header header : request_headers) {
                System.out.println("** " + header.getName() + " = " + header.getValue());
            }
            switch (request.getMethod()) {
                case "POST": {
                    HttpEntity e = ((HttpPost) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                case "PUT": {
                    HttpEntity e = ((HttpPut) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                case "PATCH": {
                    HttpEntity e = ((HttpPatch) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                default:
                    break;
            }
        } catch (IOException ex) {
            System.out.println("Cannot dump request: " + ex.getMessage());
        }
    }

    private void logResponse(ClassicHttpResponse response) {
        System.out.println("* Headers: ");
        Header[] response_headers = response.getHeaders();
        for (Header header : response_headers) {
            System.out.println("** " + header.getName() + " = " + header.getValue());
        }
        System.out.println("* Return status: " + response.getReasonPhrase() + " (" + response.getCode() + ")");
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                entity.writeTo(System.out);
                System.out.println();
            } catch (IOException ex) {
                System.out.println("Cannot dump response: " + ex.getMessage());
            }
        }
    }

    private void executeAndDump(String description, ClassicHttpRequest request) {

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(description);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("REQUEST: ");
        logRequest(request);
        try {
            client.execute(request, response -> {
                //preleviamo il contenuto della risposta
                System.out.println("RESPONSE: ");
                logResponse(response);
                return null;
            });
        } catch (IOException ex) {
            System.out.println("Cannot execute request: " + ex.getMessage());
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();

    }

    public void doTests() throws IOException {

        
        //creiamo la richiesta (GET)
        HttpGet get_request = new HttpGet(baseURI + "/fatture");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Lista entries (JSON)",get_request);

    
        get_request = new HttpGet(baseURI + "/fatture/count");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Numero entries (JSON)",get_request);

        get_request = new HttpGet(baseURI + "/fatture/2020/1234");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Singola entry (JSON)",get_request);

        get_request = new HttpGet(baseURI + "/fatture?partitaIVA=8574557");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Filtraggio collection tramite parametri GET",get_request);
        
        HttpPost post_request = new HttpPost(baseURI + "/fatture");
        //per una richiesta POST, prepariamo anche il payload specificandone il tipo
        HttpEntity payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        post_request.setEntity(payload);
        executeAndDump("Creazione entry",post_request);

        HttpPut put_request = new HttpPut(baseURI + "/fatture/2020/12345");
        //per una richiesta PUT, prepariamo anche il payload specificandone il tipo
        payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        put_request.setEntity(payload);
        executeAndDump("Aggiornamento entry",put_request);

        HttpDelete delete_request = new HttpDelete(baseURI + "/fatture/2020/12345");
        executeAndDump("Eliminazione entry",delete_request);

        get_request = new HttpGet(baseURI + "/fatture/2020/12345/elementi");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Dettaglio entry (JSON)",get_request);
       
        post_request = new HttpPost(baseURI + "/auth/login");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "pippo"));
        params.add(new BasicNameValuePair("password", "pippopass"));
        post_request.setEntity(new UrlEncodedFormEntity(params));
        executeAndDump("Login",post_request);      
        //ripetiamo la request per catturare il token...
        Header ah = client.execute(post_request, response -> {
            return response.getFirstHeader("Authorization");
        });

        get_request = new HttpGet(baseURI + "/fatture/2020");
        get_request.setHeader("Accept", "application/json");
        get_request.setHeader("Authorization", ah.getValue());
        executeAndDump("Collezione per anno (richiesta soggetta ad autenticazione)",get_request);

        get_request = new HttpGet(baseURI + "/fatture/2020");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Collezione per anno (tentativo senza autenticazione)",get_request);

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        FatturaREST_Client instance = new FatturaREST_Client();
        instance.doTests();
    }
}
