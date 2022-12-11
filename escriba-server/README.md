# LOG-SERVER



como fazer o build

# faz o build e habilita o debug remoto
./mvnw clean package quarkus:dev -DskipTests -DdebugHost=0.0.0.0 -Ddebug=8787 -Dlogserver.sourcemaps.allowed-domains=http://localhost:8080

# faz o build nativo sem gerar imagem
./mvnw package -Pnative -Dquarkus.native.container-build=false

# faz  o build do container do escriba 
podman build -f src/main/docker/Dockerfile.builder -t mycloudlab/escriba-server .