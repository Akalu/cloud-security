
About 
======
The goal of this research project is to create a minimal secure system consisting from three components:

* the keycloak server (https://www.keycloak.org/getting-started/getting-started-docker)
* Spring Boot resource server
* Spring Boot client application

Spring Boot's version is set to 2.4.3, and Spring Cloud dependencies are to 2020.0.1

Note: Hoxton is not compatible with Spring Boot 2.4, that is why Spring Cloud Version was set to 2020.0.1; for Spring Boot < 2.4 one can use Hoxton.SR10

Generally this project is using a lot of technologies from OWASP project (Open Web Application Security Project, https://owasp.org/) and OpenID connectivity (https://openid.net/)

Overview
=========
All of the code is organized into two separate services, the Keycloak server is running in docker container

# Resource service

name: image_resource_server 
port: 8050

## Description
Implemented on the basis of Spring Boot this service is using in-memory reactive database to hold a sample data (in the form of Image records)


# Resource client

name: image_client 
port: 8051

## Description
Implemented on the basis of Spring Boot this service is using the resource service to perform various operations with Image records.
For demonstration purposes 


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
Run resource server and clien app as a Spring Boot apps, for example:

```
java -jar ./image-resource-server/target/image-resource-server-1.0.0-SNAPSHOT.jar
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

There are 2 endpoints available, both on http (http://localhost:8083) and https (https://localhost:8443) ones. During R&D phase One can use any endpoint, but in production obviously makes sense to switch to https.

Navigate to https://localhost:8443/auth/ and log in as admin (default user)

The further work can be done in two ways:

1) using configuration from file
2) setup all settings from scratch

Here I will briefly describe the latter one (details can be found at https://www.keycloak.org/getting-started/getting-started-docker), and then I will show how to save the created configuration as a json.

# Configuring Keycloak server from scratch

## Create a realm

Master dropdown in the top-left corner -> 'Add realm' . Name it as 'research'

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


## Testing the secure connection via test client app at keycloak

1) Create a client: <Realm_name> -> Clients -> Create 
  in this example I added a client with id=test-client and base and redirect url https://www.keycloak.org/app/
2) Go to https://www.keycloak.org/app/
3) Choose https://localhost:8443/auth as your keycloak server, research realm and test-client as input and test oath2 authorization flow 
   After successful authorization one can see the screen keycloak_07b.png and toast for John Smith
   
## Creating a client app

1) Create a client: <Realm_name> -> Clients -> Create 
  
In this example I added a client with id=resource-client-id and empty base/root urls and redirect url as *
   
## Testing public token via rest request

The first test request to perform: 

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

Note the  issuer URL - this url must be included in config files

# Configuring Keycloak server from pre-configuration saved in json files 

1) Navigate browser to the login page (https://localhost:8443/auth/)
2) Log in to admin console
3) Use import configuration with option "If a resource exists = skip and Import client roles = OFF" and use pre-configured json from /config/realm-export-roles.json as a source.
4) Use Add realm -> from json to import realm configuration (realm itself and all client apps) and use pre-configured json from /config/realm-export.json as a source.

Unfortunately Keycloak v.12 does not have an option to import users, they must be added manually - use the instructions from Create a user section describing the whole process in details

# Configuring resource and client microservices

## Settings on the resource server side


The most important urls from this list are realm's endpoint, as well as authorization_endpoint and token_endpoint - they must be set in Spring Config file. The realm URL usually has the structure

```
<keyloak_url:port>/auth/realms/<realm_name>
```
(This is the issuer URL)

So the final variant of oauth2 configuration section in YAML file should look like:

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8083/auth/realms/research 
```
The issuer url is used to look up the well known configuration page to get all required configuration settings to set up a resource server. On this stage the configuration of resource server is done.

One can run server and authorize using the standard oauth2 flow. As a result two tokens will be retrieved, access_token and refresh_token

The first one, access_token, must be included in each consecutive request to the server as an Authorization: Bearer token, like so:

```
> GET /api/images HTTP/1.1
> Host: localhost:8050
> User-Agent: insomnia/2021.1.0
> Cookie: SESSION=; JSESSIONID=649423523B71EE9D64C0D828C15C5F7C; SESSION=06f503ff-71fd-4355-bab7-2b18ec69b38e
> Authorization: Bearer eyJhbGciOi...jkh6Y
> Accept: */*

* Mark bundle as not supporting multiuse

< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: application/json
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Content-Type-Options: nosniff
< X-Frame-Options: DENY
< X-XSS-Protection: 1 ; mode=block
< Referrer-Policy: no-referrer

* Replaced cookie SESSION="" for domain localhost, path /api/, expire 1

< Set-Cookie: SESSION=; Path=/api/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Lax
```


Some further details can be found here: https://www.baeldung.com/spring-security-oauth-resource-server



## Settings on the resource server's client side


The settings in the YAML file for the client must be as follows:

```
spring:
  security:
      oauth2:
        client:
          registration:
            keycloak: 
              client-id: 'resource-client-id'
              client-secret: '123'
              authorizationGrantType: authorization_code
              redirect-uri: '{baseUrl}/login/oauth2/code/{registrationId}'
              scope: openid
          provider:
            keycloak:
              issuerUri: http://localhost:8083/auth/realms/research 
              user-name-attribute: name
```

Start a new browser's private session.
Navigate browser to http://localhost:8051/userinfo and after authorization at Keycloak the server returns the information about current authorized user like so:

```
{
	"at_hash": "QtvASZVInhQbv-Ch6sPhYw",
	"sub": "b635555b-c16e-475a-98d4-b20c5d69e53c",
	"email_verified": false,
	"iss": "http://localhost:8083/auth/realms/research",
	"typ": "ID",
	"preferred_username": "resource_client",
	"given_name": "John",
	"nonce": "mwrpX2axDATQBgATTpIuVuIlmc1Qqha2ZCg0TUCJzdI",
	"aud": [
		"resource-client-id"
	],
	"acr": "1",
	"azp": "resource-client-id",
	"auth_time": "2021-03-09T14:41:23Z",
	"name": "John Smith",
	"exp": "2021-03-10T00:41:23Z",
	"session_state": "4eb75cd8-b9cd-483f-9e00-de1ebe15996f",
	"family_name": "Smith",
	"iat": "2021-03-09T14:41:23Z",
	"email": "client@test.com",
	"jti": "ebc2fe9d-1d3a-4408-8d7a-db09bd461b9a"
}
```

After authorization one can perform requests to other endpoints as well, f.e. GET request to http://localhost:8051/images returns all image records on resource server:

One can see in browser the result as:

```
[{"id":1,"name":"sample image","owner":"me"}]
```

Note, in case of browser there is no need to make a second authorization - browser will use cookies. In other test software include token in each request. 
Note also, that there are 2 active sessions (see the sessions tab for appropriate client)

The process of testing of transitive requests (from external client app through java client to resource server) is more complex

Requirements
=============

* JDK 11
* Spring Boot 2.4.3 (which in turn requires Java Developer Kit (JDK) 8 or higher)
* Lombok library (to make code's look nice)
* Docker (used to instantiate Keycloak server and resource and client applications)

