# About

![OpenTRICK Logo](src/main/webapp/WEB-INF/static/images/brand.png)

OpenTRICK is a web-application supporting risk assessment and treatment.  

OpenTRICK (formerly called TRICK service) is a full-featured risk management tool, assisting in assessing risk, planning actions, as required by an ISO/IE 27001 compliant information security management system (ISMS). It accompanies you throughout the whole risk management process; starting with the definition of the risk context, covering risk estimation and treatment, and communicating the results. OpenTRICK prepares you to be certified for ISO 27001, to comply with the requirements of the GDPR, to export the RISK information in the json format requested by the LU regulator ILR or in order to respond to CSSF circular 12/544.  

It covers a wide variety of features such as quantitative/qualitative analysis of risk scenarios, estimation of Return on Security Investment (ROSI) based on risk reduction factors (RRF), embedding of custom or pre-defined catalogues for rated security controls (27002, GDPR, 22301, IoT, …), multi-user support and access control, import/export, and versioning. It allows several risk assessment for different customers or contexts to share information such security and risk parameters over a central knowledge Base, thus explaining its name TRICK = Tool for Risk management of an ISMS based on a Central Knowledge base. Note that such information, e.g., ISO/IEC 27002 is copyright protection, i.e. cannot be part of this release, but it can be imported easily, based on formatted documents available at ILNAS.public.lu (e.g.) upon acquisition of the standard's copyrights (in near future).  

OpenTRICK comes with user access management, activity logs, two-factor authentication, and smart input output feature interacting with Word and Excel. 

To install OpenTRICK, please refer to the [installation guide](#install).

## Requirements

This Software uses Java 17 for running the server software, along with a MySQL compatible database manager. This project uses code from the [Trick2Monarc api project](https://github.com/itrust-consulting/Trick2MonarcApi), available at GitHub.

- Java 17;
- MySQL 8 / MariaDB;

## Build

OpenTRICK is provided as-is, and any customization is left at the discretion of the user. 
The recommended build platform for OpenTRICK is composed of:

- Java 17;
- Maven;
- Windows / Linux (or WSL) / Mac OS.

## Tools for development

OpenTRICK was developed as open-source software and utilizes the following tools:

- Visual Studio Code;
- Java 17;
- Maven;
- MySQL 8 / MariaDB;

# Install

Please refer to [installation guide](docs/INSTALL.md) for the installation process.

# Quick start

To begin using an already installed OpenTRICK instance, please refer to the [User Guide](https://itrust-consulting.github.io/OpenTRICK/src/main/webapp/WEB-INF/static/views/user-guide.html#creating-a-risk-analysis-using-trick-service).
For features available on OpenTRICK refer to [OpenTRICK features guide](./docs/P63_PPT_TRICK-TRICKServiceIntro-EN_v2.0.pdf).

# Deployment

OpenTRICK can be deployed as a Java web application or a docker container. These methods have their own advantages and disadvantages that should be taken into consideration by the IT manager.

## Docker

To build the Docker image please refer to [the installation guide on how to create a docker image.](docs/INSTALL.md#create-a-docker-image)

## Java application

To build as a java application, please refer to [the installation guide on how to create a java application.](docs/installation/deployment/java/README.md#java-application)

# Roadmap
The team at itrust consulting is only responsible for resolving bugs. 
Improvements are made according to predefined schedule and priorities.

# License

Copyright © itrust consulting. All rights reserved.
Licensed under the GNU Affero General Public License (AGPL) v3.0.

# Acknowledgment
This tool was co-funded by the Ministry of Economy and Foreign Trade of Luxembourg, within the project Cloud Cybersecurity Fortress of Open Resources and Tools for Resilience (CyFORT).

# Contact
For more information about the project, contact us at dev@itrust.lu.

