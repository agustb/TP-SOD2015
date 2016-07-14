#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include "resources.c"
//--------------------
#include <unistd.h>
#include <errno.h>
//--------------------

void error(const char *msg) {
    perror(msg);
	exit(1); 
}

int main(int argc, char* argv[])
{
	// Variables
	int n, socket_servidor, socket_retorno, puerto = 3000;
	char lModo[3],lLu[4],lNota[1];
	int long_Modo;	

	system("clear"); 
		
	printf("\n");
	printf("===================================================\n");
	printf("== BIENVENIDO A MATERO ============================\n");
	printf("== Modo: SERVIDOR =================================\n");	
	printf("===================================================\n");
	printf("\n");

	// Chequea que los parámetros ingresados sean correctos con los utilizados.
	if (chequea_parametros_servidor(argc, argv, &puerto)<0)
	    return -1;

	// SOCKET SERVIDOR ------------------------------------------------------------------------------		
	// Crea el socket del servidor.
	//int socket_servidor; -- declarado en seccion VARIABLES
	socket_servidor = socket (AF_INET, SOCK_STREAM, 0);	
	if (socket_servidor == -1)
	{
		printf("Error, no se pudo crear el socket\n");
		return -1;
	}
	
	// Estructura direccion
	struct sockaddr_in direccion;
	direccion.sin_family = AF_INET;
	direccion.sin_port = htons(puerto);
	direccion.sin_addr.s_addr = INADDR_ANY;
	
	// BIND
	if (bind (socket_servidor, (struct sockaddr *)&direccion, sizeof (direccion)) == -1)
	{
		close (socket_servidor);
		printf("Error, al intentar realizar el bind\n");
		return -1;
	}
	
	// LISTEN
	if (listen (socket_servidor, 1) == -1)
	{
		close (socket_servidor);
		printf("Error, no pudo escuchar\n");
		return -1;
	}

	// SOCKET RETORNO --------------------------------------------------------------------------------------	

	socklen_t long_cliente;	
	struct sockaddr_in cliente;
	//int socket_retorno; -- declarado en seccion VARIABLES	
	
	printf("Servidor a la espera de consultas. Escuchando en puerto %d\n", puerto);	
	//printf("Presione q para salir.\n");	



	// ** LEE SOCKET CLIENTE ------------------------------------------------------------------------------------	
	// 1. Crea el socket del cliente y queda esperando que se conecte.
	long_cliente = sizeof(cliente);
	// socket_retorno se crea cuando server acepta a cliente
	socket_retorno = accept(socket_servidor, (struct sockaddr *) &cliente, &long_cliente); 
	if (socket_retorno == -1)
	{
		//close(socket_servidor);
		printf ("No se puede abrir socket de cliente\n");
		return -1;
	}
	
	// 2. Una vez conectado el cliente, lee la cadena enviada e imprime resultado.		
	n = read(socket_retorno,lModo,3);
	if (n<0){
		error("ERROR al leer el socket");
	}
	printf("Mensaje recibido: %s\n",lModo);

	// Devuelvo mensaje al cliente
	n = write(socket_retorno,"Tengo el mensaje",18);
	if (n<0){
		error("ERROR al escribir en el socket");
	}

		
	
	// FINALIZA ------------------------------------------------------------------------------------------------
	close(socket_retorno);
	close(socket_servidor);
	printf("===================================================\n");	
	return 0;
}
