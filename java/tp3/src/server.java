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



public class server extends ServerProcess implements BasicMessageListener {

	static final String ALUMNO = "A";
	static final String PROFESOR = "P"; 
	static final String GRUPO_SERVIDOR = "GRUPO_SERVIDOR";
	public List<ServerProcess> processesList;
	public List<String> slavesList;
	public Queue<String> opsQueue;	
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
			conexionSpread.connect(InetAddress.getByName(ip), puerto, nombreCliente, false, true);
			
			// Unión a grupo
			SpreadGroup spreadGroup = new SpreadGroup();
			spreadGroup.join(conexionSpread, GRUPO_SERVIDOR);
			System.out.println("Uniendo a " + spreadGroup + ".");			
			System.out.println("Se ha establecido la conexión " + nombreCliente + " con " + ip + ":" + puerto);

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
   		    System.out.print("\tDebido a");

   		    if(info.isCausedByJoin()) {
				System.out.println("ingreso de " + info.getJoined());
				
			}	else if(info.isCausedByLeave()) {				
				System.out.println("salida de " + info.getLeft());
				
			}	else if(info.isCausedByDisconnect()) {
				System.out.println("desconexión de " + info.getDisconnected());
				
				String dado = info.getDisconnected() + "";
				String[] s = dado.split("#");				
				int IDx = Integer.parseInt(s[1].substring(7));
				
				System.out.println("Eliminar proceso: " + IDx);
				
				ServerProcess p = new ServerProcess(IDx, 0);
				removeProcess(p);
				election();
				System.out.print("\nLista de processos atual: \n\t");
				printProcessesList();
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
				
				
				
				
				
				if (this.isMaster){

					newInfo = dado + "#" + slaveID;

					System.out.println("Enviando a msg " + newInfo);
					sendMessage(newInfo, "SLAVE_GROUP");
					sendMessage("K#Arquivo deletado com sucessso.", "CLIENT_GROUP");
					
				} 

				
				
				
				
				String sender = msg.getSender().toString();
				String a[] = sender.split("#");
				sender = a[1];
				System.out.print("\nRecebida uma mensagem [REGULAR] ");
				System.out.println(" enviada por  " + sender + ".");
								
				//SpreadGroup groups[] = msg.getGroups();
				//System.out.println("para " + groups.length + " grupos.");
				
				byte data[] = msg.getData();
				String dado = new String(data);
				String newInfo = dado;
				//System.out.println("A informacao tem " + data.length + " bytes.");
				System.out.println("A mensagem eh: " + dado);
				String fileName;
				
				char letra = dado.charAt(0);
				//System.out.println("Letra = " + letra);
				int ID = 0;
				int PRIORITY = 0;
				
				String[] s = dado.split("#");
				
				if (letra == 'J' || letra == 'R') {
					ID = Integer.parseInt(s[1]);
					//System.out.println("ID = " + ID); 
					PRIORITY = Integer.parseInt(s[2]);
					//System.out.println("PRIORITY = " + PRIORITY);
				}
				
				String slave = "";
				int numReplicas = 1;
								
				// Envia a mensagem de retorno para os outros processos preencherem seus vetores de prioridades
				
				switch (letra){
				
				
					case 'J':
			
						// Monta a mensagem de resposta
						String info = new String();
						info = "R#" + id + "#" + Integer.toString(priority);
						
						sendMessage(info, groupName);
					
						break;
					
				
					case 'R': 
						// Adiciona o processo que respondeu Ã  lista de processos
						
						ServerProcess process = new ServerProcess(ID, PRIORITY);
						System.out.println("Adicionando o processo " + process + " na lista de processos.");
						
						addProcess(process);
						
						election();
						
		
						// Imprime a lista de processos
						System.out.print("\nLista de processos atual: \n\t");
						printProcessesList();
						
						/*for (ServerProcess p : this.processesList){
							System.out.print(p + " ");
						}
						*/
						
						//System.out.println("\nEu sou o master: " + this.isMaster);
						
						break;
						
					case 'S':
						
						// Monta a lista de slaves
						
						String novoSlave = s[1];
						slavesList.add(novoSlave);
					
						break;
					

					case 'C':
						// Processo Servidor Master trata a mensagem recebida do cliente
						// e envia a mensagem para criar o arquivo no grupo de processos Slave
						
						fileName = s[1];
						numReplicas = Integer.parseInt(s[2]);
						// Limita o nÃºmero de rÃ©plicas ao nÃºmero de Slaves (storages)
						if (numReplicas > slavesList.size())
							numReplicas = slavesList.size();
						
						boolean arquivoExiste = false;
						
						// Verifica se o arquivo jÃ¡ estÃ¡ na lista de arquivos criados
						for (String f : storedFilesList) {
							
							String fi[] = f.split("#");
							
							
							if (fi[0].equals(fileName)){
								// Arquivo solicitado para criaÃ§Ã£o jÃ¡ foi criado anteriormente
								
								String resp = "E#Arquivo jÃ¡ existe no ServiÃ§o de Arquivos#" + sender;
								System.out.println(resp);
								if (this.isMaster)
									sendMessage(resp, "CLIENT_GROUP");
								
								arquivoExiste = true;
								
								break;
							}
								
						}
						
						if (!arquivoExiste) {
							
							//sendMessage("E#Arquivo nÃ£o existe", "CLIENT_GROUP");

							for (int i = 0; i < numReplicas; i++){

								// Seleciona o Slave da vez que vai armazenar o arquivo - Balanceamento por rounding robin
								String currentSlave = getNextSlave();
								newInfo = dado + "#" + sender + "#" + currentSlave;
								String operation = newInfo;
								System.out.println("Slave da vez: " + currentSlave);

								// Insere a operaÃ§Ã£o no inÃ­cio da fila
								opsQueue.offer(operation);

								String currentOp;

								currentOp = opsQueue.peek();


								if (this.isMaster){


									System.out.println("Enviando a solicitaÃ§Ã£o para criar o arquivo: " + fileName + " no SLAVE_GROUP");

									System.out.println("Enviando mensagem: <" + currentOp + ">");

									sendMessage(currentOp, "SLAVE_GROUP");
									
									//inicia o timer com 5 segundos
									
									//aguardaResposta();
									
									//if (timeout) {
									// sendMessage("ServiÃ§o de Arquivos indisponÃ­vel", "CLIENT_GROUP");
									
									opsQueue.poll();

								}
							}
							if (this.isMaster)
								sendMessage("K#Arquivo criado com sucesso", "CLIENT_GROUP");

						} else
							if (this.isMaster)
								sendMessage("E#Arquivo jÃ¡ exite no ServiÃ§o de Arquivos", "CLIENT_GROUP");
						
						break;

					case 'K':
						
						// Processo Servidor Master trata a mensagem recebida do SLAVE_GROUP
						// e envia a mensagem para o cliente informando que o arquivo foi criado

						// Insere o registro do arquivo na lista de arquivos armazenados
						if (!s[1].equals("Arquivo deletado com sucesso.") && !s[1].equals("Arquivo editado com sucesso."))
						      storedFilesList.add(dado.substring(2));
						
						System.out.println("\nLista de arquivos armazenados: ");
						for (String f : storedFilesList)
							System.out.println(f);

						if (this.isMaster){
							
							System.out.println("Enviando a resposta para o CLIENT_GROUP");
							
							System.out.println("Enviando mensagem: <" + newInfo + ">");
							
							//sendMessage(newInfo, "CLIENT_GROUP");
							
						}

						// Remove a operaÃ§Ã£o do inÃ­cio da fila
						opsQueue.poll();

						
						break;
						
					case 'L': 
						// Mensagem para ler o arquivo



						fileName = s[1];

						System.out.println("Lendo o arquivo " + fileName);

						slave = "";

						if (this.isMaster){
							
							if (arquivoExiste(fileName)) {

								String slaves = getSlaves(fileName);
								
								slave = slaves.split("#")[1];
								
								// Insere a operaÃ§Ã£o no inÃ­cio da fila
								opsQueue.offer(dado);

								newInfo = dado + "#" + slave;

								System.out.println("Enviando a msg " + newInfo);
								sendMessage(newInfo, "SLAVE_GROUP");
							} else {

								sendMessage("E#Arquivo nÃ£o existe", "CLIENT_GROUP");

							}

						}


						break;
						
					case 'l':
						
						// Processo Servidor Master trata a mensagem recebida do SLAVE_GROUP
						// e envia a mensagem para o cliente com a leitura do arquivo

						if (this.isMaster){
							
							System.out.println("Enviando a resposta para o CLIENT_GROUP");
							
							System.out.println("Enviando mensagem: <" + newInfo + ">");
							
							sendMessage(newInfo, "CLIENT_GROUP");

							// Remove a operaÃ§Ã£o do inÃ­cio da fila
							opsQueue.remove();
							
						}


						
						break;
						
					case 'D': 
						// Mensagem para deletar o arquivo
						
						List<String> slavesComArquivosD = new ArrayList<>();

						fileName = s[1];

						System.out.println("Deletando o arquivo " + fileName);

						List<String> storedFilesListAux = new ArrayList<String>(storedFilesList);
						
						arquivoExiste = false;
						// Verifica se o arquivo jÃ¡ estÃ¡ na lista de arquivos criados
						for (String f : storedFilesListAux) {

							String fi[] = f.split("#");


							if (fi[0].equals(fileName)){
								// Arquivo que se quer deletar foi encontrado na lista de arquivos armazenados

								System.out.println("Arquivo " + fileName + " encontrado no slave " + fi[1]);
								slavesComArquivosD.add(fi[1]);

								arquivoExiste = true;

								storedFilesList.remove(f);
								System.out.println("Excluindo o arquivo " + fileName + " do slave " + fi[1]);
								System.out.println();
								

							}

						}

						if (arquivoExiste) {

							for (String slaveID : slavesComArquivosD) {
								// Insere a operaÃ§Ã£o no inÃ­cio da fila

								opsQueue.offer(dado);

								if (this.isMaster){

									newInfo = dado + "#" + slaveID;

									System.out.println("Enviando a msg " + newInfo);
									sendMessage(newInfo, "SLAVE_GROUP");
									
								} 
							}
							if (this.isMaster)
								sendMessage("K#Arquivo deletado com sucessso.", "CLIENT_GROUP");

						} else {

							System.out.println("Arquivo nÃ£o existe");
							if (this.isMaster)
								sendMessage("E#Arquivo nÃ£o existe", "CLIENT_GROUP");

						}

						break;
						
					case 'W': 
						// Mensagem para editar o arquivo
						
						List<String> slavesComArquivosW = new ArrayList<>();

						fileName = s[1];

						System.out.println("Enviando msg para editar o arquivo " + fileName);

						arquivoExiste = false;
						// Verifica se o arquivo jÃ¡ estÃ¡ na lista de arquivos criados
						for (String f : storedFilesList) {

							String fi[] = f.split("#");


							if (fi[0].equals(fileName)){
								// Arquivo que se quer deletar foi encontrado na lista de arquivos armazenados

								slavesComArquivosW.add(fi[1]);
								arquivoExiste = true;

							}

						}

						if (arquivoExiste) {
							
							if (this.isMaster)
								sendMessage("K#Arquivo editado com sucesso.", "CLIENT_GROUP");

							for (String slaveID : slavesComArquivosW) {
								// Insere a operaÃ§Ã£o no inÃ­cio da fila
								opsQueue.offer(dado);

								if (this.isMaster){

									newInfo = dado + "#" + slaveID;

									System.out.println("Enviando a msg " + newInfo);
									sendMessage(newInfo, "SLAVE_GROUP");

								} 
								
							}

							} else {
								if (this.isMaster)
									sendMessage("E#Arquivo nÃ£o existe", "CLIENT_GROUP");

							}
						

						break;
						
					default: 
						
						break;
				
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
    /// GET BACKUPS
    ///////////////////////////////////	
	private String getBackups(String fName) {
		
		// Verifica se o arquivo jÃ¡ estÃ¡ na lista de arquivos criados
		
		String listaDeSlaves = fName;
		
		for (String f : storedFilesList) {
			
			String fi[] = f.split("#");
			
			if (fi[0].equals(fName)) {
								
				listaDeSlaves += "#" + fi[1];
				
			}
				
		}
		return listaDeSlaves;

	}

	///////////////////////////////////
    /// SEND MESSAGE
    ///////////////////////////////////	
	private void sendMessage(String info, String groupName) {
		
		SpreadMessage msg = new SpreadMessage();
		msg.setSafe();
		msg.addGroup(groupName);

		msg.setData(info.getBytes());
		// Envia a mensagem
		try {
			connection.multicast(msg);
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
		super(Integer.parseInt(nombreServidor.substring(7)));
		this.turnPrimary(false);
		this.processesList = new ArrayList<ServerProcess>();
		this.opsQueue = new LinkedList<String>();
		
		// Inicializa servidor BACKUP
		this.slavesList = new ArrayList<>();		
		backupActual = -1;
				
		ConexionSpread(nombreServidor, ip, puerto);
	}
	
	
	public void turnPrimary(boolean b) {
		this.esPrimario = b;
	}
	
	public ServerProcess getPrimary() {
		
		List<ServerProcess> list = this.processesList;
		int lastIndex = list.size() - 1;

		return list.get(lastIndex);
		
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

	}
	
}
