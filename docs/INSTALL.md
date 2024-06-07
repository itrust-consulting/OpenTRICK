# Trick Service Installation Manual

Trick service is a web based application for creating risk analyses. This software uses Java 17 and either Apache Tomcat or Jetty as page servers, as well as MariaDB for database management.

During the installation process, it will be assumed that Java 17 and that MariaDB are already installed in the destination computer, whereas Tomcat or Jetty will be installed along with Trick Service.

## Preparation

Using the CLI terminal of your system, create a directory to hold Trick Service source files and get into the directory.

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
    $ docker image tag docker.io/library/trickservice:2.17 trickservice.com/opentrick:latest    

    Provide option -DskipTests to skip running tests while building the image
    
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
    
    Adapt the environment, port settings and other settings in the above files.
    
    Create a folder .cache in this directory. Provide atleast Read, Write, Execute permissions
    to Owner and Read permissions to members of group and others.
    $ mkdir .cache
    $ chmod +744 .cache -R 
    
### Start the docker

    Start docker
    $ sudo service docker start

    Start the docker for trick service, mysql and phpmyadmin:
    $ docker compose -f docker-compose.yml up -d

    Check the logs to see if applications are successfully started:
    $ docker compose -f docker-compose.yml logs -f    

    Check that trick service is up and running:
    
    Open TrickService URL in one of supported browsers:
    https://localhost:8443 

### Stop the docker
    $ docker compose -f docker-compose.yml down
    
## Installation using conventional method

Once the clone process is completed, Maven can be invoked and used to resolve the project dependencies.

```bash
mvn clean build
```
After Maven has completed the build process, the project can be compiled and run.

## Verifying the install and getting started

Open the URL in browser ```localhost:8443```  to get started.

Supported browsers:

Google Chrome    Version 125.0.6422.113, 
Microsoft edge   Version 125.0.2535.67

### Executing for the first time

Refer to the [User Guide for getting started](../src/main/webapp/WEB-INF/static/views/user-guide.html#creating-a-risk-analysis-using-trick-service#how-to-start-using-ts).

After compiling the project, open the system Internet browser and access the URL ```localhost:8080``` to access the PHP administration platform. The following window should appear.
In case any changes in database are needed this platform can be utilized.