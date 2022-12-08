# LOG-SERVER

Agregador de logs do frontend com processamento de sourcemaps.

Este projeto executa um servidor que recebe o log do browser, fazendo o processamento do sourcemap, também permite o envio dos dados do log para agregação de logs, como loki, fluentd, graylog, etc...



# Como usar ?

A forma mais fácil de executar é usando uma imagem docker fornecida:

```bash
docker run --net=host --name log-server \
    -p 8888:8888 \
    -e "LOGSERVER_ALLOWED_DOMAINS=localhost" \
    mycloudlab/log-server
```
Execute um frontend para testar o log, a imagem abaixo contém um frontend que faz o teste da coleta de logs para o servidor:

```bash
docker run -p 8080:8080 mycloudlab/log-server-test
```

Acesse a URL: http://localhost:8080/

# Features

- Coleta de logs com suporte a MDC (Mapped Diagnostic Context).
- Processamento de sourcemap 
- Envio de logs para agregador de logs
- Tradução de domínios para coleta de sourcemap
- Integração com agregador de logs eg. Promtail/Loki

# Arquitetura

fazer o desenho arquitetural.

# Integração com Loki



# TODO 

- integrar com kafka para processamento assincrono

# Como contribuir ?

