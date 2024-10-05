# Crag DB


## Axon Server
```shell
docker run --rm -p 8024:8024 -p 8124:8124 axoniq/axonserver:2024.1.2-jdk-17-nonroot
```

## Neo4j
```shell
docker run \
    --restart always \
    --publish=7474:7474 --publish=7687:7687 \
    --env NEO4J_AUTH=neo4j/verysecret \
    neo4j
```