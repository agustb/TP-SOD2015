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

int main(int argc, char* argv[]) // prueba para actualización de GIT
{
	char *modo = NULL;
	char *ip_server;
	int puerto = 3000;
	int lu = 123;
	int nota = 9;
	char Cadena[100];
	
	system("clear"); 
	
	printf("\n");
	printf("===================================================\n");
	printf("== BIENVENIDO A MATERO ============================\n");
	printf("== Modo: CLIENTE ==================================\n");	
	printf("===================================================\n");
	printf("\n");

	printf("Parametros de entrada:\n");
	printf("- TOTAL: %d \n",argc);
	printf("	- Param 0: %s \n",argv[0]);
	printf("	- Param 1: %s \n",argv[1]);
	printf("	- Param 2: %s \n",argv[2]);
	printf("	- Param 3: %s \n",argv[3]);
	printf("	- Param 4: %s \n",argv[4]);
	printf("	- Param 5: %s \n",argv[5]);
		
	// Chequea los parámetros de ingreso.
	if (chequea_parametros_cliente(argc, argv, &ip_server, &modo, &puerto, &lu, &nota) < 0)
	{
		return -1;
	}

	// Crear socket
	int socket_tcp;
	
	struct sockaddr_in direccion;		
	direccion.sin_family = AF_INET;
	direccion.sin_addr.s_addr = inet_addr(ip_server); //inet_addr("192.168.1.103");	
	direccion.sin_port = htons(puerto);

	socket_tcp = socket (AF_INET, SOCK_STREAM, 0);
	if (socket_tcp == -1)
	{
		printf("Error, no se pudo crear el socket\n");
		return -1;
	}
	
	if (connect (socket_tcp,(struct sockaddr *)&direccion, sizeof (direccion)) == -1)
	{
		printf("No puedo establecer conexion con el servidor\n");
		return -1;
	}
		
	// ESCRIBE SOCKET CLIENTE ----------------------------------------------------------------------------
	struct PaqueteMatero paquete_matero;
			
	paquete_matero.modo = *modo;
	paquete_matero.LU = lu;
	paquete_matero.Nota = nota;
	
	EscribeMensaje (socket_tcp, idPaqueteMatero, (char *)&paquete_matero, sizeof(paquete_matero));
	
	
	// LEE SOCKET CLIENTE -------------------------------------------------------------------------------
	//LeerSocket (socket_tcp, Cadena, sizeof(Cadena));
		
	char *mensaje = NULL;
	int identificador;
	
	LeeMensaje (socket_tcp, &identificador, &mensaje);
	
	switch (identificador) 
	{ 
		case idCadena: 
		{ 
			printf ("Soy cliente, He recibido : %s\n", Cadena); 
			break; 
		} 
	}

	// FINALIZA -----------------------------------------------------------------------------------------	
	close(socket_tcp);
	printf("===================================================\n");	
	return 0;
}

