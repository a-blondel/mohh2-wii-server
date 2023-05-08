# MoHH2 Wii Server

Designed to replace EA's closed ones.

## Requirements

### 1/ Hosts file

In order to intercept requests from the server, you must add this line to your hosts file (`C:\Windows\System32\drivers\etc`) :
```
127.0.0.1 wiimoh08.ea.com
```

The server port is defined to `21171` in **ServerConfig**. That way it'll receive the game's requests, don't change it.


### 2/ JDK

This project has been initiated with the `JDK 17`, download it if needed.

### 3/ Maven

If you downloaded Intellij, Maven comes bundled with one, otherwise download the latest version of Maven.

## Run the server

### 1. Build the project

Run the following command :  
`mvn clean package`

### 2.a Start the server in Intellij

Create a new Application config in Intellij and set the following entry-point (main class) :
```
com.ea.StartServer
```

### 2.b Start as a standalone jar

After a successful build, get into the target folder and execute the following command:
```
java -jar wii-moh-tcp-server-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

