package materocliente_tcp;

import java.net.*;
import java.io.*;

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

		Socket socketCliente = null;
		BufferedReader entrada = null;
		PrintWriter salida = null;
		
		// Create socket		
		try {
			socketCliente = new Socket("localhost",puerto);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())),true);
		} catch (IOException e) {
			System.err.println("No puede establer canales de E/S para la conexión");
			System.exit(-1);
		}
		
		BufferedReader stdIn =
				new BufferedReader(new InputStreamReader(System.in));
		
		String linea;
		
		try {
			
			// USUARIO
			linea = usuario;
			salida.println(linea);
			
			//LU
			linea = Integer.toString(libreta);
			salida.println(linea);
			
			//Nota
			linea = Integer.toString(nota);
			salida.println(linea);
			
			// RESPUESTA DEL SERVIDOR
			linea = entrada.readLine();
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
			
			/*while(true) {
				// leo la entrada del usuario
				linea = stdIn.readLine();
				
				// mando al server el mensaje que escribo
				salida.println(linea);
				
				// leo la respuesta del servidor
				linea = entrada.readLine();
				
				System.out.println("Respuesta del servidor: "+ linea);
				
				if (linea.equals("exit")) {
					break;
				}
			}*/
				
			
			// Send user data to server		
			//salida.println(usuario);
			
			//Receive server response
			//System.out.println("Respuesta servidor: "+ entrada.readLine());
			
			//salida.println(libreta);
			//Receive server response
			//System.out.println("Respuesta servidor: "+ entrada.readLine());
			
			//salida.println(nota);
			//Receive server response
			//System.out.println("Respuesta servidor: "+ entrada.readLine());	
			
			//if (stdIn.readLine().equals("exit"));
							 
		} catch (IOException e) {
			System.out.println("IOException: "+ e.getMessage());
		}
		
		// Free resources
		salida.close();
		entrada.close();
		socketCliente.close();
		
/*		System.out.println("Escuchando en puerto: "+ Integer.toString(puerto));
		if (usuario == ALUMNO)
			System.out.println("El usuario es ALUMNO");
		else
			System.out.println("El usuario es PROFESOR");			
		System.out.println("Libreta universitaria: "+ Integer.toString(libreta));
		System.out.println("Calificacion: "+ Integer.toString(nota));		
*/
	}

}
