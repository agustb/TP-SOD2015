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
		LeerSocket (socket_retorno, Cadena, sizeof(Cadena));
		printf ("Soy Servidor, he recibido : %s\n", Cadena);
		
		close(socket_retorno);

//	}
		
	// int Leido = 0;
	// int Aux = 0;
	// int largo_cadena_lectura = sizeof(Cadena);

	// while (Leido < largo_cadena_lectura)
	// {
		// Aux = read (socket_retorno, Cadena + Leido, largo_cadena_lectura - Leido);
		// if (Aux > 0)
		// {
			// Leido = Leido + Aux;
		// }
		// else
		// {
			// if (Aux == 0) 
				// break;
			// if (Aux == -1)
			// {
				// switch (errno)
				// {
					// case EINTR:
					// case EAGAIN:
						// usleep (100);
						// break;
					// default:
						// return -1;
				// }
			// }
		// }
	// }

	
	// ** ESCRIBE SOCKET CLIENTE ---------------------------------------------------------------------------------
	strcpy (Cadena, "Adios");
	EscribirSocket (socket_retorno, Cadena, sizeof(Cadena));
	// int largo_cadena_escritura = sizeof(Cadena);
	// int Escrito = 0;
	// Aux = 0;

	// while (Escrito < largo_cadena_escritura)
	// {
		// Aux = write (socket_retorno, Cadena + Escrito, largo_cadena_escritura - Escrito);
		// if (Aux > 0)
		// {
			// Escrito = Escrito + Aux;
		// }
		// else
		// {
			// if (Aux == 0)
				// break;
			// else
				// return -1;
		// }
	// }	
	
	
	// FINALIZA ------------------------------------------------------------------------------------------------
	close(socket_retorno);
	close(socket_servidor);
	printf("===================================================\n");	
	return 0;
}
