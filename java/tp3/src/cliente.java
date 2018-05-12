import java.net.*;
import java.io.*;
import java.util.*;
import spread.*;

public class cliente {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P";
	static final String GRUPO_CLIENTE = "GRUPO_CLIENTE";
	static final String GRUPO_SERVIDOR = "GRUPO_SERVIDOR";
	private static SpreadConnection conexionSpread;
	private static String userName;
    boolean espera;	
	
	
	private static void ConexionSpread(String nombreCliente, String ip, int puerto) {
	
		try
		{
			// Conecto
			conexionSpread = new SpreadConnection();
			conexionSpread.connect(InetAddress.getByName(ip), puerto, nombreCliente, false, true);
			
			// Unión a grupo
			SpreadGroup spreadGroup = new SpreadGroup();
			spreadGroup.join(conexionSpread, GRUPO_CLIENTE);
			System.out.println("Uniendo a " + spreadGroup + ".");			
			System.out.println("Se ha establecido la conexión " + nombreCliente + " con " + ip + ":" + puerto);

		}
		catch(SpreadException e)
		{
			System.err.println("Ha ocurrido un error al conectar a Daemon");
			e.printStackTrace();
			System.exit(1);
		}
		catch(UnknownHostException e)
		{
			System.err.println("El Daemon de Spread no fue encontrado." + ip);
			System.exit(1);
		}
		
	}	
	
	private static void CerrarConexionSpread() {
        
		try {
			conexionSpread.disconnect();
		} catch (SpreadException e) {
			e.printStackTrace();
		}

	}	
	
	public static void main(String[] args) throws IOException {

		int puerto = 0, libreta = 0, nota = 0;
		String usuario = null;	
		String nombreCliente = "mm_cliente";
		String ip = "127.0.0.1";
		
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
		String linea;		
		
		// Conecto con SPREAD
		ConexionSpread(nombreCliente, ip, puerto);
		
        /////////
		// Envío parámetro Usuario
		linea = usuario;
        SpreadMessage msgUsuario = new SpreadMessage();
		msgUsuario.setSafe();
		msgUsuario.addGroup(GRUPO_SERVIDOR);
		msgUsuario.setData(linea.getBytes());
		try {
			conexionSpread.multicast(msgUsuario);
		} catch (SpreadException e) {
			e.printStackTrace();
		}
        
		/////////
   	    // Envío parámetro Libreta
		linea = Integer.toString(libreta);;
        SpreadMessage msgLU = new SpreadMessage();
        msgLU.setSafe();
        msgLU.addGroup(GRUPO_SERVIDOR);
        msgLU.setData(linea.getBytes());
		try {
			conexionSpread.multicast(msgLU);
		} catch (SpreadException e) {
			e.printStackTrace();
		}
		
		/////////
   	    // Envío parámetro Nota
		linea = Integer.toString(libreta);;
        SpreadMessage msgNota = new SpreadMessage();
        msgNota.setSafe();
        msgNota.addGroup(GRUPO_SERVIDOR);
        msgNota.setData(linea.getBytes());
		try {
			conexionSpread.multicast(msgNota);
		} catch (SpreadException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		// Cierro conexión SPREAD
		CerrarConexionSpread();
		
		
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
