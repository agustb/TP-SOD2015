package materoserver_tcp;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	
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
	}
}
