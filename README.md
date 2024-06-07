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

To build the Docker image please refer to [the installation guide on how to create a docker image.](docs/INSTALL.md#create-a-docker-image)

## Java application

To build as a java application, please refer to [the installation guide on how to create a java application.](docs/installation/deployment/java/README.md#java-application)

# License

Copyright © itrust consulting. All rights reserved.

Acknowledgment: This tool was co-funded by the Ministry of Economy and Foreign Trade of Luxembourg, within the project Cloud Cybersecurity Fortress of Open Resources and Tools for Resilience (CyFORT).

Licensed under the GNU Affero General Public License (AGPL) v3.0.

# Contact
For more information about the project, contact us at dev@itrust.lu.