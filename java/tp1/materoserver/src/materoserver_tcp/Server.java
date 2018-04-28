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
		
		System.out.println("Escuchando: "+ puerto);
		try {
			// Se bloquea hasta que recibe peticion del cliente
			// abriendo un socket para el cliente
			socketCliente = socketServidor.accept();
			System.out.println("Conexion aceptada: "+ socketCliente);
			
			//Establece canal de entrada
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			
			// Establece canal de salida
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())),true);
			
			Map<String, String> map = new HashMap<String, String>();	
			map.put("100", "9");
			map.put("200", "4");
			map.put("300", "7");
			
			String linea = null;
			
			while (true) {
				
				usuario = entrada.readLine();
				System.out.println("Usuario: "+ usuario);
				libreta = entrada.readLine();
				System.out.println("Libreta: "+ libreta);
				nota = entrada.readLine();
				System.out.println("Nota: "+ nota);
				
				System.out.println("Mensaje del cliente: "+ map.get(libreta));
				
				/*if (usuario.equals(ALUMNO)) {		
					salida.println(map.get(libreta));
				} else {
					salida.println(map.get(libreta));
				}*/
					
					
					
					
				//System.out.println("Mensaje del cliente: "+ linea);
				//salida.println(linea);
				
				linea = entrada.readLine();
				if (linea.equals("exit")) {
					break;
				}
				
				// Imprimo lo que llega del servidor
				//System.out.println("Mensaje cliente: "+ entrada.readLine());
				
				//salida.println(entrada.readLine());
				
				//if (entrada.readLine().equals("exit")) break;

				
			}
			
		} catch (IOException e) {
			 System.out.println("IOException: " + e.getMessage());	
		}
				
		salida.close();
	    entrada.close();
	    socketCliente.close();
	    socketServidor.close();
		
	    //System.out.println("Escuchando en puerto: "+ Integer.toString(puerto));
		
		

	}

}
