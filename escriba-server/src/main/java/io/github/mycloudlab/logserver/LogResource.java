package io.github.mycloudlab.logserver;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.MDC;

@RequestScoped
@Path("/logs")
public class LogResource {

    Logger LOG = Logger.getLogger("jslog");
    
    private CacheSourceMaps sourceMaps;

    private LogServerConfig config;
    
    @Inject
    public LogResource(CacheSourceMaps sourceMaps,LogServerConfig config) {
        this.sourceMaps = sourceMaps;
        this.config = config;
    }

    @POST
    public Response logReceiver(LogData data) {
        for (ServerStackTrace log : data.getLogs()) {
            List<StackFrame> localizedStackFrame = log.getStacktrace().stream()
                    .map((stackframe) -> {
                        SourceMap sm = sourceMaps.load(stackframe.getFileName());
                        return sm.locate(stackframe);
                    }).collect(Collectors.toList());

            JavascriptException javascriptException = new JavascriptException(log.getMessage(), localizedStackFrame);


            // fill mdc
            log.getMdc().forEach((key, value) -> MDC.put(key, value));
            config.fillMDCContext();

            // log
            LOG.log(Level.valueOf(log.getLevel()), log.getMessage(), javascriptException);
            
        }
    
        return Response.ok().build();
    }

}
