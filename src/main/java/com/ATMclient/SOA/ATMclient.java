package com.ATMclient.SOA;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.example.webserviceatm.CartaDebito;
import org.example.webserviceatm.PrelievoRequestSchema;
import org.example.webserviceatm.PrelievoResponseSchema;
import org.example.webserviceatm.WebServiceATM;

import com.atmproject.soa.ATMwsImplService;

public class ATMclient {

	public static void main(String[] args) throws MalformedURLException {

		ATMwsImplService service = new ATMwsImplService(new URL("http://localhost:8080/ATMservice/ATMservice?wsdl"));

		WebServiceATM ATMport = service.getATMwsImplPort();

		Client client = ClientProxy.getClient(ATMport);

		Endpoint endpoint = client.getEndpoint();

		// ------------- OUTPUT INTERCEPTOR: autenticazione, cifratura, firma, timestamp
		// ---------------

		Map<String, Object> outProps = new HashMap<String, Object>();

		// azioni che deve compiere l'input interceptor (conta l'ordine, bisogna prima
		// firmare per poi poter cifrare anche la firma)
		outProps.put(WSHandlerConstants.ACTION, "UsernameToken Timestamp Signature Encrypt");

		// autenticazione
		outProps.put(WSHandlerConstants.USER, "pippo");
		outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, UTPasswordCallBack.class.getName());

		// cifratura (tramite la chiave pubblica del server presente anche nel keystore
		// del client)
		outProps.put(WSHandlerConstants.ENC_PROP_FILE, "etc/clientKeystore.properties");
		outProps.put(WSHandlerConstants.ENCRYPTION_USER, "myservicekey");
		// cifratura dell'elemento firma e del contenuto del soap body (la firma non la
		// vediamo più il body si ma non il contenuto)
		outProps.put(WSHandlerConstants.ENCRYPTION_PARTS,
				"{Element}{http://www.w3.org/2000/09/xmldsig#}Signature;{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");

		// firma (tramite chiave privata del client)
		outProps.put(WSHandlerConstants.SIG_PROP_FILE, "etc/clientKeystore.properties");
		outProps.put(WSHandlerConstants.SIGNATURE_USER, "myclientkey");
		outProps.put(WSHandlerConstants.SIGNATURE_PARTS,
				"{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");

		outProps.put("timeToLive", "30");

		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
		endpoint.getOutInterceptors().add(wssOut);

		// ------------ INPUT INTERCEPTOR: decifratura, verifica firma, timestamp
		// ----------------
		HashMap<String, Object> inProps = new HashMap<>();

		inProps.put(WSHandlerConstants.ACTION, "Encrypt Signature Timestamp");

		// decifratura
		inProps.put(WSHandlerConstants.DEC_PROP_FILE, "etc/clientKeystore.properties");
		inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, UTPasswordCallBack.class.getName());

		// verifica firma
		inProps.put(WSHandlerConstants.SIG_PROP_FILE, "etc/clientKeystore.properties");

		WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
		endpoint.getInInterceptors().add(wssIn);

		// ---------------------------- SCRIPT CLIENT ---------------------------

		PrelievoRequestSchema request = new PrelievoRequestSchema();

		CartaDebito cartaDebito = new CartaDebito();
		cartaDebito.setIdcartaDebito(BigInteger.valueOf(4567));
		cartaDebito.setIdcontoCorrente(BigInteger.valueOf(1234));
		cartaDebito.setPIN(BigInteger.valueOf(0000));
		cartaDebito.setIdUtente(BigInteger.valueOf(0));

		request.setCartaDebito(cartaDebito);
		request.setPin(BigInteger.valueOf(0000));
		request.setImporto(BigInteger.valueOf(500));

		PrelievoResponseSchema response = ATMport.prelievo(request);

		System.out.println(response.isResult());

	}

}
