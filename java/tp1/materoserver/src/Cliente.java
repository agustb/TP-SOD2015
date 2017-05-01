import java.io.*;
import java.net.*;

public class Cliente {
	
	private static String HOST = "localhost";
	private static int PUERTO = 2017;
	
	public static void main (String args[]) {
		Socket socket;
		DataOutputStream mensaje;
		
		try{
			// Creo socket
			socket = new Socket(HOST,PUERTO);
			mensaje = new DataOutputStream(socket.getOutputStream());
			
			// Envio mensaje
			mensaje = writeUTF("Hola soy un cliente!");
			
			// Cerramos la conexion
			socket.close();
			
		} catch (UnknownHostException e){
			System.out.println("El host no existe o no esta activo.");
		} catch (IOException e){
			System.out.println("Error de entrada/salida.");
		}		
	}

}
