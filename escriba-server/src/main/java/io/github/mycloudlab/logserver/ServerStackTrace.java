package io.github.mycloudlab.logserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerStackTrace {

    private String datetime;
    private String level;
    private String message;
    private Map<String,String> mdc = new HashMap<>();
    private List<StackFrame> stacktrace = new ArrayList<>();

    public String getDatetime() {
        return datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Map<String, String> getMdc() {
        return mdc;
    }
    public void setMdc(Map<String, String> mdc) {
        this.mdc = mdc;
    }
    public List<StackFrame> getStacktrace() {
        return stacktrace;
    }
    public void setStacktrace(List<StackFrame> stacktrace) {
        this.stacktrace = stacktrace;
    }
   
}
