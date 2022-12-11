# Escriba-Browser

This library is part of the mycloudlab/escriba solution. It serves to collect the logs and send the logging data from the frontend to the backend.

## Features

- Multiple levels of frontend and server logs
- Multiple transport formats fetch, xhr, websockets
- Send logs with no blocking Main Thread
- MDC (mapped diagnostic context) is supported
- Ignore source map processing in frontend, source maps are processed in backend

## Usage

To use the library you can do via unpkg cdn:

```html
<script type="module" src="https://unpkg.com/stacktrace-js@2.0.2/dist/stacktrace.min.js"></script>
<script type="module" src="https://unpkg.com/@mycloudlab/escriba-browser/dist/escriba.min.mjs"></script>
```

or use npm install:

```bash
npm i @mycloudlab/escriba-browser 
```

Now it is necessary to initialize Escriba:

```typescript
import { Escriba, HTTPTransport } from '@mycloudlab/escriba-browser';

const logger = await Escriba.init({
    transport: new HTTPTransport("http://localhost:8888/api/logs")
});

// use 
logger.warn("this a warn log message");
logger.info("this a info log message");
logger.debug("this a debug log message");
logger.error("an error occurred",errorObject);
logger.trace("this a trace log message");
```
Configure the transport to send the log to the server, modify the server address to the correct address where the service is running: mycloudlab/escriba-server.

### config initialization

Escriba is very configurable, below are the possible initialization parameters:

| Parameter name           | Description                                                                                                         | Options                                          | Default        |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ | -------------- |
| level                    | Used to determine browser logging level                                                                             | OFF, ERROR, WARN, INFO, DEBUG, TRACE             | LogLevel.TRACE |
| enableServerLogging      | determines whether logs should be sent to the server                                                                | true, false                                      | true           |
| serverLogLevel           | determines what level of logging will be sent to the server                                                         | OFF, ERROR, WARN, INFO, DEBUG, TRACE             | LogLevel.TRACE | 
| transport                | determines how the log will be sent to the server                                                                   | NOOPTransport, HTTPTransport, WebSocketTransport | NOOPTransport  |
| browserErrorHandler      | defines whether to manage browser errors globally, defining a Listener in the window.onerror event                  | true, false                                      | true           |
| instrumentConsoleLogging | defines whether to override the default console api behavior for the log, error, info, trace, debug, warn functions | true, false                                      | true           |
 
### Log Level

Log levels are hierarchical, so when defining:

| Log Level      | Hierarchy  
| -------------- | -----------------------------------------------------|
| OFF            | no logs is processed                                 |
| ERROR          | only error logs is processed                         |
| WARN           | error and warn logs is processed                     |
| INFO           | error, warn and info logs is processed               |
| DEBUG          | error, warn, info and debug logs is processed        |
| TRACE          | error, warn, info, debug and trace logs is processed |

### Transport

Each transport has specific parameters for its correct initialization. Below is a description of each available transport:

#### NOOPTransport

This transport does not send the message anywhere.

#### HTTPTransport

The HTTPTransport sends the requests using fetch api, it is necessary to pass the address of the backend api in the constructor.

#### WebSocketTransport

Websocket transport is being implemented.

### Mapped Diagnostic Context (MDC)

Mapped Diagnostic Context provides a way to enrich log messages with information that could be unavailable in the scope where the logging actually occurs but that can be indeed useful to better track the execution of the program.

MDC is introduced to address this gap. This will help developers to store specific information in its internal data structure and append the information to the log as per configuration.

For example, when an error occurs, it can be useful to know which user was logged into the application that the error occurred, or let's say that tracing is being used to track the user's activity, it is useful to add the traceId in the log to correlate the information from trace with the messages in the log.

to use the MDC the escriba provides the following methods:

```typescript
// put data into mdc context
logger.mdc.set(key: string, value: string);

// unset key in mdc context 
logger.mdc.clear(key: string);

// clear all keys in mdc context
logger.mdc.clearAll();

// retrive data mdc context
logger.mdc.entries();
```

the information placed in the mdc context is sent for each generated log.

For the information to appear correctly in Loki, it is necessary to configure the promtail to store the information sent via MDC.

