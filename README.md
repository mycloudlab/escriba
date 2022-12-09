# Escriba

Escriba is a complete solution for receiving and processing javascript logs with support for processing sourcemaps, integrated with Loki, Promtail, Grafana.

Problems can occur, no one is immune to that. Log analysis is one of the development activities. However, javascript log processing is a pain point in many frontend architectures. Many frontend developers end up unaware, ignoring the error logs of the frontend layer. Corrections take longer, as there is a delay in identifying errors, since there is no proper collection.

Escriba Stack comes to remedy this point. The solution is grouped into:

- **escriba-browser** - javascript library for processing logging in the browser and sending it to the server.
- **escriba-angular** - library that adds logging functionality to angular applications.
- **escriba-server** - application server that receives logs from the browser, processing the sourcemap properly.

In addition, the project uses Promtail to forward the logs to Loki for storage and a grafana dashboard for viewing errors.

## Features

- multiple levels of frontend and server logs
- MDC (mapped diagnostic context) is supported
- proper processing of sourcemaps on the server side
- restriction on which domains are accepted for processing
- customization and transformation of the request to obtain the application's source-map.

## How to use

An example docker-compose for running the application is provided:

```bash
# clone escriba project
git clone https://github.com/mycloudlab/escriba.git

# enter docker-compose examples folder
cd escriba/examples/compose

# start demo
docker-compose up -d
```

The following services will start:

| Service          | Description                                                             | host binding ports                   |
| ---------------- | ----------------------------------------------------------------------- | ------------------------------------ |
| Loki             | system for storing logs from the frontend                               |                                      |
| promtail         | system for receiving generated logs and sending them to loki            |                                      |
| grafana          | dashboard for viewing frontend logs                                     | 3000 : 3000 - ui dashboard           |  
| escriba-server   | server that receives frontend logs and processes sourcemaps accordingly | 8888 : 8888 - endpoint log receiver  |
| escriba-demo     | demo application of the platform's logging features                     | 8080 : 8080 - frontend demo app      |
 
Access the demo at http://localhost:8080 and click on the log trigger buttons. then view the logs on the grafana dashboard at http://localhost:3000

for other available examples see the examples folder.

## How to contribute

This project is based on docker-compose, we use podman and podman-compose as runtime.

There is a docker-compose at the root of the project, it is responsible for initializing the development environment of the solution.

1. Install podman and podman-compose.

2. Create an alias in ~/.bashrc with the following command:
```bash
echo "alias dc='PODMAN_USERNS=keep-id podman-compose'">> ~/.bashrc
```
3. Clone the project
```bash
git clone https://github.com/mycloudlab/escriba.git
```

4. Build the environment:
```bash
cd escriba
dc build .
```

5. start the environment:
```bash
dc up -d
```

6. access the component you want to evolve:

dc exec [ browser | angular | server ] bash

for example, if you are going to contribute to the server use:
```bash
dc exec server bash
``` 

7. send the pull request

### Technical details of the projects:

**escriba-browser** - library written in typescript, consult package.json to view available commands.

**escriba-angular** - library written in typescript and angular to provide angular modules for handling logging, consult package.json to view the available commands.

**escriba-server** - application written in java using quarkus.

