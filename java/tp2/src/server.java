
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class server {

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
		
		Map<String, String> map = new HashMap<String, String>();	
		map.put("100", "9");
		map.put("200", "4");
		map.put("300", "7");

		String linea = null;
		

			
		} catch (IOException e) {
			 System.out.println("IOException: " + e.getMessage());	
		}

	}
}