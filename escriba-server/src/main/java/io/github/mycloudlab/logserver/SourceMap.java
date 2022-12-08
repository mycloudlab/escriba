package io.github.mycloudlab.logserver;

import com.google.debugging.sourcemap.SourceMapping;
import com.google.debugging.sourcemap.proto.Mapping.OriginalMapping;

/**
 * decorator class for sourcemapping processor
 *
 */
public class SourceMap {

	private SourceMapping sourceMapping;

	public SourceMap() {
	}

	public SourceMap(SourceMapping sourceMapping) {
		this.sourceMapping = sourceMapping;
	
	}

	/**
	 * find correct trace from sourcemap
	 * 
	 * @param stackFrame
	 * @return new StackFrame with new positions
	 */
	public StackFrame locate(StackFrame stackFrame) {
		
		if (sourceMapping == null)
			return stackFrame;
		
		OriginalMapping mapping = sourceMapping.getMappingForLine(stackFrame.getLineNumber(), stackFrame.getColumnNumber());
		
		StackFrame novoRastro = new StackFrame(stackFrame);
		novoRastro.setFileName(mapping.getOriginalFile());
		novoRastro.setLineNumber(mapping.getLineNumber());
		novoRastro.setColumnNumber(mapping.getColumnPosition());

		return novoRastro; 
	}

}