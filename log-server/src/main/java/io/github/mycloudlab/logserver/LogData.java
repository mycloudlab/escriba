package io.github.mycloudlab.logserver;

public class LogData {

    private ServerStackTrace[] logs;

    public ServerStackTrace[] getLogs() {
        return logs;
    }

    public void setLogs(ServerStackTrace[] logs) {
        this.logs = logs;
    }

}
