package com.ATMclient.SOA;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
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

		// ------------------------------------------------------
		// USERNAME TOKEN PROFILE INFORMATION
		// Accedere all'oggetto client
		Client client = ClientProxy.getClient(ATMport);
		// dal client prendere l'endpoint
		Endpoint endpoint = client.getEndpoint();

		// configurazione delle proprietà dello username token profile
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		props.put(WSHandlerConstants.USER, "pippo");
		props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		props.put(WSHandlerConstants.PW_CALLBACK_CLASS, UTPasswordCallBack.class.getName());

		// configurazione out interceptor (come per il lato server solo che sono di
		// output)
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(props);
		// aggiungere l'interceptor creato alla lista di interceptor dell'endpoint
		endpoint.getOutInterceptors().add(wssOut);

		// -------------------------------------------------------
		// ora che posso accedere alle operazioni devo indicare quali stub usano
		// -----------------GET

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
