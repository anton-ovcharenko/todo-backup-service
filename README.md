#Introduction:
This is a simple backup service for todo items, that are fetched from TodoItemServer provided 
(see zip file in folder /bin). 
For detailed information about TodoItemServer, its API and how to start it see the README which 
is located in zip file.

**Service functionality:**
1) start backup accounts (asynchronously) 
2) expose list of backups
3) export backup into CSV format
 
#Environment requirements
- installed maven
- installed java8 

The application uses maven as a project builder and practically is a Spring Boot application that 
is packed into executable jar file.
To build and start the application open project folder and run the command: 

    mvn spring-boot:run

Application uses embedded MongoDB so during first launching it will download all necessary resources, 
it can require some time.
In case some problem with MongoDB instance try to change version of MongoDB through properties.

The server startup on port 8080 and you can access it at http://localhost:8080

#Configuration:
Application has default configuration values that can be changed in the following way:
1) create file application.yml
2) put it near application's executable JAR file
3) re-run the application

**Configurable properties:**

- server.port - Service http port (default: 8080)
- todoitemserver.url - URL to TodoItemServer (default: 127.0.0.1:9000)
- backupDataLoaderTaskExecutor.threads - The number of threads in the pool for asynchronous loading 
of backups (default: 10)
- cvsExporter.batchSize - Amount or records that will be written to stream during export before 
intermediate flushing (default: 10)
- backupData.batchSize - Amount of TodoItems that will be saved per BackupDataBatch document during
 backup data loading (default: 10)
- updateStatusForStuckBackups.fixed.rate.ms - Amount of milliseconds for updateStatusForStuckBackups 
job rate (default: 30000)
- embedded.mongodb.host - Host for embedded MongoDb instance (default: localhost)
- embedded.mongodb.port - Port for embedded MongoDb instance (default: 12345)
- embedded.mongodb.dbname - Database name for embedded MongoDb instance (default: embedded_db)
- embedded.mongodb.version - Version of embedded MongoDb instance (default: 3.5.5)
- feign.hystrix.enabled - Hystrix enabling toggle for TodoItemServer client (default: true)

# Service API:
The server provides to following REST API:

**Start backup accounts** 

This API will initiate a complete backup of all todo items in the TodoItemServer. 
The backup is asynchronous and the API will return the the id for the initiated backup.
   
- Request: `POST /backups` 
- Request body: n/a
- Response body: `{ “backupId”: <backupId> }`

**Expose list of backup**

This API will list all backups that have initiated. 
Backup status is one of the following:

    * In progress
    * OK
    * Failed

- Request: `GET /backups` 
- Request body: n/a 
- Response body: 
    ```
    [ 
      { “backupId”: “<backup id>”, “date”: “<backup date>”, “status”: “<backup status>” }, 
      { … } 
    ]
    ```

**Export backup into CSV format**

This API will return the content of a specified backup id the CSV format specified below.

- Request: `GET /exports/{backup id} `
- Request body: n/a 
- Response body: 
    ```
    Username;TodoItemId;Subject;DueDate;Done 
    {username};{todoitemid};{subject};{duedate};{done}
    ...
    ```