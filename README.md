# JSLOGK

Stack completa para recebimento de logs javascript do browser para o servidor.

O projeto permite que o sourcemap seja processado no servidor, permitindo integração com agregadores de log como loki, fluentd, logstash ou graylog.

# Como funciona?

anexar aqui video mostrando como configurar

# Como usar?

```bash
npm i jslog-stack
```




https://itnext.io/step-by-step-building-and-publishing-an-npm-typescript-package-44fe7164964c


Projeto para coleta de logs no frontend.

**Organização do projeto:**
- **jslog:** Biblioteca javascript que efetua o logging no servidor
- **jslog-angular:** Wrapper que fornece os módulos para gerenciamento de logs na plataforma angular
- **log-server:** código de servidor que efetua o recebimento dos logs e o processamento

Os logs são então processados conforme configuração da aplicação.

Sugerimos enviar os logs para um servidor central como graylog, loki, ou outra solução.





environment:
    ALLOWED_DOMAINS: (.*).plataformaeducar.com.br
    TRANSLATE_DOMAINS: (.*).plataformaeducar.com.br=localhost:4200
    MDC_EXTRA_LABELS: layer:frontend
