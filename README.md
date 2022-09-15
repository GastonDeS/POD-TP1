# Tickets de Vuelo

Trabajo Práctico realizado para la materia Programación de Objetos Distribuidos. 

Sistema remoto thread-safe para la administración de asientos de vuelo de una aerolínea, a partir de la existencia de uno o más modelos de avión y los tickets de vuelo, permitiendo notificar a los pasajeros de los eventos y ofreciendo reportes de los mapas de asientos hasta el momento.

# Instrucciones
Instalar Maven
````
$ sudo apt-get install maven
````

## Correr el programa

Para correr el programa, primero hay que ejecutar un script. Desde la carpeta tpe1-g10 se debe ejecutar:
````
$ ./run.sh
````
Luego, para ejecutar el servidor y el registry se deben correr
````
$ ./run-registry.sh
````
en una terminal, y luego en otra terminal:
````
$ ./run-server.sh
````

## Correr los clientes

Para correr cada client, nos debemos parar en la carpeta descomprimida dentro de /client/target/tpe1-g10-client-1.0-SNAPSHOT

### Client de Administrador de Vuelos

````
$ ./run-admin.sh -DserverAddress={ip}:{port} -Daction={action}[ -DinPath={filename} | -Dflight={flightCode} ]
````
Donde action puede ser:
- models: Agrega un lote de modelos de aviones
- flights: Agrega un lote de vuelos 
- status: Consulta el estado del vuelo de código flightCode. Deberá imprimir en
pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente 
- confirm: Confirma el vuelo de código flightCode.Deberá imprimir en pantalla el
estado del vuelo luego de invocar a la acción o el error correspondiente 
- cancel: Cancela el vuelo de código flightCode. Deberá imprimir en pantalla el
estado del vuelo luego de invocar a la acción o el error correspondiente 
- reticketing: Fuerza el cambio de tickets de vuelos cancelados por tickets de
vuelos alternativos

### Cliente de Asignación de Vuelos

````
 $ ./run-seatAssign -DserverAddress={ip}:{port} -Daction={action} -Dflight={flightCode} [ -Dpassenger={name} | -Drow={num} | -Dcol={L} | -DoriginalFlight={originFlightCode} ]
````
Donde action puede ser:
- status: Deberá imprimir en pantalla si el asiento de fila num y columna L del vuelo de código flightCode está libre u ocupado luego de invocar a la acción 
- assign: Asigna al pasajero name al asiento libre de fila num y columna L del vuelo de código flightCode.
- move: Mueve al pasajero name de un asiento asignado en el vuelo de código flightCode a un asiento libre del mismo vuelo, ubicado en la fila num y columna L. 
- alternatives: Listar los vuelos alternativos al vuelo de código flightCode para el pasajero name. Para cada categoría de asiento en cada vuelo alternativo se debe listar 
  - El código del aeropuerto destino 
  - El código del vuelo 
  - La cantidad de asientos asignables de la categoría 
  - La categoría de los asientos asignables 
- changeTicket: Cambia el ticket del pasajero name de un vuelo de código originFlightCode a otro vuelo alternativo de código flightCode

### Cliente de Notificaciones del Vuelos

````
$ ./run-notifcations -DserverAddress={ip}:{puerto} -Dflight={flightCode} -Dpassenger={name}
````

### Cliente de Consulta del Mapa de Asientos

````
$ ./run-seatMap -DserverAddress={ip}:{puerto} -Dflight={flightCode} [ -Dcategory={catName} | -Drow={rowNumber} ] -DoutPath={output.csv}
````


# Integrantes

- Florencia Chao 
- Juan Manuel De Luca
- Gaston De Schant
- Sol Konfederak
- Brittany Lin

