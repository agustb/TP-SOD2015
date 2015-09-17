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

int main(int argc, char* argv[])
{
	int puerto = 3000;
	char Cadena[100];	

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
	int socket_servidor;
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
	int socket_retorno;	
	
	printf("Servidor a la espera de consultas. Escuchando en puerto %d\n", puerto);	
	printf("Presione q para salir.\n");	

	char q;	
	// while( (q = getchar() ) != 'q')
	// {
	
		// ** LEE SOCKET CLIENTE ------------------------------------------------------------------------------------	
		// 1. Crea el socket del cliente y queda esperando que se conecte.
		long_cliente = sizeof(cliente);
		socket_retorno = accept(socket_servidor, (struct sockaddr *) &cliente, &long_cliente);
		if (socket_retorno == -1)
		{
			//close(socket_servidor);
			printf ("No se puede abrir socket de cliente\n");
			return -1;
		}
		
		// 2. Una vez conectado el cliente, lee la cadena enviada e imprime resultado.		
		struct PaqueteMatero paquete_matero;
		
		char *mensaje = NULL;
		int identificador;
		
		LeeMensaje (socket_retorno, &identificador, &mensaje);
		
		switch (identificador) 
		{ 
			case idPaqueteMatero: 
			{ 
				PaqueteMatero *paquete_matero = NULL; 
				paquete_matero = (PaqueteMatero *)mensaje; 
				printf ("Soy Servidor, he recibido modo: %s\n", mensaje.modo);		
				printf ("Soy Servidor, he recibido LU: %s\n", mensaje.LU);
				printf ("Soy Servidor, he recibido Nota: %s\n", mensaje.Nota);
				break; 
			} 
		} 

		close(socket_retorno);
		
		/* Se libera el mensaje  cuando ya no lo necesitamos */ 
		if (mensaje != NULL) 
		{ 
			free (mensaje); 
			mensaje = NULL; 
		}		

//	}
		
	// ** ESCRIBE SOCKET CLIENTE ---------------------------------------------------------------------------------
	strcpy (Cadena, "Adios");	
	EscribeMensaje (socket_retorno, idCadena, (char *)&Cadena, sizeof(Cadena));	
	
	// FINALIZA ------------------------------------------------------------------------------------------------
	close(socket_retorno);
	close(socket_servidor);
	printf("===================================================\n");	
	return 0;
}
