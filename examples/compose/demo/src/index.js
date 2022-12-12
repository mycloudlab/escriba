import {Escriba, HTTPTransport, Logger} from "@mycloudlab/escriba-browser"

let logger = null;

function init(){

    logger = Escriba.init({
        transport: new HTTPTransport("http://localhost:8888/api/logs")
    });

}

function fireInfo(){
    logger.info("this a info message");
    alert("info message fired");
}



init();