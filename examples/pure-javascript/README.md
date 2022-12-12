# Escriba stack demo

in this example we show how to run the complete log collection solution. The demo should not be used in production, for productive use we suggest looking at docker-compose in the examples/production/docker-compose.yml folder.


## Usage 
To use this demo you need to have installed docker-compose or podman-compose.

The project starts the following services:


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

