# MoHH2 Wii Server

Designed to replace EA's closed ones.

## Requirements

### 1/ Hosts file

In order to intercept messages from the server, you must add this line to your hosts file (`C:\Windows\System32\drivers\etc`) :
```
127.0.0.1 wiimoh08.ea.com
```

### 2/ JDK

This project has been initiated with the `JDK 17`, download it if needed.

### 3/ Maven

If you downloaded Intellij, Maven comes bundled with one, otherwise download the latest version of Maven.

#### Maven profile

**Some properties like the SSL port are region-dependant, therefore they must be changed accordingly to the version of the game.**  
A maven profile exists for each region:
- `pal` : RM2X69 and RM2P69
- `ntsc` : RM2E69

Currently, all profiles are located in *application.yml* as there won't be many region-based properties.  
**When you don't specify any maven profile, it fallbacks to `pal`.**

## Run the server

### 1. Build the project

Run the following command :  
`mvn clean package`

### 2.a Start the server in Intellij

Create a new Application config in Intellij and set the following entry-point (main class) :
```
com.ea.ServerApp
```

If you need to specify a profile, be sure to check `Add VM options` (or use Alt+V), then fill the field with :
```
-Dspring.profiles.active=ntsc
```

### 2.b Start as a standalone jar

After a successful build, get into the target folder and execute the following command:
```
java -jar mohh2-wii-server-1.0.0-SNAPSHOT.jar
```

If you need to specify a profile, use :
```
java -jar -Dspring.profiles.active=ntsc mohh2-wii-server-1.0.0-SNAPSHOT.jar
```

## Database

For development, an embedded database is used.
See `data.sql` for available data on start.
It can be accessed in a browser when the server is running :  
```
http://localhost:8080/h2/
```

Configuration like database url, user and password can be found in `application.yml`.
