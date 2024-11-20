
# dwese-ticket-logger-webapp

## Descripción

Esta aplicación es un ejemplo de una aplicación web desarrollada con **Spring Boot**. Su objetivo principal es ofrecer un registro de tickets en un entorno de servidor. Se ha configurado para utilizar **MariaDB** como base de datos y **JSP** para las vistas. Además, el proyecto utiliza pruebas unitarias con **JUnit** y **Mockito** para garantizar la calidad del código.

## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

- **src/main/java**: Contiene el código fuente de la aplicación.
    - `config`: Aquí se encuentra la clase de configuración que carga las variables de entorno.
    - `controller`: Aquí están los controladores REST, como el `HelloController`.
    - `DweseTicketLoggerWebappApplication`: Clase principal que lanza la aplicación.

- **src/test/java**: Contiene las pruebas unitarias de la aplicación.
    - `DweseTicketLoggerWebappApplicationTests`: Pruebas para la clase principal.

- **src/main/resources**: Contiene los recursos como el archivo `application.properties` que define la configuración de la aplicación.

## Dependencias

Este proyecto utiliza **Maven** para gestionar sus dependencias. Puedes instalar todas las dependencias necesarias ejecutando el siguiente comando:

```bash
./mvnw clean install
```

Esto descargará e instalará todas las dependencias necesarias en el archivo `pom.xml`. Algunas de las dependencias más importantes del proyecto incluyen:

- **Spring Boot**: Para la construcción de la aplicación web.
- **MariaDB**: Para la conexión a la base de datos.
- **JUnit** y **Mockito**: Para la realización de pruebas unitarias.
- **Dotenv**: Para manejar variables de entorno.

## Configuración

La aplicación utiliza variables de entorno definidas en un archivo `.env` para configurar la conexión a la base de datos y otros parámetros clave. Aquí tienes un ejemplo de archivo `.env`:

```bash
DB_URL=jdbc:mariadb://localhost:3306/ticket-logger
DB_ROOT_PASSWORD=root_password
DB_DATABASE=ticket-logger
DB_USER=myuser
DB_PASSWORD=mypassword
DB_DRIVER=org.mariadb.jdbc.Driver
```

Asegúrate de que estas variables de entorno estén correctamente configuradas antes de ejecutar la aplicación.

El archivo `application.properties`, ubicado en `src/main/resources`, utiliza estas variables para establecer la configuración del datasource, que incluye la URL, el usuario y la contraseña de la base de datos.

## Ejecución

Para ejecutar la aplicación, utiliza el siguiente comando desde el directorio raíz del proyecto:

```bash
./mvnw spring-boot:run
```

Esto iniciará el servidor de **Spring Boot** y la aplicación estará disponible en la siguiente URL:

```
http://localhost:8080
```

### Usando Docker (opcional)

Si prefieres usar Docker para configurar la base de datos, puedes levantar un contenedor de **MariaDB** utilizando el archivo `docker-compose.yml`:

```bash
docker-compose up
```

Esto levantará un servicio de base de datos que estará listo para conectarse a la aplicación.

## Uso

Una vez que la aplicación esté corriendo, puedes acceder a un controlador básico que responde a la siguiente ruta:

```
http://localhost:8080/hello?name=tu_nombre
```

Si no pasas el parámetro `name`, el controlador responderá con "Hello World!".

## Pruebas

Para ejecutar las pruebas unitarias, utiliza el siguiente comando:

```bash
./mvnw test
```

Esto ejecutará las pruebas definidas en el directorio `src/test/java`.

## Licencia

Este proyecto está licenciado bajo la licencia **Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)**.
