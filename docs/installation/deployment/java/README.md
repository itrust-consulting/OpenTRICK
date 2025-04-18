# Java applicaiton

## Build
TRICK Service can be run as a web application directly from the terminal. For that to be possible, initially compile the project using Maven.

```bash
mvn clean install 
```

After a successful build, verify that the file ```target/trickservice.war``` has been created.
Once Maven has finished the compilation process, the application is ready to run in the terminal.
Follow the sections below to setup the database and setting up the properties files before running the application.

## Optional arguments
   - -DskipTests : Provide this argument to skip running tests
   - -Pjetty-server :  This option enables using Jetty as as the web server application. This option is recommended for production environment.
   - -Ptomcat-server : The default build configuration uses Tomcat as the web server application. This option will however force the use of Tomcat as web server application.

## HTTPS Certificate
    Please note that the https certificate is issued for dev.local. Hence, it is necessary to add the localhost to the hosts file of the machine's Operational System. 
    For this procedure to be successful, it might be necessary to update both the IPv4 and IPv6 of the local host in the file.

## Database initialization
   Trick Service requires a connection to database called ```trickservice``` available on a running instance of mysql on port "3306".

   Refer deployment.properties explained in [Running the application using conventional method](#Running-the-application-using-conventional-method) having the following default settings

   ```jdbc.databaseurl=jdbc:mysql://localhost:3306/trickservice?autoReconnect=true&verifyServerCertificate=false&useSSL=true&requireSSL=false```

   Ensure to create a database as per the above specifications before proceeding to the next step.
   
### Create database using docker compose
    If not configured manually database can also be created using docker compose using command below:
    
    $ docker compose -f docs/installation/deployment/java/docker-compose-db.yml up -d

    The above command created an instance of  
    - mysql:8 on port 3306 
    - phpmyadmin/phpmyadmin on port 8080

    Use following docker command to verify both instances are up and running
    $ docker ps 
        
## Running the application using conventional method
   
Trick service requires following two configuration files for application to run successfully:
- -deployment.properties : This file provides the configuration of various environments used for successful running of the application including the database url etc.
                              Sample file is available at [deployment-example.properties] (src/main/resources/deployment-example.properties).
- -deployment-ldap.properties : This file provides the configuration of LDAP environment required for successful running of the application.
                              Sample file is available at [deployment-ldap-example.properties] (src/main/resources/deployment-ldap-example.properties).

You may like to copy these files to a location accessible by application at all times example:
   
   ```$ mkdir java_properties```

   ```$ cp src/main/resources/deployment-example.properties java_properties/deployment.properties```

   ```$ cp src/main/resources/deployment-ldap-example.properties java_properties/deployment-ldap.properties```

   NOTE: The provided paths are suggestions. If there is good reason, it is possible to change the file path for any other, just make sure it is visible to the application at all times.

   Run the java application, enter the following command in the CLI terminal:

```bash
./target/trickservice.war --spring.config.additional-location=</deployment-ldap.properties file full path>,</deployment.properties file full path>
```

Example:

``` ./target/trickservice.war --spring.config.additional-location=/home/user/opentrick/java_properties/deployment.properties,/home/user/opentrick/java_properties/deployment-ldap.properties ``` 

Now it is possible to go to the internet browser and access the URL https://localhost:8443

## Changing configuration or ports
It is possible to configure ports ```server.port``` property in the ```deployment.properties``` file. Similarly other settings can be changed as well.