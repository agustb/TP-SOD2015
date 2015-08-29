#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>

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






