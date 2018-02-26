package materoserver_tcp;

import java.io.*;
import java.net.*;

import javax.sound.midi.Soundbank;

public class Server {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	
	public static void main(String[] args)  throws IOException {
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
			
			while (true) {
				// Imprimo lo que llega del servidor
				System.out.println("Mensaje cliente: "+ entrada.readLine());

				//
			}
			
		} catch (IOException e) {
			
		}
		
		
		
		//System.out.println("Escuchando en puerto: "+ Integer.toString(puerto));
		
		

	}

}
