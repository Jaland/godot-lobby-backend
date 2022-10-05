# Backend Lobby Server

This project is an example/POC for the backend Lobby Service of a Godot application. The matching frontend service can be found [here]()

## File Structure

```tree
📦src
 ┣ 📂main
 ┃ ┣ 📂java.org.landister.lobby.backend
 ┃ ┃ ┣ 📂mapper ➊
 ┃ ┃ ┣ 📂model❷
 ┃ ┃ ┃ ┣ 📂dao Ⓐ
 ┃ ┃ ┃ ┣ 📂enums Ⓑ
 ┃ ┃ ┃ ┣ 📂request Ⓒ
 ┃ ┃ ┃ ┣ 📂response Ⓒ
 ┃ ┃ ┃ ┣ 📂session Ⓓ
 ┃ ┃ ┃ ┗ 📂shared Ⓔ
 ┃ ┃ ┣ 📂services
 ┃ ┃ ┣ 📂util
 ┃ ┃ ┗ 📂websocket❸
 ┃ ┃ ┃ ┣ 📂games
 ┃ ┃ ┃ ┃ ┗ 📜WalkingSimulator.java Ⓔ
 ┃ ┃ ┃ ┣ 📜BaseController.java Ⓐ
 ┃ ┃ ┃ ┣ 📜ChatController.java Ⓑ
 ┃ ┃ ┃ ┣ 📜LobbyController.java Ⓒ
 ┃ ┃ ┃ ┗ 📜LoginController.java Ⓓ
 ┃ ┗ 📂resources
 ┃ ┃ ┗ 📜application.properties
 ┣ 📜LICENSE
 ┣ 📜Makefile ❹
 ┣ 📜README.md
 ┣ 📜pom.xml
 ```

<sub>
1. **mappers:** A set of Mappers used to translate to/from a DTO(Data Transfer Object) to a Request or Response
2. **model:** POJOs representing different pieces of the application
    - A. Database Objects
    - B. Shared Enums used across our model
    - C. **request**: Client to Server request/ **response**: Server to Client responses
    - D. Used to save user information for in-memory cache
    - E. Inner objects shared across our model
3. **websocket:** WebSocket Connection Controllers
    - A. **BaseController:** Controller that is extended by all the other Websocket Controllers (except login). This is where our common logic lives
    - B. **ChatController:** Extended by controllers that use the chat functionality. Contains logic for sending messages to specific users and all users in a game
    - C. **LobbyController:** Controller backing the initial login screen
    - D. **LoginController:** Controller backing the main lobby, and game lobby screen
    - E. **WalkingSimulator:** Controller backing our example game
4. **MakeFile** File used to easily run our different commands
</sub>

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Running Application On DigitalOcean

There are lots of ways to deploy your application on the interwebs. I have used Google Cloud and you should be able to get that working for free, and I think that AWS has some free options. But I have recently started playing around with [Digital Ocean](https://m.do.co/c/5dca16f0ed95) and I have really like the interface and simplicity of it. The pricing is also very reasonable and if you are just creating a couple dropplets for your initial POC it is easy to turn them off and on so I am going to write the guide below assuming that you are using the DigitalOcean products and are new.

### Create Container Repository

DigitalOcean offers a way to create a container repository. The free level lets you create a single repo, should be able to follow the direction to get authenticated.

>**Note:** I chose to stick with the normal `docker` tool rather than their provided `doctl` cli.

### Build Image And Push

This repository includes a `.github/workflows` folder that will create a github workflow by default. But in order for it to work there are two secrets that will need to be added to your repo's "secrets" which can be done through the setting menu

TBD Create guide for adding secrets

#### Required Secrets:

| Name                      | Value                                                                                                       | Example                     |
| ------------------------- | ----------------------------------------------------------------------------------------------------------- | --------------------------- |
| DIGITALOCEAN_ACCESS_TOKEN | Token retrieved from the DO cloud ui. `API -> Generate New Token`                                           |                             |
| REGISTRY_BASE_URL         | Base url retrieved from the `Container` page. Should probably be `registry.digitalocean.com`                | `registry.digitalocean.com` |
| REGISTRY_NAME             | Registry name should be the part after the `/` so if your url looks like `registry.digitalocean.com/myrepo` | `myrepo`                    |
| DATABASE_URL              | Database Url (should include credentials) | `mongodb+srv://readwrite:abc123@mydatabasehost.gcp.mongodb.net/database?retryWrites=true&w=majority` |

### Create App

The application can be created by running one of the pipelines supplied by the `.github/workflows/create-app.yml`. Note that this is based on the `config/digitalocean/spec.yaml` file. Note this file can be customized based on the spec found [here](https://docs.digitalocean.com/products/app-platform/reference/app-spec/)

#### Spec Notes

**Repository:** Make sure to replace the repository information in the spec with your repo info. Should just be a name change.

**Machine:** Defaulting to a single instance of the most basic pod instance (512 mb of memory and 1 shared CPU). it is the cheapest option at $5 a month as of the writing of this README, and can easily be deleted and redeployed using this pipeline again. Note: If you want to test out your app with more than a couple people you can up your memory and cpu options with a different `slug` which you can find using the `doctl apps tier instance-size list` command.

**Region:** Defaults to your closest region but more regions can be found using the `doctl apps list-regions` command.

> Important: Be careful if you remove the `name` field. Doing so could result in multiple instances of your application being deployed which could result in an unpleasant bill. You should also throw a couple billing alerts on you DO just to be safe.

#### Run Workflow

A workflow has been added to the Github Workflows that takes care of the deployment of the application. It assumes the repository has already been created and the image has been deployed. And it also assumes that there is a "latest" tag.

This workflow will only be required to run once after the first deployment and should be run manually. More about [manual deployments](https://docs.github.com/en/actions/managing-workflow-runs/manually-running-a-workflow) can be found here.

After everything is set up navigate to the the workflows in the Github UI and run `Create Backend Application in Digital Ocean` workflow.

#### Validate Deployment

Easiest way to validate your deployment is by using the DO UI. Navigate to [cloud.digitalocean.com](cloud.digitalocean.com) > `Apps` and find the application named `lobby-example-app-backend`.

Find your app by hitting the `Live App`button to get the base URL and then navigate to the path `/q/health`

### Build Image Locally

Now that we are connected to our Image Registry we just need to build our image and push. Quarkus creates a couple different docker files for us, we are going to use the `src/main/docker/Dockerfile.jvm` file for our build. The `native` file is only intended for serverless use which we do not want for our application.

Container can be built locally and pushed to the registry with the following commands:

```sh
docker build -f src/main/docker/Dockerfile.jvm . -t registry.digitalocean.com/<YOUR_REPO_NAME>/lobby-example
docker push registry.digitalocean.com/<YOUR_REPO_NAME>/lobby-example
```

```sh
make build-docker-image:
```

### Create a Dropplet

Navigate to Dropplets on the side and choose "Create Dropplet"

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
