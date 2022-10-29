# Backend Lobby Server

The goal of this project is the creation of a basic lobby system with a frontend using Godot and a backend server built in Java using the Quarkus framework. For documentation on the the frontend check [this](https://github.com/Jaland/godot-lobby-frontend) repository.

**Language:** Java 11

**Framework:** [Quarkus](https://quarkus.io/)

## Prerequisites

* Java JDK 11+
* Maven 3.8+
* MongoDB Accessible on the internet
  * The easiest way I found to do this for free is with [Mongodb's Cloud Platform](https://cloud.mongodb.com/)

# File Structure

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

Once the server is up and running either locally or on a hosted provider, the Godot Frontend can be connected by modifying the `Websocket Host-> Hostname Url` as noted [here](https://github.com/Jaland/godot-lobby-frontend/blob/main/README.md)  in the `Updating Server Host Information` section.

## Installing On DigitalOcean

There are lots of ways to deploy your application on the interwebs. I have used Google Cloud in the past and you should be able to get that working for free or close too. I think that AWS may also have some free options. But I have recently started playing around with [Digital Ocean](https://m.do.co/c/5dca16f0ed95) and I have really like the interface and simplicity of it. The pricing is also very reasonable(not totally free). The rest of this demo assumes you are creating a single instance of the backend server at the lowest price for DO($5 a month). And keep in mind that the app can be deleted and redeployed fairly easily using the Github CI/CD process included with this repo and the rate is pro-rated for when the application is actually up. Meaning you can create an account deploy it play around with it for a couple hours then delete it and it will only cost you like $0.20

> **Tip:** Don't forget to delete the instance :)

### Create Container Repository

The GitHub CI/CD process specified later in this README builds our application as an image that is later deployed into a running container. In order to store this image we need access to an Image Registry.

[DigitalOcean offers a way to create a container repository](https://www.digitalocean.com/products/container-registry). The free level lets you create a single repo that will hold enough data to house at least one version of our image.

> Note: If you have access to a different container registry you would rather use that is fine, but you may need to make some changes to the Gitlab CI/CD Pipeline.

### Build Image And Push

This repository includes a `.github/workflows` folder that will create a Github Workflow by default. But in order for it to work there are two secrets that will need to be added to your repo's "secrets" which can be done through the setting menu

> Note: You will need to activate your Github Workflows which can be done through the `Actions` tab on your repo

> Important: Since we are using the `doctl` command (DO's proprietary CLI) if you want to use a different repo you will need to modify the CI pipeline to use `docker` instead.

#### Required Secrets

| Name                      | Value                                                                                                       | Example                     |
| ------------------------- | ----------------------------------------------------------------------------------------------------------- | --------------------------- |
| DIGITALOCEAN_ACCESS_TOKEN | Token retrieved from the DO cloud ui. `API -> Generate New Token`<br/><br/> <sub>See `Creating Repository Secrets` section of the frontend README for more info<sub>                                           |                             |
| REGISTRY_BASE_URL         | Base url retrieved from the `Container` page. Should probably be `registry.digitalocean.com`                | `registry.digitalocean.com` |
| REGISTRY_NAME             | Registry name should be the part after the `/` so if your url looks like `registry.digitalocean.com/myrepo` it would be `myrepo` | `myrepo`                    |
| DATABASE_URL              | Database Url (should include credentials) <br /> **IMPORANT:** You must escape all of the `&`s with a `\` | `mongodb+srv://readwrite:abc123@mydatabasehost.gcp.mongodb.net/database?retryWrites=true\&w=majority` |

#### Push Image

The pipeline associated with pushing the image to the image repository is `.github/workflows/deploy-image`. Based on the `push` section located at line 10 you will note that the workflow should be run based on any commit to the `main` branch that makes a change to the src folder, pom.xml, etc... (assuming workflows were activated). So the easiest way to test the workflow is by just adding a space to the end of the `pom.xml` and pushing a commit.

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

* WebSockets ([guide](https://quarkus.io/guides/websockets)): WebSocket communication channel support

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

## Useful Links

Git Actions Status (incase your actions stop running suddenly): <https://www.githubstatus.com/>
