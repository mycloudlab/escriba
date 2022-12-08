import { report, StackFrame } from 'stacktrace-js'
/**
 * Escriba config logging 
 */
export type EscribaConfig = {
    /**
     * log level in console browser 
     */
    level?: LogLevel;

    /**
     * log messages sended to server? 
     */
    enableServerLogging?: boolean;

    /**
     * log level messages sended to server
     */
    serverLogLevel?: LogLevel;

    /**
     * transport usage to send logs to server
     */
    transport?: Transport;
    /**
     * automatic attach onerror eventlistener to handle browser errors
     */
    browserErrorHandler?: boolean;

    /**
     * instrument console.log functions to redirect messages do escriba.
     */
    instrumentConsoleLogging?: boolean;
};

/**
 * log levels 
 */
export enum LogLevel {
    OFF = "OFF",
    ERROR = "ERROR",
    WARN = "WARN",
    INFO = "INFO",
    DEBUG = "DEBUG",
    TRACE = "TRACE"
}

const levelCode: any = {
    'OFF': 0,
    'ERROR': 1,
    'WARN': 2,
    'INFO': 3,
    'DEBUG': 4,
    'TRACE': 5
};

/**
 * LogMessage represent one message log
 * 
 */
export type LogMessage = {
    level: LogLevel;
    datetime: string;
    message: string;
    mdc: { [key: string]: any };
    stacktrace: Array<StackFrame>;
}

const consoleFn: { [key: string]: Function } = {
    error: console.error,
    warn: console.warn,
    info: console.info,
    debug: console.debug,
    trace: console.trace
};


/**
 * interface to use sender transport 
 */
export interface Transport {

    init(config: EscribaConfig): Promise<Transport>;
    send(log: LogMessage, ...extra: any): void;
}

/**
 * NOOPTransport implementation usage of transport 
 */
export class NOOPTransport implements Transport {

    init(config: EscribaConfig): Promise<Transport> {
        return Promise.resolve(this);
    }
    send(log: LogMessage, ...extra: any): void {
    }
}

/**
 * this implementation of Transport use fetch api to send data to server
 */
export class HTTPTransport implements Transport {
   

    constructor(private url: string) { }

    init(config: EscribaConfig): Promise<Transport> {
        return Promise.resolve(this);
    }

    send(log: LogMessage, ...extra: any): void {
        const requestOptions = {
            method: 'POST',
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({ logs: [log] })
        };
        fetch(this.url, requestOptions);
    }

}

/**
 * this implementation of Transport use web socket to send data to server
 */
export class WebSocketTransport implements Transport {

    init(config: EscribaConfig): Promise<Transport> {
        throw new Error("Method not implemented.");
    }

    send(log: LogMessage, ...extra: any): void {
    }

}

export const defaultConfig: EscribaConfig = {
    level: LogLevel.TRACE,
    enableServerLogging: false,
    serverLogLevel: LogLevel.TRACE,
    transport: new NOOPTransport(),
    browserErrorHandler: true,
    instrumentConsoleLogging: true
};

export class MDC {

    currentMDCContext: { [key: string]: any } = {};

    /**
     * define key to mdc
     * 
     * @param key 
     * @param value 
     */
    set(key: string, value: any) {
        this.currentMDCContext[key] = value;
    }

    /**
     * 
     * @param key 
     */
    clear(key: string) {
        delete this.currentMDCContext[key];
    }
    /**
     * clear all MDC context entries
     */
    clearAll() {
        this.currentMDCContext = {};
    }

    entries(): { [key: string]: any } {
        return this.currentMDCContext;
    }
}

export class Logger {

    public mdc: MDC = new MDC();



    constructor(private config: EscribaConfig) {
        this.configureBrowserErrorHandler();
        this.instrumentConsoleLogging();
    }

    private configureBrowserErrorHandler() {
        if (!this.config.browserErrorHandler)
            return;

        window.addEventListener('error', (event: ErrorEvent) => {
            event.preventDefault();
            this.error(event.error.message, event.error);
        });
    }

    private instrumentConsole(method: string) {
        method = method.toLowerCase();
        let consoleWrapper: any = console;
        let me: any = this;
        if (consoleWrapper[method] !== me[method]) {
            consoleFn[method] = consoleWrapper[method];
            consoleWrapper[method] = function (log: any) {
                return function (...args: any) {
                    log[method](...args);
                };
            }(this);
        }

    }


    private instrumentConsoleLogging() {
        if (!this.config.browserErrorHandler)
            return;

        this.instrumentConsole('info');
        this.instrumentConsole('debug');
        this.instrumentConsole('trace');
        this.instrumentConsole('warn');
        this.instrumentConsole('error');

    }

    private async log(level: LogLevel, message: string, error?: Error, ...extra: any): Promise<void> {
        let stackFrame: StackFrame[];

        if (error == undefined)
            stackFrame = []; //await StackTrace.get({ offline: false });
        else
            stackFrame = await StackTrace.fromError(error, { offline: false });

        let dt = new Date();
        let datetime = `${dt.toLocaleDateString('sv')} ${dt.toLocaleTimeString('sv')}.${dt.getMilliseconds().toString().padStart(3, '0')}`;



        let log: LogMessage = {
            level: level,
            datetime: datetime,
            message: message,
            mdc: { ...this.mdc.entries() },
            stacktrace: stackFrame
        };

        if (levelCode[log.level.toString()] <= (levelCode[this.config.level?.toString() ?? 'off']))
            this.report(log, error, ...extra);

        if (levelCode[log.level.toString()] <= (levelCode[this.config.serverLogLevel?.toString() ?? 'off'])) {
            
            this.config.transport?.send(log, ...extra);
        }
    }

    private report(log: LogMessage, error?: Error, ...extra: any) {
        let line = `[${log.datetime}] - ${log.level} - ${JSON.stringify(log.mdc)} - ${log.message}`;
        let lineError = `[${log.datetime}] - ${log.level} - ${JSON.stringify(log.mdc)}`;
        if (log.level == LogLevel.ERROR)
            consoleFn[log.level.toLocaleLowerCase()](lineError, error, ...extra);
        else if (extra !== undefined)
            consoleFn[log.level.toLocaleLowerCase()](line, ...extra);
        else
            consoleFn[log.level.toLocaleLowerCase()](line);
    }

    async warn(message: string, ...extra: any): Promise<void> {
        return this.log(LogLevel.WARN, message, undefined, ...extra);
    }

    info(message: string, ...extra: any) {
        this.log(LogLevel.INFO, message, undefined, ...extra);
    }

    async debug(message: string, ...extra: any): Promise<void> {
        return this.log(LogLevel.DEBUG, message, undefined, ...extra);
    }

    async trace(message: string, ...extra: any): Promise<void> {
        return this.log(LogLevel.TRACE, message, undefined, ...extra);
    }

    async error(message: string, error: Error) {
        return this.log(LogLevel.ERROR, message, error);
    }
}

export class Escriba {

    static async init(config: EscribaConfig): Promise<Logger> {
        let mergedConfig: EscribaConfig = Object.assign({}, defaultConfig, config);

        await mergedConfig.transport?.init(mergedConfig);
        let logger: Logger = new Logger(mergedConfig);

        return Promise.resolve(logger);
    }
}
