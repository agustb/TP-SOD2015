import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.common.*;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.client.XmlRpcClient;

public class cliente {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P";
	
	public static void main(String[] args) throws IOException {

		int puerto = 0, libreta = 0, nota = 0;
		String usuario = null;	
		
		/////////////////////////
		// Parsing parameters
		/////////////////////////
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-p":
				puerto = Integer.parseInt(args[i+1]);
				break;
			case "-A":
				usuario = ALUMNO;
				break;
			case "-P":
				usuario = PROFESOR;
				break;			
			}
			
			//opt1: LU
			if (i == 3)
				libreta = Integer.parseInt(args[i]);
			
			//opt2: Nota
			if (i == 4)
				nota = Integer.parseInt(args[i]);			
		}
		/////////////////////////
				
		try {
	        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	        config.setServerURL(new URL("http://127.0.0.1/minimatero"));

	        // create the client and configure it with instantiated configuration
	        XmlRpcClient server = new XmlRpcClient();
	        server.setConfig(config);
			
	         // Cargo parámetros para pasar a server
	         Vector params = new Vector();
	         params.addElement(libreta);
	         params.addElement(nota);
	         params.addElement(usuario);
	         
			 if (usuario.equals(ALUMNO)) {
		 		/*ALUMNO*/
		         Object result = server.execute("matero.get", params);
	 			if (result.equals("null")) {
 					System.out.printf("La libreta %s no corresponde a ningún alumno.\n",libreta); 
				} else {
					System.out.printf("La nota del alumno con LU %s es %s\n", libreta, result);
				}
			 } else {
				/*PROFESOR*/
				Object result = server.execute("matero.add", params);
				if (result.equals("-1")) {
					System.out.printf("Se produjo un error al intentar guardar la nota del alumno LU %s",libreta);
				} else {
					System.out.printf("La nota del alumno LU %s se ha guardado con éxito!",libreta);
				}
			 } 

	      } catch (Exception e) {
	    	  	System.out.println("IOException: "+ e.getMessage());
	      }

	}

}
