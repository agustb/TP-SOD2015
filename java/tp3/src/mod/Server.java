package mod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import spread.*;

public class Server {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	static final String GRUPO_SERVIDOR = "GRUPO_SERVIDOR";
	private static SpreadConnection conexionSpread;
	private static boolean esPrimario;
	
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
	
	private static void evaluarMensaje(SpreadMessage msg) {
		try {
			if (msg.isRegular()) {
				// escritura?
			} 
			else if (msg.isMembership()) {
				MembershipInfo info = msg.getMembershipInfo();
				if (info.isCausedByDisconnect()) {
					esPrimario = true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
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
		ServerSocket socketServidor = null;
		try {
			socketServidor = new ServerSocket(puerto);
			
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: "+ puerto);
			System.exit(-1);
		}
		
		// Inicializa y hace JOIN de SPREAD
		SpreadMessage msg;
		SpreadMessage msgreceived = new SpreadMessage();
		
		String nombreServidor = "mm_servidor";
		String ip = "192.168.1.100";
		try
		{
			// Conecto
			conexionSpread = new SpreadConnection();
			conexionSpread.connect(InetAddress.getByName(ip), puerto, nombreServidor, false, true);
			
			// JOIN a grupo
			SpreadGroup grupoSpread = new SpreadGroup();
			grupoSpread.join(conexionSpread, GRUPO_SERVIDOR);

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
		
		// Defino PRIMARY/BACKUP
		int cantServer = 1; // falta resolver
		esPrimario = (cantServer == 1);
		
		// Si es BACKUP
		if (!esPrimario) {
			while (true) {
				try{
					msgreceived = conexionSpread.receive();
					try {
						if (msg.isRegular()) {
							// escritura?
						} 
						else if (msg.isMembership()) {
							MembershipInfo info = msg.getMembershipInfo();
							if (info.isCausedByDisconnect()) {
								esPrimario = true;
								break;
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.exit(1);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}				
			}
		}
		
		// Si es PRIMARIO
		do {
			try {
				msgreceived = conexionSpread.receive();
				cantServer = msgreceived.getGroups().length; 
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}			
		} while (cantServer > 1);				
		
		////////////////////////////////////////////////
		Socket socketCliente = null;
		BufferedReader entrada = null;
		PrintWriter salida = null;

		BufferedReader stdIn =
				new BufferedReader(new InputStreamReader(System.in));
		
		Map<String, String> map = new HashMap<String, String>();	
		map.put("100", "9");
		map.put("200", "4");
		map.put("300", "7");

		String linea = null;
		
		System.out.println("Escuchando: "+ puerto);
		try {
			while (true) {
				// Se bloquea hasta que recibe peticion del cliente
				// abriendo un socket para el cliente
				socketCliente = socketServidor.accept();
				System.out.println("Conexion aceptada: "+ socketCliente);
				
				//Establece canal de entrada
				entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
				
				// Establece canal de salida
				salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())),true);
					
				usuario = entrada.readLine();
				System.out.println("Usuario: "+ usuario);
				libreta = entrada.readLine();
				System.out.println("Libreta: "+ libreta);
				nota = entrada.readLine();
				System.out.println("Nota: "+ nota);
				
				if (usuario.equals(ALUMNO)) {		
					/*ALUMNO*/
					salida.println(map.get(libreta));
				} else {
					/*PROFESOR*/
					try {
						map.put(libreta, nota);
						salida.println("0");
					} catch (Exception e) {
						salida.println("-1");
					}
				}
				
				linea = stdIn.readLine();
				if (linea.equals("exit")) {
					break;
				}
			}
			
		} catch (IOException e) {
			 System.out.println("IOException: " + e.getMessage());	
		}
				
		salida.close();
	    entrada.close();
	    socketCliente.close();
	    socketServidor.close();
	    CerrarConexionSpread();
	}
}
