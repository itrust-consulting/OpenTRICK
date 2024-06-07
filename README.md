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

src/main/webapp/WEB-INF/static/views/user-guide.html

# Deployment

## Docker

TRICK Service is a network service, and should be made available in an internal network. To deploy TRICK Service, it is recommended to use Docker and the ```docker-compose``` file contained in this project.

To begin the deployment process, copy the deployment-example.properties

## Java application

TRICK Service can be run as a web application directly from the terminal. For that to be possible, initially compile the project using Maven:

```bash
mvn clean install -DskipTests
```

The default build configuration uses Tomcat as the web server application. However, it is possible to use Jetty as well, all that is necessary for this change to take place is to change the Maven profile used in the build process by using the following command:

```bash
mvn clean install -DskipTests -Pjetty-server
```

The certificate issuer is for dev.local

NOTE: It is possible to force the use of Tomcat by using the command ```bash mvn clean install -DskipTests -Ptomcat-server```

After the successful build, verify that the file ```target/trickservice.war``` has been created

After Maven has finished the compilation process, the application is ready to run in the terminal. For that, however, it is necessary to verify the properties files used by Spring to run the application. 

Find trickservice.war inside ./target/trickservice.war 

2. java --spring.config.additional-location=</deployment-ldap.properties file path>,</deployment.properties file path>

# License

Copyright © itrust consulting. All rights reserved.

Acknowledgment: This tool was co-funded by the Ministry of Economy and Foreign Trade of Luxembourg, within the project Cloud Cybersecurity Fortress of Open Resources and Tools for Resilience (CyFORT).

Licensed under the GNU Affero General Public License (AGPL) v3.0.

# Contact
For more information about the project, contact us at dev@itrust.lu.