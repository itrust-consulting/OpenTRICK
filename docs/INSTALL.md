# OpenTRICK Installation Manual

OpenTRICK is a web based application for creating risk analyses. This software uses Java 17 and either Apache Tomcat or Jetty as page servers, as well as MariaDB for database management.

During the installation process, it will be assumed that Java 17 and that MariaDB are already installed in the destination computer, whereas Tomcat or Jetty will be installed along with OpenTRICK.

## Preparation

Using the CLI terminal of your system, create a directory to hold OpenTRICK source files and get into the directory.

```bash
mkdir opentrick
cd opentrick
```

After that, clone the GitHb project repository:

```bash
git clone [...]
```

## Build the project
    $ mvn clean compile

### Create a docker image
    $ mvn clean spring-boot:build-image

    After this step the following image must be build successfully
    'opentrick.eu/docker:latest'

 Use the option ```-Dmodule.image.name``` to configure image name as per the need.<br>
 Example:<br>
 ```$ mvn clean spring-boot:build-image -Dmodule.image.name="registry.domain.lu:443/docker-images/opentrickdev"```

 Provide option -DskipTests to skip running tests while building the image.
    
### Installation Using docker
    $ cd docs/installation/deployment/docker    

    $ cp deployment-example.env  deployment.env
    $ cp deployment-ldap-example.env  deployment-ldap.env
    $ cp docker-compose-example.yml  docker-compose.yml

    Change the env_file target in docker-compose.yml file to use the apprpriate docker files (deployment.env and deployment-ldap.env)
    as shown below:
```bash
    env_file:
     - ./deployment-example.env
     - ./deployment-ldap-example.env
```
  to:
```bash
    env_file:
     - ./deployment.env
     - ./deployment-ldap.env
```
    
    Adapt the environment, port settings and other settings in the above files if needed.
  
### Start the docker

   Start docker<br>
   ```$ sudo service docker start```<br>

   Start the docker for trick service, mysql and phpmyadmin:<br>
   ```$ docker compose -f docker-compose.yml up -d```<br>

   Check the logs to see if applications are successfully started:<br>
   ```$ docker compose -f docker-compose.yml logs -f    ```<br>

   Check the docker has successfully started. The following images should be visble with docker ps command.<br>
   ```$ docker ps``` <br>
   ```CONTAINER ID   IMAGE                               COMMAND                  CREATED          STATUS          PORTS                                                  NAMES```<br>
   ```b455c54386d7   phpmyadmin/phpmyadmin:latest        "/docker-entrypoint.…"   17 seconds ago   Up 15 seconds   0.0.0.0:8080->80/tcp, :::8080->80/tcp                  docker-phpmyadmin-1```<br>
   ```f80f0f45edc5   opentrick.eu/docker:latest          "/cnb/process/web"       17 seconds ago   Up 15 seconds   0.0.0.0:8443->8443/tcp, :::8443->8443/tcp              docker-opentrick-1```<br>
   ```02467260bfd2   mysql:8                             "docker-entrypoint.s…"   17 seconds ago   Up 16 seconds   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   docker-db-1```<br>

    Check that OpenTRICK is up and running    
    Open OpenTRICK URL in one of supported browsers
    https://localhost:8443 

### Stop the docker
    $ docker compose -f docker-compose.yml down
    
## Alternately installation using conventional method

    After building the project follow the following steps for installation instructions.

    Refer to the [installation using conventional method](./installation/deployment/java/README.md).

## Verifying the install and getting started

Open the URL in browser ```localhost:8443```  to get started.

### Supported browsers:

Google Chrome    Version 125.0.6422.113, 
Microsoft edge   Version 125.0.2535.67

### Executing for the first time

Refer to the [User Guide for getting started](../src/main/webapp/WEB-INF/static/views/user-guide.html#creating-a-risk-analysis-using-trick-service#how-to-start-using-ts).

After compiling the project, open the system Internet browser and access the URL ```localhost:8080``` to access the PHP administration platform. The following window should appear.
In case any changes in database are needed this platform can be utilized.