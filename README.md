# Backend Lobby Server

The goal of this project is the creation of a basic lobby system with a frontend using Godot and a backend server built in Java using the Quarkus framework. For documentation on the the frontend check [this](https://github.com/Jaland/godot-lobby-frontend) repository.

**Language:** Java 11

**Framework:** [Quarkus](https://quarkus.io/)

## Prerequisites

* Java JDK 11+
* Maven 3.8+
* MongoDB Accessible on the internet
  * The easiest way I found to do this for free is with [Mongodb's Cloud Platform](https://cloud.mongodb.com/)

# Deploying

## Deploying Locally (Quick Start)

In order to run locally you will need to create the file `src/main/resources/application-local.properties`. This file will allow you to modify your properties when running the application locally, the only property that is required is `quarkus.mongodb.connection-string` in order to allow our server to connect to the database.

> Note: The `.gitignore` will ignore this file by default so you should not have to worry about putting in Database Credentials on a public repo, and the line can be removed if you fork this into a private repository

**Example application-local.properties**:

```properties
# Database (REQUIRED)
quarkus.mongodb.connection-string=mongodb+srv://readwrite:abc123@mydatabase.gcp.mongodb.net/game?retryWrites=true&w=majority

# Gives trace level logging for our code making local debugging easier.
quarkus.log.category."org.landister".level=TRACE

# Set Logging Levels
quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n
quarkus.log.category."org.mongodb.driver".level=WARN
quarkus.log.level=INFO
```

Once your properties file is in place you can run your application in dev mode using:

```sh
mvn compile quarkus:dev -Dquarkus.profile=local
```

Or if you have `make` installed using:

```sh
make start
```

> **Tip:** Navigating to `localhost:8080` will give you a 404 page with a list of links you can hit that come default with quarkus. The `q/dev` link is useful to explore and better understand what the different java beans and build processes created by the code.

> **Tip:** Dev mode includes `live updates` meaning most code change will take effect without having to recompile (although some changes such as property changes will require a recompile)

### Connecting the Frontend

Once the server is up and running either locally or on a hosted provider, the Godot Frontend can be connected by modifying the `Websocket Host-> Hostname Url` as noted [here](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md#updating-server-host-information)  in the `Updating Server Host Information` section.

## Installing On DigitalOcean

There are lots of ways to deploy your application on the interwebs. I have used Google Cloud in the past and you should be able to get that working for free or close too. I think that AWS may also have some free options. But I have recently started playing around with [Digital Ocean](https://m.do.co/c/5dca16f0ed95) and I have really like the interface and simplicity of it. The pricing is also very reasonable(not totally free). The rest of this demo assumes you are creating a single instance of the backend server at the lowest price for DO($5 a month). And keep in mind that the app can be deleted and redeployed fairly easily using the Github CI/CD process included with this repo and the rate is pro-rated for when the application is actually up. Meaning you can create an account deploy it play around with it for a couple hours then delete it and it will only cost you like $0.20

> **Tip:** Don't forget to delete the instance :)

### Create Container Repository

The GitHub CI/CD process specified later in this README builds our application as an image that is later deployed into a running container. In order to store this image we need access to an Image Registry.

[DigitalOcean offers a way to create a container repository](https://www.digitalocean.com/products/container-registry). The free level lets you create a single repo that will hold enough data to house at least one version of our image.

> Note: If you have access to a different container registry you would rather use that is fine, but you may need to make some changes to the Gitlab CI/CD Pipeline.

### Build Image And Push

This repository includes a `.github/workflows` folder that will create a Github Workflow by default. But in order for it to work there are two secrets that will need to be added to your repo's "secrets" which can be done through the setting menu, documented [here](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md#creating-repository-secret)

> Note: You will need to activate your Github Workflows which can be done through the `Actions` tab on your repo

> Important: Since we are using the `doctl` command (DO's proprietary CLI) if you want to use a different image repository you will need to modify the CI pipeline to use `docker` instead.

#### Required Secrets

| Name                      | Value                                                                                                       | Example                     |
| ------------------------- | ----------------------------------------------------------------------------------------------------------- | --------------------------- |
| DIGITALOCEAN_ACCESS_TOKEN | Token retrieved from the DO cloud ui. `API -> Generate New Token`<br/><br/> <sub>See [Creating Repository Secrets](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md#creating-api-token) section of the frontend README for more info<sub>                                           |                             |
| REGISTRY_BASE_URL         | Base url retrieved from the `Container` page. Should probably be `registry.digitalocean.com`                | `registry.digitalocean.com` |
| REGISTRY_NAME             | Registry name should be the part after the `/` so if your url looks like `registry.digitalocean.com/myrepo` it would be `myrepo` | `myrepo`                    |
| DATABASE_URL              | Database Url (should include credentials) <br /> **IMPORANT:** You must escape all of the `&`s with a `\` | `mongodb+srv://readwrite:abc123@mydatabasehost.gcp.mongodb.net/database?retryWrites=true\&w=majority` |

#### Push Image

The pipeline associated with pushing the image to the image repository is `.github/workflows/deploy-image`. This should be done every time you commit to the main branch based on the `push` section located at line 10. Note that it will only happen if the commit includes a change to the src folder, pom.xml, etc... (assuming workflows were activated). So the easiest way to test this workflow is by just adding a space to the end of the `pom.xml` and pushing a commit.

### Create App

The application can be created by running pipeline `Create Backend Application on Digital Ocean` supplied by the `.github/workflows/create-app.yml`. Note that the application infrastructure specs are based on the [config/digital-ocean/spec.yml](config/digital-ocean/spec.yml) file. This file can be customized based on the spec found [here](https://docs.digitalocean.com/products/app-platform/reference/app-spec/)

#### Spec Notes

**Repository:** Make sure to replace the repository information in the spec with your repo info, should just be a name change.

**Machine:** Defaulting to a single instance of the most basic pod instance (512 mb of memory and 1 shared CPU). it is the cheapest option at $5 a month as of the writing of this README, and can easily be deleted and redeployed using this pipeline again. 
> Note: If you want to test out your app with more than a your friend(s) you can up your memory and cpu options with a different `slug` which you can find using the `doctl apps tier instance-size list` command.

**Region:** Defaults to your closest region but more regions can be found using the `doctl apps list-regions` command.

> Important: Be careful if you remove the `name` field. Doing so could result in multiple instances of your application being deployed which could result in an unpleasant bill. You should also throw a couple billing alerts on you Digital Ocean instance just to be safe.

#### Run The Workflow

The `Create Backend Application on Digital Ocean` assumes the container repository has already been created and the image has been deployed (see `Build Image and Push` above for instructions on how to do this). And it also assumes that there is a "latest" tagged image.

The workflow is set in a way that it needs to be run manually in order to prevent accidental multiple deployments. More about [manual deployments](https://docs.github.com/en/actions/managing-workflow-runs/manually-running-a-workflow) can be found here and an example of running the manual workflow can be found in the [frontend README](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md#run-the-workflow).

After everything is set up navigate to actions in the Github UI and run `Create Backend Application in Digital Ocean` workflow.

#### Validate Deployment

Easiest way to validate your deployment is by using the DO UI. Navigate to [cloud.digitalocean.com](cloud.digitalocean.com) > `Apps` and find the application named `lobby-example-app-backend`.

Find your app's endpoint by hitting the `Live App`button to get the base URL and then navigate to the path `/q/health`

## Hook Up the Frontend

Once you have validated your application has been deployed the only thing left is to setup the [Frontend End and update the host information](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md#updating-server-host-information) to point to your newly deployed server

At this point you should be able to register/login to your server and start a new game.

# Coding Info

Below I have tried to document some of the basics of how the application works, and how the code is layed out.

## Architecture

```mermaid

flowchart TD
    subgraph gc[Godot Client]
    gLogin(Login Scene)
    gLobby(Main/Game Lobby Scene)
    gGame(InGame Scene)
    end
    subgraph qs[Quarkus Server]
    qLogin(Login Controller)
    qLobby(Lobby Controller)
    qGame(Game Controller)
    cache[(In-Memory Cache)]
    end
    db[(MongoDB Database)]  
    
    gLogin --1. Check Credentials--> qLogin
    qLogin -- 2. Retrieve User Info --> db
    qLogin --3. Send JWT Token--> gLogin
    gLogin -- 4. User Logged In --> gLobby
    qLobby -- Store/Retrieve Lobby Info --> cache
    gLobby -- "5. Retrieve Game List/Game Lobby Info" --> qLobby
    gLobby -- "6. Start Game" --> gGame
    gGame -- "7. Get Game Info" --> qGame
    qGame -- Store/Retrieve Game Info --> cache

    style gc fill:#0B8384,stroke:#333,stroke-width:4px
    style qs fill:#EE0000,stroke:#333,stroke-width:4px
    style db fill:#4DB33D,stroke:#333,stroke-width:4px
```

> Tip: While this repo is a single code base that contains the controllers for the Login/Lobby/InGame. There is no reason it could not be deployed separately as a Login Server, Lobby Server, and set of In Game Servers. Which is how I would probably want to do it in a production environment.

> Tip: The "In-Memory" cache is just a java object in this implementation meaning a reboot wipes out your cache, and multiple servers don't share the same cache. But I would probably make it a Redis Server or similar technology if I had more time.

## File Structure

```tree
????src
 ??? ????main
 ??? ??? ????java.org.landister.lobby.backend
 ??? ??? ??? ????mapper ???
 ??? ??? ??? ????model???
 ??? ??? ??? ??? ????dao ???
 ??? ??? ??? ??? ????enums ???
 ??? ??? ??? ??? ????request ???
 ??? ??? ??? ??? ????response ???
 ??? ??? ??? ??? ????session ???
 ??? ??? ??? ??? ????shared ???
 ??? ??? ??? ????services
 ??? ??? ??? ????util
 ??? ??? ??? ????websocket???
 ??? ??? ??? ??? ????games
 ??? ??? ??? ??? ??? ????WalkingSimulator.java ???
 ??? ??? ??? ??? ????BaseController.java ???
 ??? ??? ??? ??? ????ChatController.java ???
 ??? ??? ??? ??? ????LobbyController.java ???
 ??? ??? ??? ??? ????LoginController.java ???
 ??? ??? ????resources
 ??? ??? ??? ????application.properties
 ??? ????LICENSE
 ??? ????Makefile ???
 ??? ????README.md
 ??? ????pom.xml
 ```

1. **mappers:** A set of Mappers used to translate to/from a DTO(Data Transfer Object) to a Request or Response
1. **model:** POJOs representing different pieces of the application
    * A. Database Objects
    * B. Shared enums used across our model
    * C. **request**: Client to Server request/ **response**: Server to Client responses
    * D. Used to save user information for in-memory cache
    * E. Inner objects shared across our model
1. **websocket:** WebSocket Connection Controllers
    * A. **BaseController:** Controller that is extended by all the other Websocket Controllers (except login). This is where our common logic lives
    * B. **ChatController:** Extended by controllers that use the chat functionality. Contains logic for sending messages to specific users and all users in a game
    * C. **LobbyController:** Controller backing the initial login screen
    * D. **LoginController:** Controller backing the main lobby, and game lobby screen
    * E. **WalkingSimulator:** Controller backing our example game
1. **MakeFile** Utilizes [make](https://www.gnu.org/software/make/manual/make.html) to run commands

## Game State Graphs

Communication from the frontend to the backend happen using [WebSockets](https://quarkus.io/guides/websockets). With the data being passed using the [JSON Format](https://www.json.org/json-en.html) for the most part. This provides an easier way for both our client and server to pass and interpret multiple pieces of information at once.

### Login Flow

When a user logins to our application we will retrieve that users info and return back a [JWT](https://jwt.io/) that will be used for validation and will contain the user's username information. And the Godot client will store that token as a cookie for future use. See the `BaseControler` for the validation logic.

> **Note:** The JWT has a 5 hour expiration by default and currently has no logic for automatic refresh.

If we are able to successfully login, we will check to see if the user is currently in a an in-progress game and put them there if so. Or if not we will put the user in the main lobby and refresh the list of active games.

```mermaid
sequenceDiagram
  participant Client as User's Godot Client
  participant Controller as Quarkus Server
  participant Database as Database


  note right of Client: User Enters login info and hit's "LOGIN"

  Client->>+Controller: Get: { requestType: LOGIN }
      Database->>Controller: Retrieve User Info
      Controller->>-Client: Send: <JWT Token>
      Client->>Client: Save token in Cookie
      Client->>Client: Load Lobby View
      
    critical Send Initial Request from User
      Client->>+Controller: Get: { requestType: INITIAL_REQUEST }
      Database->>Controller: Retrieve User's Game Data
      Controller->>Client: Send: { type: load_assets }
    
    option No Game Data Found for User
      Controller->>Client: Send: { type: leave_game }
      Client->>Controller: Get: { requestType: REFRESH_LOBBY }
      Controller->>Client: Send: { type: game_list, game: <Open Game's Info> }
      Client->>Client: Update Game List with Open Games

    option User is Currently In-Game
      Controller->>Client: Send: { type: join_game }
      Client->>Client: Load Game View
    end
```

## Start Game

The host of a game lobby in which a valid number of players have joined, has the option of starting the game. Once started a message will be sent to all the clients in the game and they will load the "In Game Scene". From there the server will request that the host decide on the spawn points and send that info back to the server, where the server will relay that information to the rest of the clients currently in the game.

> **Note:** I am not going to go over the "restart" logic but it works effectively the same way.

```mermaid
sequenceDiagram
  participant UClient as Host Godot Client
  participant GClient as Peer's Godot Clients
  participant Controller as Quarkus Server
  participant Database as Database


  note right of UClient: Host hits "START" from Game Lobby


  rect rgb(191, 223, 255)
  note right of UClient: Lobby Controller
  UClient->>+Controller: Request: { requestType: START_GAME }
    Controller->>UClient: Response: { type: start_game }
    Controller->>GClient: Response: { type: start_game }

  end

  UClient-->>UClient: Load Game Scene
  GClient-->>GClient: Load Game Scene


  rect rgb(225, 225, 153)
  note right of UClient: InGame Controller
  UClient->>+Controller: Request: { requestType: INITIAL_REQUEST }
    Controller-->>Controller: Validate Request is from Host
    Database-->>Controller: Retrieve Game Info
    Controller->>UClient: Response: { type: load_assets }
    UClient-->>UClient: "Load Player/Goal Spawn Points"

  
  UClient->>+Controller: Request: { requestType: LOAD_ASSETS, <Spawn Point Info> }
    Controller-->>Database: Update Game Info
    Controller->>GClient: Response: { type: map_setup, player: <Player Location>, goal: <Goal Spawn> }
    GClient->>GClient: Update scene
    Controller->>UClient: Response: { type: map_setup, player: <Player Location>, goal: <Goal Spawn> }
    UClient->>UClient: Update scene

  end
```

## Useful Links

Git Actions Status (incase your actions stop running suddenly): <https://www.githubstatus.com/>
