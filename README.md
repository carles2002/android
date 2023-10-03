
# Android Beacon Base

Este proyecto de Android recibe iBeacons y los envia mediante POST para ser guardados mas tarde en una base de datos

## Instalación
Se debe descargar/clonar el repositorio, se tiene que comprobar que la API utilizada en android es mayor que la 31 para asegurar el correcto funcionamiento. 

Al abrir el proyecto se ha de dejar que se descargen correctamente todas las librerias (graddle).

Para que funcione correctamente se tiene que cambiar la IP a la que se envian los datos del json. 

*La parte del servidor tiene que estar correctamente configurada (Véase el repositorio del servidor)*

Al instalar la aplicación en el dispositivo android se tienen que dar todos los permisos necesarios en la configuración de aplicación (*Ajustes del dispositivo*)

## Uso:

1. Inicie la aplicación.
2. Mueva el dispositivo Android cerca de un beacon iBeacon.
3. La aplicación mostrará la información del beacon detectado en el TextView y en en Log.d del android studio.
4. La aplicación enviará la información del beacon al servidor web en segundo plano.



## Cambio de registro:

Para cambiar el registro de los beacons que se detectan, puede modificar el siguiente código en la clase MainActivity:

``` kotlin
// Crear una región para detectar todos los beacons (UUID, major y minor en null)

val region = Region("myRegion", null, null, null)
```
Puede modificar los valores de UUID, major y minor para detectar solo los beacons que le interesan.




