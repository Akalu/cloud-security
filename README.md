
About 
======
The goal of this project is to create a minimal secure system consisting from three components:

* the keycloak server (https://www.keycloak.org/getting-started/getting-started-docker)
* Spring Boot resource server
* Spring Boot client application

Spring Boot's version is set to 2.4.3, and Spring Cloud dependencies are to 2020.0.1

Note: Hoxton is not compatible with Spring Boot 2.4, that is why Spring Cloud Version was set to 2020.0.1; for Spring Boot < 2.4 one can use Hoxton.SR10

Generally this project is using a lot of technologies from OWASP project (Open Web Application Security Project, https://owasp.org/) and OpenID connectivity (https://openid.net/)

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


# keycloak

name:keycloak
port:8083

## Description
An image with ready to go keycloak server


Building
========

The building is quite strait-forward, use the following command to build all modules:

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

Keycloak initial setup
=======================

The most difficult part would be the keycloak server installation

The server alone can be installed using the following command:

```
docker run -p 8080:8083 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:12.0.4
```
This will start Keycloak exposed on the local port 8083 (http). It will also create an initial administrator user with username=admin and password=admin.

For the simplicity and convenience I bootstrapped all services including the keycloak one using docker-compose

Note, that the  server is starting quite slow, and initialization takes a quite time

There are 2 endpoints available, both on http and https ones

Navigate to https://localhost:8443/auth/ and log in as admin (default user)

The further work can be done in two ways:

1) using configuration from file
2) setup all settings from scratch

Here I will briefly describe the latter one (details can be found at https://www.keycloak.org/getting-started/getting-started-docker), and then I will show how to save the created configuration as a json.

## Create a realm

Master dropdown in the top-left corner -> Add realm 
Name it as 'research', then create

## Create a user

1) Add a user for service - these creds will be used on behalf of microservices

Username: resource_client (the only mandatory field)

Other data can be added for convenience:

Email: client@test.com

First Name: John

Last Name: Smith

## Add the initial password to be able to login as resource_client

1) Users -> resource_client -> Credentials tab -> Password
2) Set Temporary to OFF to prevent having to update password on first login
 
## Login to account console

Let’s now try to login to the account console to verify the user is configured correctly.

1) Open the Keycloak Account Console at https://localhost:8443/auth/realms/research/account/#/ 

2) Login with resource_client and the password you created earlier


## Testing the secure connection via keycloak

1) Create a client: <Realm_name> -> Clients -> Create 
  in this example I added a client with id=test-client and base and redirect url https://www.keycloak.org/app/
2) go to https://www.keycloak.org/app/
3) choose https://localhost:8443/auth as your keycloak server, research realm and test-client as input and test oath2 authorization flow 
   After successful authorization one can see the screen keycloak_07b.png and toast for John Smith
   
## Testing public token via rest request

The request 

```
GET http://localhost:8083/auth/realms/research
```
should return

```
{
  "realm": "research",
  "public_key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo6cirYaFP1NKFla7lilbQWYKN2ZNclT8gn6Ue9uSMkcsI+E0mjFiWgP1wdW1/Zw83nQdPTnoM1DVLZzNh3Izw/7AOYTcI3NgIEYU0QvIaFJv8l8Df8h0tVx9RTIEG6ECRKrmZnPiB3J/TWYXXvoWPXh6EYH2JRzIHAUtdUzyi36E1/dKbGqHf92UeutsK7QeRyXwebGH4gkkzf7Cycu2mdegBiw/HGSau8H2FpVWtvZa2BDYuAxyrxNc4ZhYOeFfmkRnnJW7+h7dT3/Htf4+2BJXHqEO5/2YxkG7VMBj4z+Eue0snDFDXGYwMeLnozr4PggAJkyWyJ1ZI2jBgeTbFwIDAQAB",
  "token-service": "http://localhost:8083/auth/realms/research/protocol/openid-connect",
  "account-service": "http://localhost:8083/auth/realms/research/account",
  "tokens-not-before": 0
}
```
The configuration for keycloak service in OpenId compliant form can be retrieved via the following request;

```
GET http://localhost:8083/auth/realms/research/.well-known/openid-configuration

{
  "issuer": "http://localhost:8083/auth/realms/research",
  "authorization_endpoint": "http://localhost:8083/auth/realms/research/protocol/openid-connect/auth",
  "token_endpoint": "http://localhost:8083/auth/realms/research/protocol/openid-connect/token",
  "introspection_endpoint": "http://localhost:8083/auth/realms/research/protocol/openid-connect/token/introspect",
  "userinfo_endpoint": "http://localhost:8083/auth/realms/research/protocol/openid-connect/userinfo",
  "end_session_endpoint": "http://localhost:8083/auth/realms/research/protocol/openid-connect/logout",
  "jwks_uri": "http://localhost:8083/auth/realms/research/protocol/openid-connect/certs",
  "check_session_iframe": "http://localhost:8083/auth/realms/research/protocol/openid-connect/login-status-iframe.html",
  "grant_types_supported": [
    "authorization_code",
    "implicit",
    "refresh_token",
    "password",
    "client_credentials"
  ],
  
  <skipped for brevity>
}
```
## Settings on the resource server side


The most important urls from this list are authorization_endpoint and token_endpoint - they must be set in Spring Config file

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/research 
```
The issuer url is used to look up the well known configuration page to get all required configuration settings to set up a resource server

One can run server and authorize using the standard oauth2 flow. As a result two tokens will be retrieved, access_token and refresh_token

The first one, access_token, must be included in each consecutive request to the server as an Authorization: Bearer token

a
Some further details can be found here: https://www.baeldung.com/spring-security-oauth-resource-server




## Settings on the resource server's client side



Requirements
=============

* JDK 11
* Spring Boot 2.4.3 (which in turn requires Java Developer Kit (JDK) 8 or higher)
* Lombok library
* Docker (used to instantiate RabbitMQ 3.6 (https://www.rabbitmq.com/) and MongoDB 4.4 (https://www.mongodb.com/)

