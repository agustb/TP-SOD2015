import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import spread.*;



public class server implements BasicMessageListener {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	static final String GRUPO_CLIENTE = "GRUPO_CLIENTE";
	static final String GRUPO_SERVIDOR = "GRUPO_SERVIDOR";
	private boolean esPrimario;
	private int backupActual;
	private static SpreadConnection conexionSpread;
	private static String userName;
	private SpreadGroup spreadGroup;
	private static int libreta = 0;
	private static int nota = 0;
	private static String usuario = null;
	private static Map<String, String> map = new HashMap<String, String>();
	
	
	///////////////////////////////////
    /// CONEXION SPREAD
    ///////////////////////////////////
	private static void ConexionSpread(String nombreservidor, String ip, int puerto) {
	
		try
		{
			// Conecto
			conexionSpread = new SpreadConnection();
			conexionSpread.connect(InetAddress.getByName(ip), puerto, nombreservidor, false, true);
			
			// Unión a grupo
			SpreadGroup spreadGroup = new SpreadGroup();
			spreadGroup.join(conexionSpread, GRUPO_SERVIDOR);
			System.out.println("Uniendo a " + spreadGroup + ".");			
			System.out.println("Se ha establecido la conexión " + nombreservidor + " con " + ip + ":" + puerto);

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
    /// Override MESSAGERECEIVED
    ///////////////////////////////////
	@Override
	public void messageReceived(SpreadMessage message) {
		resolveMessage(message);		
	}
	
	
	///////////////////////////////////
    /// HANDLE MEMBERSHIP INFO
    ///////////////////////////////////
	private void handleMembershipInfo(MembershipInfo info) 
	{
		if(info.isRegularMembership()) {
   		    if(info.isCausedByDisconnect()) {
				turnPrimary(true);
			}
		}
	}
	
	///////////////////////////////////
    /// RESOLVE MESSAGE
    ///////////////////////////////////	
	private void resolveMessage(SpreadMessage msg)
	{
		try
		{
			if(msg.isRegular())
			{
			
				byte data[] = msg.getData();
				String mensajeCliente = new String(data);
				String respuesta = new String();
				
				char letra = mensajeCliente.charAt(0);
				if (letra == 'U') {
					usuario = mensajeCliente;
				} 
				if (letra == 'L') {
					libreta = Integer.parseInt(mensajeCliente);
				} 
				if (letra == 'N') {
					nota = Integer.parseInt(mensajeCliente);
				}
				
				
				if (usuario.equals(ALUMNO)) {		
					/*ALUMNO*/
					respuesta = map.get(libreta);
				} else {
					/*PROFESOR*/
					try {
						map.put(Integer.toString(libreta), Integer.toString(nota));
						respuesta = "0";
					} catch (Exception e) {
						respuesta = "-1";
					}
				}
				
				if (this.esPrimario) {					
					sendMessage(respuesta, GRUPO_CLIENTE);
				}
				
			}
			else if (msg.isMembership())
			{
				System.out.println("\nRecepción de mensaje [DE GRUPO]: ");
				MembershipInfo info = msg.getMembershipInfo();
				handleMembershipInfo(info);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	///////////////////////////////////
    /// SEND MESSAGE
    ///////////////////////////////////	
	private void sendMessage(String info, String groupName) {
		
		SpreadMessage msg = new SpreadMessage();
		msg.setSafe();
		msg.addGroup(groupName);

		msg.setData(info.getBytes());
		try {
			conexionSpread.multicast(msg);
		} catch (SpreadException e) {
			e.printStackTrace();
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
    /// CONSTRUCTOR
    ///////////////////////////////////
	public server(String nombreServidor, String ip, int puerto)
	{
		this.turnPrimary(false);
		backupActual = -1;
				
		ConexionSpread(nombreServidor, ip, puerto);
	}
	

	///////////////////////////////////
    /// TURN PRIMARY
    ///////////////////////////////////
	public void turnPrimary(boolean b) {
		this.esPrimario = b;
	}
	

	///////////////////////////////////
    /// MAIN
    ///////////////////////////////////	
	public static void main(String[] args) {
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

		////////////////////////
		map.put("100", "9");
		map.put("200", "4");
		map.put("300", "7");		
		
		String nombreServidor = "mm_servidor";
		String ip = "127.0.0.1";
		new server(nombreServidor, ip, puerto);
		
		// Cierro conexión SPREAD
		CerrarConexionSpread();
	}
	
}
