[![Maintainability](https://api.codeclimate.com/v1/badges/59f16110623a9b5801d2/maintainability)](https://codeclimate.com/github/petrmac/crag-db/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/59f16110623a9b5801d2/test_coverage)](https://codeclimate.com/github/petrmac/crag-db/test_coverage)

# Crag DB
A project of route database for sport climbers. I wanted to learn new things and discover the capabilities of the Neo4j and Axon.

## Prerequisites
- `Java 21`
- `skaffold`
- `minikube` or other local kubernetes

For minikube please follow the instructions on the [official site](https://minikube.sigs.k8s.io/docs/start/).


## How to run
First run the `minikube` and switch to the proper context.

Then issue the following:

```shell
./gradlew clean build
skaffold dev
```

This will start the app with all dependencies in the kubernetes cluster.
The application does not have any ingress or UI configured yet, the GraphQL API can be accessed via port-forwarding.


## Local build

Start docker containers via the compose file below and run app in your IDE.

### Run docker-compose

This will start in interactive mode and you can see the logs.
```shell
docker compose up 
```

Alternatively, you can run it in the background.
```shell
docker compose up -d
```
