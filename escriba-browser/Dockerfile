FROM node:18-bullseye-slim

RUN npm i -g npm@latest &&\
    npm i -g @angular/cli &&\
    ng config -g cli.warnings.versionMismatch false

WORKDIR /escriba-browser