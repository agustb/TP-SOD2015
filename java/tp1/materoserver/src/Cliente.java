import java.net.*;
import java.io.*;

public class Cliente {
	
	private static String HOST = "localhost";
	private static int PUERTO = 2017;
	
	private static boolean parseParameters(String args[]){
		
		
			
		
		return false;
	}
	
	
	public static void main (String args[]) {
		 
		try{
			// Creo socket
			Socket  socket = new Socket(HOST,PUERTO);
			DataOutputStream mensaje = new DataOutputStream(socket.getOutputStream());
			
			// Envio mensaje
			mensaje.writeUTF("Hola soy un cliente!");
			
			// Cerramos la conexion
			socket.close();
			
		} catch (UnknownHostException e){
			System.out.println("El host no existe o no esta activo. "+e.getMessage());
		} catch (IOException e){
			System.out.println("Error de entrada/salida. "+e.getMessage());
		}		
	}

}
