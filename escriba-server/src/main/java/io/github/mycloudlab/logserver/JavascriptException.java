package io.github.mycloudlab.logserver;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;


/**
 * class usaged for log stacktrace in javascript format
 * 
 *
 */
public class JavascriptException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private List<StackFrame> stackframes;

	public JavascriptException(String message, List<StackFrame> stackframes) {
		super(message);
		this.stackframes = stackframes;
	}

	@Override
	public void printStackTrace(PrintStream ps) {
		ps.println("");
		for (var stackframe : stackframes) {
			ps.append(stackframe.getFunctionName());	
			ps.append("(");
			ps.append(stackframe.getFileName());
			ps.append(":");
			ps.append(Integer.toString(stackframe.getLineNumber()));
			ps.append(":");
			ps.append(Integer.toString(stackframe.getColumnNumber()));
			ps.append(")");
			ps.println("");
		}
	}

	@Override
	public void printStackTrace(PrintWriter ps) {
		ps.println("");
		for (var stackframe : stackframes) {
			ps.append(stackframe.getFunctionName());	
			ps.append("(");
			ps.append(stackframe.getFileName());
			ps.append(":");
			ps.append(Integer.toString(stackframe.getLineNumber()));
			ps.append(":");
			ps.append(Integer.toString(stackframe.getColumnNumber()));
			ps.append(")");
			ps.println("");
		}
	}

	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

}