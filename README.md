
About 
======
The goal of this project is to create a minimal secure system consisting from three components:

* the keycloak server (https://www.keycloak.org/getting-started/getting-started-docker)
* Spring Boot resource server
* Spring Boot client application

Spring Boot's version is set to 2.4.3, and Spring Cloud dependencies are to 2020.0.1

Note: Hoxton is not compatible with Spring Boot 2.4, that is why Spring Cloud Version was set to 2020.0.1; for Spring Boot < 2.4 one can use Hoxton.SR10


Overview
=========
All of the code is organized into two separate services, all running in docker containers

# Resource service

name: image_resource_server 
port: 8050

## Description
Implemented on the basis of Spring Boot this service is using in-memory reactive database to hold a sample data (in the form of Image records)


# Resource client

name: image_client 
port: 8051

## Description
Implemented on the basis of Spring Boot this service is using in-memory reactive database to hold a sample data (in the form of Image records)


Building
========

The building is quite straitforward, use the following command to build all modules:

```
mvn clean package
```


Running
========

First, start all managed services using command:

```
docker-compose -f docker-compose.yml up --build
```
This command will start MongoDB, RabbitMQ and config service


In the end of work one can shut down and delete containers:

```
docker-compose -f docker-compose.yml down
```



Requirements
=============

* JDK 11
* Spring Boot 2.4.3 (which in turn requires Java Developer Kit (JDK) 8 or higher)
* Lombok library
* Docker (used to instantiate RabbitMQ 3.6 (https://www.rabbitmq.com/) and MongoDB 4.4 (https://www.mongodb.com/)

