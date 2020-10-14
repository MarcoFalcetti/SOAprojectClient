package com.ATMclient.SOA;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import org.example.webserviceatm.CartaDebito;
import org.example.webserviceatm.PrelievoRequestSchema;
import org.example.webserviceatm.PrelievoResponseSchema;
import org.example.webserviceatm.WebServiceATM;

import com.atmproject.soa.ATMwsImplService;

public class ATMclient {

	public static void main(String[] args) throws MalformedURLException {
		ATMwsImplService service = new ATMwsImplService(new URL("http://localhost:8080/ATMservice/ATMservice?wsdl"));

		WebServiceATM ATMport = service.getATMwsImplPort();
		
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
