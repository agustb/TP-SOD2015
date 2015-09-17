#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>

int chequea_parametros_cliente (int argc, char* argv[], char **ip, char **modo, int *puerto, int *lu, int *nota)
{

	if (argc < 4) // si no llegan parámetros produce error y muestra el siguiente mensaje.
	{
		printf ("Error en parametros de entrada para %s\n-> Cadena valida: [-p <port>] [-A|-P <Modo Alumno|Profesor>] [lu <Libreta Universitaria>] [nota (solo modo Profesor) <Calificacion>]\n", argv[0]);
		return -1;
	}
	
	*ip= argv[1];	

	// Controlo que el parámetro de Modo sea -A o -P. Sino muestra error.
	*modo = argv[3];
	if (strcmp(*modo, "-A") != 0 && strcmp(*modo, "-P") != 0)
    {
        printf ("Error. Los modos permitidos son: [-A] (si ingresa como ALUMNO) o [-P] (si ingresa como PROFESOR).\n");
		printf ("Verifique que las letras esten en MAYUSCULAS.\n");
        return -1;
    }

	// analizo parametros con getopt
	int c;
	while ((c = getopt (argc, argv, "p:l:n::A::P::")) != -1)
	{
		switch (c)
	    {
	    	case 'p': // puerto
	       	{
			   *puerto = atoi(optarg);
			   if (*puerto <2000 || *puerto > 30000)
			   {	
			   		printf ("El puerto debe estar comprendido entre 2000 y 30000\n");
					return -1;
			   }
			   break;
	       	}

			case 'l': // libreta universitaria
	       	{
			   *lu = atoi(optarg);
			   if (*lu < 0)
			   {
			   		printf ("El código de Libreta Universitaria no puede ser negativo.\n");
					return -1;
			   }
			   break;
		   	}

			case 'n': // nota.
	       	{
			   *nota = atoi(optarg);
			   if (*nota < 0 || *nota > 10)
			   {
					printf ("El calificacion del alumno no puede ser negativa o mayor a 10.\n");
					return -1;
			   }				   
			   break;
		   	}

			case 'A':
			{
				// Capturo el parámetro -A
				break;
			}				
			
			case 'P':
			{
				// Capturo el parámetro -P
				break;
			}				
			
			case '?':
			{
				// Capturo el parámetro -A|-P
			}				
		}
	}
	return 0;
}

int chequea_parametros_servidor(int argc, char* argv[], int *puerto)
{
	int c;
	while ((c= getopt(argc, argv, "p::")) != -1)
	{
		switch(c)
		{
			case 'p':
			{
				*puerto = atoi(optarg);
				if (*puerto < 1024 || *puerto > 65535)
				{
					printf ("El puerto debe estar comprendido entre 1024 y 65535\n");
					return -1;
				}
				break;
			}			
		}
	}
    return 0;
}

int LeerSocket (int fd, char *Datos, int Longitud)
{
	int Leido = 0;
	int Aux = 0;

	if ((fd == -1) || (Datos == NULL) || (Longitud < 1))
		return -1;
	while (Leido < Longitud)
	{
		Aux = read (fd, Datos + Leido, Longitud - Leido);
		if (Aux > 0)
		{
			Leido = Leido + Aux;
		}
		else
		{
			if (Aux == 0) 
				return Leido;
			if (Aux == -1)
			{
				switch (errno)
				{
					case EINTR:
					case EAGAIN:
						usleep (100);
						break;
					default:
						return -1;
				}
			}
		}
	}
	return Leido;
}

int EscribirSocket (int fd, char *Datos, int Longitud)
{
	int Escrito = 0;
	int Aux = 0;

	if ((fd == -1) || (Datos == NULL) || (Longitud < 1))
		return -1;

	while (Escrito < Longitud)
	{
		Aux = write (fd, Datos + Escrito, Longitud - Escrito);
		if (Aux > 0)
		{
			Escrito = Escrito + Aux;
		}
		else
		{
			if (Aux == 0)
				return Escrito;
			else
				return -1;
		}
	}
	return Escrito;
}

typedef struct Cabecera 
{ 
    int identificador;
	int longitud;
} Cabecera;

typedef enum 
{ 
    idPaqueteMatero, 
    idCadena 
} Identificadores;

void EscribeMensaje (int socket, int idMensaje, char *mensaje, int tamanio)
{
    /* Se declara y rellena la cabecera */ 
    Cabecera cabecera; 
    cabecera.identificador = idMensaje; 
	cabecera.longitud = tamanio;

    /* Se envía la cabecera */ 
    write (socket, &cabecera, sizeof(cabecera)); 

    /* Si el mensaje no tiene cuerpo, hemos terminado */ 
    if ((mensaje == NULL) || (tamanio == 0)) 
        return; 

    /* Se envía el cuerpo */ 
    write (socket, mensaje, tamanio);	
}

void LeeMensaje (int socket, int *idMensaje, char **mensaje)
{
	Cabecera cabecera; 
	*mensaje = NULL;
	
    read (socket, &cabecera, sizeof(cabecera)); /* Se lee la cabecera */ 

    /* Rellenamos el identificador para devolverlo */ 
    *idMensaje = cabecera.identificador; 

    /* Si hay que leer una estructura detrás */ 
    if (cabecera.longitud > 0) 
    { 
        *mensaje = (char *)malloc (cabecera.longitud);  /* Se reserva espacio para leer el mensaje */ 
        read (socket, *mensaje, cabecera.longitud); 
    }
}

struct PaqueteMatero {
    char* modo;
    int LU;
    int Nota;
};

struct PaqueteMatero arrayNotas[20];

/*void IncializaArrayNotas()
{
	int low = 0;
	int high = (sizeof(arrayNotas) - 1);
	
	for (low=0;low<=high;low++)
	{
		arrayNotas[low].modo = "";
		arrayNotas[low].LU = 0;
		arrayNotas[low].Nota = 0;		
	}	
}

int IndiceLibretaUniversitaria(int LU)
{
	int low = 0;
	int high = (sizeof(arrayNotas) - 1);

	for (low=0;low<=high;low++)
	{
		if (LU == arrayNotas[low].Nota)
		{
			return low;
		}
	}
	return -1;	
}

int BuscarNota(int indice)
{
	return arrayNotas[indice].Nota;
}

int GrabarNota(int LU, int nota)
{
	int low = 0;
	int high = (sizeof(arrayNotas) - 1);

	for (low=0;low<=high;low++)
	{
		if (arrayNotas[low].LU == 0 && arrayNotas[low].Nota == 0)
		{
			arrayNotas[low].modo = 'P';
			arrayNotas[low].LU = LU;
			arrayNotas[low].Nota = nota;
			return 0;
		}
	}
	return -1;	
}
*/



