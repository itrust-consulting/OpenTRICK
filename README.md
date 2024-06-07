# About

![TRICK Service Logo](src/main/webapp/WEB-INF/static/images/TrickService.png)

TRICK Service is a network-based risk analysis platform that allows to create risk scenarios with user-defined assets and risks.

This platform uses Java 17 for running the server software, along with a MySQL compatible database manager. This project uses code from the [Trick2Monarc api project](https://github.com/itrust-consulting/Trick2MonarcApi), available at GitHub.

To begin using TRICK Service, please refer to the [installation guide](docs/INSTALL.md).

## Requirements

- Java 17;
- MySQL 8 / MariaDB;

## Build

TRICK Service is provided as-is. The recommended build platform for TRICK Service is composed of:

- Java 17;
- Maven;
- Windows / Linux (or WSL) / Mac OS.

## Tools for development

TRICK Service was developed as open source software and 

- Visual Studio Code;
- Java 17;
- Maven;
- MySQL 8 / MariaDB;

# Install

Please refer to [the TRICK Service installation guide](docs/INSTALL.md) to verify the installation process.

# Quick start

To begin using an already installed TRICK Service instance, please refer to the [User Guide](src/main/webapp/WEB-INF/static/views/user-guide.html#creating-a-risk-analysis-using-trick-service).

# Deployment

TRICK Service can be deployed as a Java web application or a docker container. These methods have their own advantages and disadvantages that should be taken into consideration by the IT manager.

## Docker

To build the Docker image please refer to [the installation guide on how to create a docker image.](#create-a-docker-image)

## Java application

TRICK Service can be run as a web application directly from the terminal. For that to be possible, initially compile the project using Maven:

```bash
mvn clean install -DskipTests
```

The default build configuration uses Tomcat as the web server application. However, it is possible to use Jetty as well, all that is necessary for this change to take place is to change the Maven profile used in the build process by using the following command:

```bash
mvn clean install -DskipTests -Pjetty-server
```

Please note that the https certificate is issued for dev.local. Hence, it is necessary to add the localhost to the hosts file of the machine's Operational System. For this procedure to be successful, it might be necessary to update both the IPv4 and IPv6 of the local host in the file.

NOTE: It is possible to force the use of Tomcat by using the command ```bash mvn clean install -DskipTests -Ptomcat-server```

After the successful build, verify that the file ```target/trickservice.war``` has been created

After Maven has finished the compilation process, the application is ready to run in the terminal. For that, however, it is necessary to verify the properties files used by Spring to run the application. 

To create the properties files used by TRICK Service, copy the files [deployment-example.properties](src/main/resources/deployment-example.properties) and [deployment-ldap-example.properties](src/main/resources/deployment-ldap-example.properties) to create the files src/main/resources/deployment.properties and src/main/resources/deployment-ldap.properties, respectively.

NOTE: The provided paths are suggestions. If there is good reason, it is possible to change the file path for any other, just make sure it is visible to the application at all times and copy the path to the files, which will be necessary shortly.

To run the java application, enter the following command in the CLI terminal:

```bash
java --spring.config.additional-location=</deployment-ldap.properties file path>,</deployment.properties file path>
```

Now it is possible to go to the internet browser and access the URL https://localhost:8443. If there is good reason to change to another port, it is possible to configure it by changing the ```server.port``` property in the ```deployment.properties``` file.

# License

Copyright © itrust consulting. All rights reserved.

Acknowledgment: This tool was co-funded by the Ministry of Economy and Foreign Trade of Luxembourg, within the project Cloud Cybersecurity Fortress of Open Resources and Tools for Resilience (CyFORT).

Licensed under the GNU Affero General Public License (AGPL) v3.0.

# Contact
For more information about the project, contact us at dev@itrust.lu.