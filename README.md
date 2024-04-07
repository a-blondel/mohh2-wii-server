# Medal Of Honor Heroes 2 Server [Wii]

Designed to replace EA's closed ones.  

## Discord

Link : https://discord.gg/fwrQHHxrQQ  

It is used to :
- Talk about the game
- Share technical knowledge
- Centralize documentation
- Regroup the community and organize events when we'll be ready (hopefully)

Fell free to join !

## Wiki

Everything to know is in the [Wiki](https://github.com/a-blondel/mohh2-wii-server/wiki)  
It contains :
- Generic info about the game (weapons, maps,...)
- Technical knowledge (packet capture, ...)

## Development Status

**Work In Progress**

<img src="doc/img/player-details.png" alt="player-details" width="400"/> <img src="doc/img/leaderboards.png" alt="leaderboards" width="400"/><br/>
*Player details / Leaderboards*

<img src="doc/img/lobbies.png" alt="lobbies" width="400"/> <img src="doc/img/game-join.png" alt="game-join" width="400"/><br/>
*Lobbies / Game joining*

<img src="doc/img/game-disconnect.png" alt="game-disconnect" width="400"/><br/>
*Disconnection*

**Features**
- [x] Access Nintendo WFC (Riivolution patch with Wiimmfi)
- [ ] EA account management
  - [x] Create account
  - [x] Update account
  - [x] Use account
  - [x] Create persona
  - [x] Delete persona
  - [x] Use persona
  - [ ] Account recovery (require SMTP)
  - [ ] Password reset (require SMTP)
- [ ] Main menu
  - [x] Player info (Country, Kills, Deaths, Score, Rank, Reputation)
  - [ ] Online player count
- [ ] Lobby
  - [x] Games available
  - [x] Players in game (partially : player list is done, players details isn't)
  - [ ] Filter options
  - [x] Create game (almost complete : password protected lobbies isn't handled yet)
  - [x] Join game (almost complete : password protected lobbies isn't handled yet)
- [ ] In game
  - [ ] Team, uniform and weapon selection screen (disconnected just before)
  - [ ] --anything else to handle that we are not aware of yet--
- [x] Leaderboards
  - [x] My EA Leaderboard
  - [x] EA Top 100
  - [x] EA Weapon Leaders
- [ ] My profile
  - [ ] FAQ
  - [ ] Terms and conditions
- [ ] EA Messenger
  - [ ] List recently met people
  - [ ] List online/offline people and which game they play (friends/blocked/friend request)
  - [ ] Find Player
  - [ ] Send message
  - [ ] Send friend request
  - [ ] Block
  - [ ] Comment

Note that error messages eg 'invalid password'/'unknown account' are more or less complete.


## Requirements

### 1/ Riivolution patches

To make the game to use this server you need to activate Riivolution patches when starting the game.  
Patches can be found here : https://github.com/a-blondel/mohh2-wii-patch

### 2/ Hosts file or DNS

In order to intercept requests from the game, you must either use a DNS server or edit your hosts file.

- Hosts file (for Dolphin)

Add this line to your hosts file (`C:\Windows\System32\drivers\etc`) :
```
127.0.0.1 wiimoh08.ea.com
```

- DNS (for the Wii)

  - Host a DNS server (i.e. [Unbound](https://nlnetlabs.nl/projects/unbound/about/)) to redirect `wiimoh08.ea.com` packets to the machine hosting the server.
  - Configure the Wii to use the DNS server

Unbound configuration (`service.conf`) :
```
server:
	# verbosity level 0-4 of logging
	verbosity: 1

	# On windows you may want to make all the paths relative to the
	# directory that has the executable in it (unbound.exe).  Use this.
	directory: "%EXECUTABLE%"

	# if you want to log to a file use
	logfile: "unbound.log"
	
	interface: ::1
	interface: 127.0.0.1
	interface: 192.168.1.1 # CHANGE IT !
	
	port: 53
	
	access-control: 127.0.0.0/24 allow
	access-control: 192.168.0.0/16 allow
	access-control: ::1 allow
	
	root-hints: "named.cache"
	
	local-zone: "wiimoh08.ea.com" static
	local-data: "wiimoh08.ea.com A 192.168.1.1" # CHANGE IT !
```

Change `192.168.1.1` to your server's IP address.


### 3/ JDK

This project has been initiated with the `JDK 17`, download it if needed.

### 4/ Maven

If you downloaded Intellij, Maven comes bundled with one, otherwise download the latest version of Maven.

#### Maven profile

**Some properties like the SSL port are region-dependant, therefore they must be changed accordingly to the version of the game.**  
A maven profile exists for each region:
- `pal` : RM2X69 and RM2P69
- `ntsc` : RM2E69

Currently, all profiles are located in *application.yml* as there won't be many region-based properties.  
**When you don't specify any maven profile, it fallbacks to `pal`.**

### 5/ Define the host machine

Configuration is defined in `application.yml`.

Currently, multiplayer isn't supported (only single player online), so `udp.host` can remain `127.0.0.1`, players will "host themselves".

As for `tcp.host`, it depends on your use case :
- If you are running the server (not in WSL) and the game on the same machine (using Dolphin), and you don't need to host for other machines, then no changes are needed.
- If you are running the server (in WSL) and the game on the same machine (using Dolphin), and you don't need to host for other machines, then you must set the WSL's eth0 IP.
- If you are running the server for other machines (i.e. a Wii, or another computer using Dolphin), then you must set the machine IP (works for private and public networks).

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

### 2.c Start with Docker

Open a terminal at the root of the project (where the Dockerfile is located). If you are using WSL :
```
cd /mnt/c/path/to/the/project
```

Create the image
```
docker build --tag mohh2-wii-server:latest .
```

Start the image (add `-d` option to run in background)
- PAL
```
docker run --name mohh2-wii-pal --rm -it -p 8080:8080 -p 21171:21171 -p 21172:21172 -p 21173:21173 -e "SPRING_PROFILES_ACTIVE=pal" mohh2-wii-server:latest
```

- NTSC
```
docker run --name mohh2-wii-ntsc --rm -it -p 8080:8080 -p 21121:21121 -p 21172:21172 -p 21173:21173 -e "SPRING_PROFILES_ACTIVE=ntsc" mohh2-wii-server:latest
```

If started in background, here is how to open a bash in the container :
```
docker container ls
docker exec -it <container-id> bash
```

## Database

For development, an embedded database is used.  
See `data.sql` (default file) & `data-full.sql` (leaderboards sample) for available data on start.  
It can be accessed in a browser when the server is running :  
```
http://localhost:8080/h2/
```

Configuration like database url, user and password can be found in `application.yml`.

## Connect Mode

To enable `connect mode`, set `udp.connect-mode.enabled` to `true` in `application.yml`.  
Don't forget to replace `41` by `40` at `0x8001BEB4`.
