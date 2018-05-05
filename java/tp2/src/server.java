
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.webserver.WebServer;

public class server {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	
	static Map<String, String> map = new HashMap<String, String>();	
	
	// GET: devuelvo nota de libreta
	public String get(String LU){  
		return map.get(LU);
	}	
	
	// ADD: inserto nota asociada a LU
	public String add(String LU, String nota){
		try {
			map.put(LU, nota);
			return "0";
		} catch (Exception e) {
			return "1";
		}
	}	
	
	public static void main(String[] args)  throws IOException {
		int puerto = 0;
		String usuario = null, libreta = null, nota = null;	
		
		/////////////////////////
		// Parsing parameters
		/////////////////////////
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-p":
				puerto = Integer.parseInt(args[i+1]);
				break;
			}
		}
		/////////////////////////
		map.put("100", "9");
		map.put("200", "4");
		map.put("300", "7");	
		
		try {
	        System.out.println("Intentando conectar al servidor RPC en puerto "+ puerto);
	        
	        WebServer server = new WebServer(80);
	        server.addHandler("matero", new server());
	        server.start();
	        
	        System.out.println("Conexión exitosa!");
	        System.out.println("Escuchando peticiones (terminar programa para detener)");
						
		} catch (IOException e) {
			 System.out.println("IOException: " + e.getMessage());	
		}

	}
}