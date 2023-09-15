package org.mwt.soa.examples.fattura;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class FatturaREST_Client {

    private static final String baseURI = "http://localhost:8085/FatturaREST/rest";
    //private static final String baseURI = "http://localhost:8080/Fattura_REST_Server_Servlet_Maven/rest";
    //private static final String baseURI = "http://localhost/Fattura_REST_Server_PHP/public";

    //una entry di esempio, già serializzata in JSON (come farebbe Google Gson, per esempio)  
    private static final String dummy_json_entry = "{\"venditore\":{\"ragioneSociale\":\"MWT\",\"partitaIVA\":\"123456789\",\"via\":\"\",\"citta\":\"L'Aquila\",\"civico\":\"\"},\"intestazione\":{\"ragioneSociale\":\"Acquirente 505\",\"partitaIVA\":\"123456789505\",\"via\":\"\",\"citta\":\"Roma\",\"civico\":\"\"},\"numero\":1234,\"data\":[2020,10,8],\"prodotti\":[{\"codice\":\"P505-0\",\"descrizione\":\"Prodotto P505-0\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":0.0,\"sconto\":0,\"prezzoTotale\":0.0,\"iva\":22},{\"codice\":\"P505-1\",\"descrizione\":\"Prodotto P505-1\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":10.0,\"sconto\":0,\"prezzoTotale\":10.0,\"iva\":22},{\"codice\":\"P505-2\",\"descrizione\":\"Prodotto P505-2\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":20.0,\"sconto\":0,\"prezzoTotale\":20.0,\"iva\":22},{\"codice\":\"P505-3\",\"descrizione\":\"Prodotto P505-3\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":30.0,\"sconto\":0,\"prezzoTotale\":30.0,\"iva\":22},{\"codice\":\"P505-4\",\"descrizione\":\"Prodotto P505-4\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":40.0,\"sconto\":0,\"prezzoTotale\":40.0,\"iva\":22}],\"modalitaPagamento\":\"CONTANTI\",\"totali\":{\"22\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":0.0}},\"totaleGenerale\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":100.0}}";
    //versione modificata per data in formato millisecondi (metodo base usato anche da jackson)
    //private static final String dummy_json_entry = "{\"venditore\":{\"ragioneSociale\":\"MWT\",\"partitaIVA\":\"123456789\",\"via\":\"\",\"citta\":\"L'Aquila\",\"civico\":\"\"},\"intestazione\":{\"ragioneSociale\":\"Acquirente 505\",\"partitaIVA\":\"123456789505\",\"via\":\"\",\"citta\":\"Roma\",\"civico\":\"\"},\"numero\":1234,\"data\":1586766766714,\"prodotti\":[{\"codice\":\"P505-0\",\"descrizione\":\"Prodotto P505-0\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":0.0,\"sconto\":0,\"prezzoTotale\":0.0,\"iva\":22},{\"codice\":\"P505-1\",\"descrizione\":\"Prodotto P505-1\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":10.0,\"sconto\":0,\"prezzoTotale\":10.0,\"iva\":22},{\"codice\":\"P505-2\",\"descrizione\":\"Prodotto P505-2\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":20.0,\"sconto\":0,\"prezzoTotale\":20.0,\"iva\":22},{\"codice\":\"P505-3\",\"descrizione\":\"Prodotto P505-3\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":30.0,\"sconto\":0,\"prezzoTotale\":30.0,\"iva\":22},{\"codice\":\"P505-4\",\"descrizione\":\"Prodotto P505-4\",\"quantita\":1,\"unita\":\"N\",\"prezzoUnitario\":40.0,\"sconto\":0,\"prezzoTotale\":40.0,\"iva\":22}],\"modalitaPagamento\":\"CONTANTI\",\"totali\":{\"22\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":0.0}},\"totaleGenerale\":{\"imponibile\":100.0,\"imposta\":0.0,\"generale\":100.0}}";

    //usiamo Apache Httpclient perchè molto più intuitivo della classi Java.net...
    CloseableHttpClient client = HttpClients.createDefault();

    private void execute_and_dump(HttpRequestBase request) {
        try {
            System.out.println();
            System.out.println("REQUEST: ");
            System.out.println("* Metodo: " + request.getMethod());
            System.out.println("* URL: " + request.getURI());
            if (request.getFirstHeader("Accept") != null) {
                System.out.println("* " + request.getFirstHeader("Accept"));
            }
            System.out.println("* Headers: ");
            Header[] headers = request.getAllHeaders();
            for (Header header : headers) {
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
            try (CloseableHttpResponse response = client.execute(request)) {
                //preleviamo il contenuto della risposta
                System.out.println("RESPONSE: ");
                System.out.println("* Headers: ");
                headers = response.getAllHeaders();
                for (Header header : headers) {
                    System.out.println("** " + header.getName() + " = " + header.getValue());
                }
                System.out.println("* Return status: " + response.getStatusLine().getReasonPhrase() + " (" + response.getStatusLine().getStatusCode() + ")");
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    entity.writeTo(System.out);
                    System.out.println();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doTests() throws IOException {

        //1 -- Lista entries (JSON)
        System.out.println("1 -- Lista entries (JSON)");
        //creiamo la richiesta (GET)
        HttpGet get_request = new HttpGet(baseURI + "/fatture");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

        System.out.println();

        //1bis -- Numero entries (JSON)
        System.out.println("1bis -- Numero entries (JSON)");
        //creiamo la richiesta (GET)
        get_request = new HttpGet(baseURI + "/fatture/count");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

        System.out.println();

        //3 -- Singola entry (JSON)
        System.out.println("3 -- Singola entry (JSON)");
        get_request = new HttpGet(baseURI + "/fatture/2020/1234");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

        System.out.println();

        //5 -- Filtraggio collection tramite parametri GET
        System.out.println("5 -- Filtraggio collection tramite parametri GET");
        get_request = new HttpGet(baseURI + "/fatture?partitaIVA=8574557");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

        System.out.println();

        //6 -- Creazione  
        System.out.println("6 -- Creazione entry");
        HttpPost post_request = new HttpPost(baseURI + "/fatture");
        //per una richiesta POST, prepariamo anche il payload specificandone il tipo
        HttpEntity payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        post_request.setEntity(payload);
        execute_and_dump(post_request);

        System.out.println();

        //7 -- Aggiornamento 
        System.out.println("7 -- Aggiornamento entry");
        HttpPut put_request = new HttpPut(baseURI + "/fatture/2020/12345");
        //per una richiesta PUT, prepariamo anche il payload specificandone il tipo
        payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        put_request.setEntity(payload);
        execute_and_dump(put_request);

        System.out.println();

        //8 -- Eliminazione
        System.out.println("8 -- Eliminazione entry");
        HttpDelete delete_request = new HttpDelete(baseURI + "/fatture/2020/12345");
        execute_and_dump(delete_request);

        System.out.println();

        //9 -- Dettaglio Entry (JSON)
        System.out.println("9 -- Dettaglio entry (JSON)");
        get_request = new HttpGet(baseURI + "/fatture/2020/12345/elementi");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

        System.out.println();
       
        //10 -- Login
        System.out.println("10 -- Login");
        post_request = new HttpPost(baseURI + "/auth/login");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "pippo"));
        params.add(new BasicNameValuePair("password", "pippopass"));
        post_request.setEntity(new UrlEncodedFormEntity(params));
        execute_and_dump(post_request);
        //ripetiamo la request per catturare il token...
        Header ah;
        try (CloseableHttpResponse response = client.execute(post_request)) {
            ah = response.getFirstHeader("Authorization");
        }

        System.out.println();

        //11a -- Collezione per anno (richiesta soggetta ad autenticazione)
        System.out.println("11a -- Collezione per anno (richiesta soggetta ad autenticazione)");
        get_request = new HttpGet(baseURI + "/fatture/2020");
        get_request.setHeader("Accept", "application/json");
        get_request.setHeader("Authorization", ah.getValue());
        execute_and_dump(get_request);
        
        System.out.println();

        //11b -- Collezione per anno (tentativo senza autenticazione)
        System.out.println("11b -- Collezione per anno (tentativo senza autenticazione)");
        get_request = new HttpGet(baseURI + "/fatture/2020");
        get_request.setHeader("Accept", "application/json");
        execute_and_dump(get_request);

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
