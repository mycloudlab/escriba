package io.github.mycloudlab.logserver;


import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import io.quarkus.arc.Arc;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JaxrsGatewayCDI implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var container = Arc.container().instance(ContainerRequestContextProducer.class).get();
        container.set(requestContext);
    }
}