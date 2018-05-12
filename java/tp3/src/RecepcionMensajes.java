import java.net.InetAddress;
import java.net.UnknownHostException;

import spread.BasicMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class RecepcionMensajes implements Runnable, BasicMessageListener{

	private static SpreadConnection conexionSpread;
	static final String ALUMNO = "A";
	static final String PROFESOR = "P";	
	private static int libreta = 0;
	private static String usuario = null;	
	
	//Constructor
	public RecepcionMensajes(String usr, int LU) {
		this.usuario = usr;
		this.libreta = LU;
	}	
	
	public void setConnection(SpreadConnection conn){
		conexionSpread = conn;
	}
	
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

	@Override
	public void run() {
        SpreadMessage msgreceived = new SpreadMessage();
        
        boolean aguardando = true;
        
        while(aguardando){
        	try{
        		msgreceived = conexionSpread.receive();
        		messageReceived(msgreceived);
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        		System.exit(1);
        	}
        }		
	}

}
