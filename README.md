# Backend Lobby Server

This project is an example/POC for the backend Lobby Service of a Godot application. The matching frontend service can be found [here]()

## File Structure

```tree
📦src
 ┣ 📂main
 ┃ ┣ 📂java.org.landister.vampire.📂backend
 ┃ ┃ ┣ 📂mapper (1)
 ┃ ┃ ┣ 📂model
 ┃ ┃ ┃ ┣ 📂dao
 ┃ ┃ ┃ ┣ 📂enums
 ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┣ 📂response
 ┃ ┃ ┃ ┣ 📂session
 ┃ ┃ ┃ ┗ 📂shared
 ┃ ┃ ┣ 📂services
 ┃ ┃ ┣ 📂util
 ┃ ┃ ┗ 📂websocket
 ┃ ┃ ┃ ┣ 📂games
 ┃ ┃ ┃ ┃ ┗ 📜WalkingSimulator.java
 ┃ ┃ ┃ ┣ 📜BaseController.java
 ┃ ┃ ┃ ┣ 📜ChatController.java
 ┃ ┃ ┃ ┣ 📜LobbyController.java
 ┃ ┃ ┃ ┗ 📜LoginController.java
 ┃ ┗ 📂resources
 ┃ ┃ ┗ 📜application.properties
 ```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/websockets-quickstart-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- WebSockets ([guide](https://quarkus.io/guides/websockets)): WebSocket communication channel support

## Game States

```mermaid
sequenceDiagram
  participant UClient as Host Godot Client
  participant GClient as All Game User's Godot Client
  participant Controller as Quarkus Server
  participant Database as Database


  note right of UClient: Host Starts Game from Lobby

  UClient->>+Controller: Send: { requestType: INITIAL_REQUEST }
    Database-->>Controller: Retrieve Game Info
    Controller->>-UClient: Send: { type: load_assets }

  
  UClient->>+Controller: Send: { requestType: LOAD_ASSETS }
    Database-->>Controller: Retrieve Game Info
    Controller-->>Database: Update Game Info
    Controller->>-GClient: Send: { type: map_setup, player: <Player Info>, goal: Goal Info }
  
  
  GClient->>GClient: Update scene
  
```
