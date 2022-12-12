import { Escriba, HTTPTransport, Logger } from "@mycloudlab/escriba-browser"

let logger = null;

function listenConsole(log) {


    ['info', 'trace', 'log', 'warn', 'error'].forEach(function (verb) {
        console[verb] = (function (method, verb, log) {
            return function (text) {
                method(text);
                var msg = document.createElement('code');
                msg.classList.add(verb);
                msg.textContent = text;
                log.prepend(msg);
            };
        })(console[verb].bind(console), verb, log);
    });

}


async function init() {

    // configure listener console
    var log = document.querySelector('#log');
    listenConsole(log);


    // initialize escriba logger
    logger = await Escriba.init({
        transport: new HTTPTransport("http://localhost:8888/api/logs")
    });

    // configure event listeners of buttons to fire logger
    document.querySelector("#syntax-error").addEventListener("click", () => {
        // a and b does not exists, force error to handle window.onerror
        // escriba automatic handle window.onerror

        a + b;
    });


    document.querySelector("#error").addEventListener("click", () => {
        let error = new Error("error");
        logger.error("this a error message", error);
    });

    document.querySelector("#warn").addEventListener("click", () => {
        logger.warn("this a warn message");
    });

    document.querySelector("#info").addEventListener("click", () => {
        logger.info("this a info message");
    });

    document.querySelector("#debug").addEventListener("click", () => {
        logger.info("this a debug message");
    });

    document.querySelector("#trace").addEventListener("click", () => {
        logger.warn("this a trace message");
    });

    document.querySelector("#mdc-set").addEventListener("click", () => {
        // set IP in mdc context data

        fetch('https://api.ipify.org').then(res => res.text()).then(ip => {
            logger.mdc.set('currentIP',ip);
            logger.info(`your current ip is ${ip}, set in mdc with currentIP entry`);
        });
    });

    document.querySelector("#mdc-clear-all").addEventListener("click", () => {
            logger.mdc.clearAll();
            logger.info(`mdc context clear`);
    });




}




init();