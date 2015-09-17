#ifndef _RESOURCES_H
#define _RESOURCES_H

int chequea_parametros_cliente (int argc, char* argv[], char **ip, char **modo, int *puerto, int *lu, int *nota);
int chequea_parametros_servidor(int argc, char* argv[], int *puerto);
int LeerSocket (int fd, char *Datos, int Longitud);
int EscribirSocket (int fd, char *Datos, int Longitud);
void EscribeMensaje (int socket, int idMensaje, char *mensaje, int tamanio);
void LeeMensaje (int socket, int *idMensaje, char **mensaje);


void IncializaArrayNotas();
int IndiceLibretaUniversitaria(int LU);
int BuscarNota(int indice);
int GrabarNota(int LU, int nota);


#endif


