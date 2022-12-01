package io.github.mycloudlab.logserver;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mockito.Mockito;

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
	public void printStackTrace(PrintStream arg0) {
		arg0.println("");
		for (var el : getStackTrace()) {
			arg0.println(el.toString());
		}
	}

	@Override
	public void printStackTrace(PrintWriter arg0) {
		arg0.println("");
		for (var el : getStackTrace()) {
			arg0.println(el.toString());
		}
	}

	@Override
	public void printStackTrace() {
		System.out.println("JavascriptException.printStackTrace()");
		super.printStackTrace();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return stackframes.parallelStream().<StackTraceElement>map((stackframe) -> {
			StringBuffer sb = new StringBuffer();

			sb.append(stackframe.getFunctionName());
			sb.append("(");
			sb.append(stackframe.getFileName());
			sb.append(":");
			sb.append(stackframe.getLineNumber());
			sb.append(":");
			sb.append(stackframe.getColumnNumber());
			sb.append(")");

			String randomValue = UUID.randomUUID().toString();

			// we use mockito spy to override the default behavior of jvm because we want
			// the stackElement
			// to be the javascript errors, done to allow integration with java logging
			// frameworks,
			// so it would not be necessary to override the behavior as it would seem like a
			// java error.
			// If you have any better ideas we are open to suggestions, we await your pull
			// request.

			StackTraceElement stacktraceElement = Mockito
					.spy(new StackTraceElement(randomValue, randomValue, randomValue, 1));
			Mockito.when(stacktraceElement.toString()).thenReturn(sb.toString());

			return stacktraceElement;
		}).collect(Collectors.toList()).toArray(new StackTraceElement[] {});

	}

}