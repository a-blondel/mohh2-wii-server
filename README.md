# MoHH2 Wii Server

Designed to replace EA's closed ones.

## Requirements

### 1/ Hosts file

In order to intercept messages from the server, you must add this line to your hosts file (`C:\Windows\System32\drivers\etc`) :
```
127.0.0.1 wiimoh08.ea.com
```

The SSL port is defined in **application.yml** (property `ssl.port`).  
**It is region-dependant therefore it must be changed accordingly to your version of the game** :  
- `21171` : RM2X69
- `21121` : RM2E69


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
com.ea.ServerApp
```

### 2.b Start as a standalone jar

After a successful build, get into the target folder and execute the following command:
```
java -jar wii-moh-tcp-server-1.0.0-SNAPSHOT.jar
```

## Database

For development, an embedded database is used.
See `data.sql` for available data on start.
It can be accessed in a browser when the server is running :  
```
http://localhost:8080/h2/
```

Configuration like database url, user and password can be found in `application.yml`.

## Lobby PARAMS description

```
1 = Mode (2 = CTF, 7 = TDM, 8 = DM)
2 = Map (191 = Village, 65 = Port, 1f5 = monastery, c9 = City, 12d = sewers, 259 = base)
3 = Friendly fire (1 = true, 2 = reverse fire, empty = false)
4 = Equilibrate (1 = true, empty = false)
5 = number of rounds
6 = points limit
7 = score limit
8 = round time limit
9 = max team kills
10 = controls (empty = Elite, ? = Zapper, -1 = all)
11 = SMG (1 = true, empty = false)
12 = HMG
13 = Rifle
14 = Scoped Rifle
15 = Shotgun
16 = Bazooka
17 = Grenades
18 = Ranked - Must come with SYSFLAGS (ranked = 262656, unranked = 512) !
19 = max players
```