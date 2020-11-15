package com.ATMclient.SOA;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class UTPasswordCallBack implements CallbackHandler {

	private Map<String, String> passwords = new HashMap<>();
	
	public UTPasswordCallBack() {
		passwords.put("pippo", "segreto");
		passwords.put("myclientkey", "ckpass");
	}
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		
		for(Callback callback : callbacks) {
			WSPasswordCallback passwordCallBack = (WSPasswordCallback) callback;
			//getIdentifier ritorna lo username poi prendiamo la password corrispondete
			//dall'hashmap
			String password = passwords.get(passwordCallBack.getIdentifier());
			//se l'utente ha una password nell'hashmap settiamo la password
			//nel passwordCallBack
			if(password != null) {
				passwordCallBack.setPassword(password);
				return;
			}			
		}
		
		//non funziona per recuperare le chiavi, da non usare
		/*for(int i = 0; i < callbacks.length; i++) {
		WSPasswordCallback wpc = (WSPasswordCallback) callbacks[i];
		if(wpc.getIdentifier().equals("pippo")) {
			wpc.setPassword("segreto");
			return;
		}
	}*/
	}

}
