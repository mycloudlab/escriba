package io.github.mycloudlab.logserver;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import org.jboss.logging.Logger.Level;

@RequestScoped
@Path("/logs")
public class LogResource {

    Logger LOG = Logger.getLogger("jslog");

    private CacheSourceMaps sourceMaps;

    private LogServerConfig config;

    private ContainerRequestContext context;

    @Inject
    public LogResource(CacheSourceMaps sourceMaps, LogServerConfig config, ContainerRequestContext context) {
        this.sourceMaps = sourceMaps;
        this.config = config;
        this.context = context;
    }

    @POST
    public Response logReceiver(LogData data) {
        List<String> origins = context.getHeaders().get("Origin");
        final AtomicBoolean allowed = new AtomicBoolean(false);
        origins.forEach((domain) -> {
            if (config.domainIsAllowed(domain))
                allowed.set(true);;
        });

        if (!allowed.get()) 
            return Response.status(Status.FORBIDDEN).build();

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
