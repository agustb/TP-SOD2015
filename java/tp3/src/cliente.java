import java.net.*;
import java.io.*;
import java.util.*;
import spread.*;

public class cliente implements BasicMessageListener {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P";
	static final String GRUPO_CLIENTE = "GRUPO_CLIENTE";
	static final String GRUPO_SERVIDOR = "GRUPO_SERVIDOR";
	private static SpreadConnection conexionSpread;
	private static String userName;
	private static int libreta = 0;
	private static String usuario = null;
		
	///////////////////////////////////
    /// CONEXION SPREAD
    ///////////////////////////////////
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
	
	///////////////////////////////////
    /// CERRAR CONEXION SPREAD
    ///////////////////////////////////
	private static void CerrarConexionSpread() {
        
		try {
			conexionSpread.disconnect();
		} catch (SpreadException e) {
			e.printStackTrace();
		}

	}

	///////////////////////////////////
    /// Override MESSAGERECEIVED
    ///////////////////////////////////
	@Override
	public void messageReceived(SpreadMessage msg) {
		try{
			if (msg.isRegular()){
				byte data[] = msg.getData();
				String linea = new String(data);	
			
				// RESPUESTA DEL SERVIDOR
				if (usuario.equals(ALUMNO)) {
					/*ALUMNO*/
					if (linea.equals("null")) {
						System.out.printf("La libreta %s no corresponde a ningún alumno.\n",libreta);
					} else {
						System.out.printf("La nota del alumno con LU %s es %s\n", libreta, linea);
					}
				} else {
					/*PROFESOR*/
					if (linea.equals("-1")) {
						System.out.printf("Se produjo un error al intentar guardar la nota del alumno LU %s",libreta);
					} else {
						System.out.printf("La nota del alumno LU %s se ha guardado con éxito!",libreta);
					}
				}				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		
	}
	
	///////////////////////////////////
    /// MAIN
    ///////////////////////////////////	
	public static void main(String[] args) throws IOException {

		int puerto = 0, nota = 0;	
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
		
		// Recibo mensaje de servidor
		RecepcionMensajes recepcion = new RecepcionMensajes(usuario,libreta);
		recepcion.setConnection(conexionSpread);
		
		// Cierro conexión SPREAD
		CerrarConexionSpread();
	}

}
