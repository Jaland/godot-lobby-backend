# IP_ADDRESS should be set through an environment variable and the following line commented out
IP_ADDRESS=162.243.168.138
USER=root
SHELL = /bin/bash
JAR_NAME=lobby-example-1.0.0-SNAPSHOT.jar

build:
	mvn clean install

start:
	mvn quarkus:dev -Dquarkus.profile=local

build-and-start: build start

build-docker-image:
	docker build -f src/main/docker/Dockerfile.jvm -t quarkus/lobby-example .
	docker save quarkus/lobby-example > lobby-image.tar

# Used to push to droplet (deprecated)
push-to-droplet: 
	ssh $(USER)@$(IP_ADDRESS) "rm -rf /opt/java && mkdir -p /opt/java"
	rsync target/quarkus-app/quarkus-run.jar $(USER)@$(IP_ADDRESS):/opt/java/lobby.jar
	ssh $(USER)@$(IP_ADDRESS) "cd /opt/java && java -jar lobby.jar"
