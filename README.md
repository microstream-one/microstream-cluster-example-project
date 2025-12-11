# Eclipse DataGrid Cluster Storage Demo
Demo application featuring a bookstore demo REST-application using the EclipseStore backed [Eclipse DataGrid Micronaut Nodelibrary](https://github.com/eclipse-datagrid/datagrid).

## Starting the application
To start the application execute the following command:

```bash
mvn --add-exports java.base/jdk.internal.misc=ALL-UNNAMED mn:run
```

## Endpoints
To get a documented list of every endpoint the [/swagger-ui](http://localhost:8080/swagger-ui) endpoint can be called at runtime. This uses an OpenAPI definition file which is generated when the project is built. To view this file without starting the application, execute the following command:

```bash
mvn clean package
```

afterwards the file should be located at `target/classes/META-INF/swagger/cluster-storage-demo-1.0.yml`

### Testing
The _testing_ directory contains a Postman collection which can be imported in Postman for easy testing. *WIP* It also contains bash and batch scripts for calling the endpoints via the _curl_ executable.

### Definitions
The application defines the following endpoints:
