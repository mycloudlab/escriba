package io.github.mycloudlab.logserver;


import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.container.ContainerRequestContext;

import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection
@RequestScoped
public class ContainerRequestContextProducer {
  
    private ContainerRequestContext requestContext;

    public void set(ContainerRequestContext requestContext) {
    this.requestContext = requestContext;
    }

    @Produces
    @RequestScoped
    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }
    
}