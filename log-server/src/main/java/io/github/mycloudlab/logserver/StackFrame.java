package io.github.mycloudlab.logserver;

public class StackFrame {

    private String functionName;
	private String fileName;
	private int lineNumber;
	private int columnNumber;

    public StackFrame() {
	}

	public StackFrame(StackFrame stackframe) {
		this.functionName = stackframe.functionName;
		this.fileName = stackframe.fileName;
		this.lineNumber = stackframe.lineNumber;
		this.columnNumber = stackframe.columnNumber;
	}
    
    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    public int getColumnNumber() {
        return columnNumber;
    }
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
}
