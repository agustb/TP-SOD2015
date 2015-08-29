#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>

int chequea_parametros_cliente (int argc, char* argv[], char **ip, char **modo, int *puerto, int *lu, int *nota)
{

	if (argc < 2) // si no llegan parámetros produce error y muestra el siguiente mensaje.
	{
		printf ("Ha ocurrido un Error. Parametros: %s [-p <port>] [-a|-p <Modo Alumno|Profesor>] [lu <Libreta Universitaria>] [nota <Calificacion>]\n", argv[0]);
		return -1;
	}
	*ip= argv[1];
	
	*modo = argv[3];
	//printf ("Opcion elegida: %s\n",*modo);

/*	if (*modo != "-a" || *modo != "-p")
    {
        printf ("ERROR. Los modos permitidos son: [-a] (si ingresa como ALUMNO) o [-p] (si ingresa como PROFESOR).\n");
        return -1;
    }
    else
    {
        printf ("Modo Correcto. Opcion elegida: %s",*modo);
    }  */

	int c;
	while ((c = getopt (argc, argv, "p:l:n:m:")) != -1)
	{
		switch (c)
	    {
	    	case 'p': // puerto
	       	{
			   *puerto = atoi(optarg);
			   if (*puerto <2000 || *puerto > 30000)
			   {printf ("Modo Correcto. Opcion elegida: %s",*modo);
			   		printf ("El puerto debe estar comprendido entre 2000 y 30000\n");
					return -1;
			   }
			   break;
	       	}

//			case 'm': // modo: Alumno | Profesor
//	       	{
//			   *modo = optarg;
//			   if (*modo != "a" || *modo != "p")
//			   {
//			   		printf ("Los modos permitidos son: [-a] (si ingresa como ALUMNO) o [-p] (si ingresa como PROFESOR).\n");
//					return -1;
//			   }
//			   break;
//	       	}

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