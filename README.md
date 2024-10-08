# Crag DB
A project of route database for sport climbers. I wanted to learn new things and discover the capabilities of the Neo4j and Axon.

## Prerequisites
- `Java 21`
- `skaffold`
- `minikube` or other local kubernetes


## How to run
First run the `minikube` and switch to the proper context.

Then issue the following:

```shell
./gradlew clean build
skaffold dev
```

This will start the app with all dependencies in the kubernetes cluster.

## Local build

Start docker containers below and run app.

### Neo4j
```shell
docker run \
    --restart always \
    --publish=7474:7474 --publish=7687:7687 \
    --env NEO4J_AUTH=neo4j/verysecret \
    neo4j
```

### Axon Server
```shell
docker run --rm -p 8024:8024 -p 8124:8124 axoniq/axonserver:2024.1.2-jdk-17-nonroot
```
