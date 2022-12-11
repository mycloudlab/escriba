FROM node:18-bullseye-slim

RUN npm i -g npm@latest &&\
    npm i -g @angular/cli &&\
    ng config -g cli.warnings.versionMismatch false




FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:22.3-java17 as builder

# copy source code
ADD --chown=quarkus:quarkus . /escriba-server 


# run build
RUN cd /escriba-server && \
    ./mvnw clean package -Pnative -Dquarkus.native.container-build=false


FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /work/
COPY --from=builder /escriba-server/target/*-runner /work/escriba-server

ENV QUARKUS_HTTP_PORT=8888 \
    LOGSERVER_SOURCEMAPS_ALLOWED_DOMAINS= \
    LOGSERVER_SOURCEMAPS_TRANSLATE_DOMAINS= \
    LOGSERVER_MDC_EXTRA_LABELS= \
    LOGSERVER_SOURCEMAPS_REQUEST_EXTRA_HEADERS= \
    LOGSERVER_GELF_HOST=udp:promtail \
    LOGSERVER_GELF_PORT=12201 \
    LOGSERVER_LOG_LEVEL=ALL

RUN chmod 775 /work

EXPOSE 8888

CMD ["./escriba-server"]