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
    OFF = "off",
    ERROR = "error",
    WARN = "warn",
    INFO = "info",
    DEBUG = "debug",
    TRACE = "trace"
}

/**
 * LogMessage represent one message log
 * 
 */
export type LogMessage = {
    level: LogLevel;
    message: string;
    mdc: { [key: string]: any };
    stacktrace: Array<StackFrame>;
}


/**
 * interface to use sender transport 
 */
export interface Transport {

    init(config: EscribaConfig): Promise<Transport>;
    send(log: LogMessage): void;
}

/**
 * NOOPTransport implementation usage of transport 
 */
export class NOOPTransport implements Transport {

    init(config: EscribaConfig): Promise<Transport> {
        return Promise.resolve(this);
    }
    send(log: LogMessage): void {
    }
}

/**
 * this implementation of Transport use fetch api to send data to server
 */
export class HTTPTransport implements Transport {

    constructor(private url: String) { }

    init(config: EscribaConfig): Promise<Transport> {
        return Promise.resolve(this);
    }
    send(log: LogMessage): void {
    }

}

/**
 * this implementation of Transport use web socket to send data to server
 */
export class WebSocketTransport implements Transport {

    init(config: EscribaConfig): Promise<Transport> {
        throw new Error("Method not implemented.");
    }

    send(log: LogMessage): void {
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
            this.error(event.message, event.error);
        });
    }

    private instrumentConsoleLogging() {
        if (!this.config.browserErrorHandler)
            return;

        // TODO implements
    }

    private async log(level: LogLevel, message: string, error?: Error): Promise<void> {
        let stackFrame: StackFrame[];
        
        if (error == undefined) 
            stackFrame = await StackTrace.get({ offline: false });
        else
            stackFrame = await StackTrace.fromError(error, { offline: false });

        let log: LogMessage = {
            level: LogLevel.INFO,
            message: message,
            mdc: { ...this.mdc.entries() },
            stacktrace: stackFrame
        };

        if (log.level <= (this.config.level ?? LogLevel.OFF))
            this.report(log);

        if (log.level <= (this.config.serverLogLevel ?? LogLevel.OFF))
            this.config.transport?.send(log);
    }

    private report(log: LogMessage) {
       console.log(log);
    }

    async warn(message: string): Promise<void> {
        return this.log(LogLevel.INFO, message);
    }

    async info(message: string): Promise<void> {
        return this.log(LogLevel.INFO, message);
    }

    async debug(message: string): Promise<void> {
        return this.log(LogLevel.INFO, message);
    }

    async trace(message: string): Promise<void> {
        return this.log(LogLevel.INFO, message);
    }

    async error(message: string, error: Error) {
        return this.log(LogLevel.INFO, message, error);
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
