package io.github.mycloudlab.logserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.apache.hc.client5.http.fluent.Request;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.MDC;

@ApplicationScoped
public class LogServerConfig {

    private List<Pattern> allowedDomains = new ArrayList<>();
    private Map<Pattern, URL> translatedDomains = new HashMap<>();
    private Map<String, String> mdcExtraLabels = new HashMap<>();
    private Map<String, String> sourceMapsRequestHeaders = new HashMap<>();

    public LogServerConfig(
            @ConfigProperty(name = "logserver.sourcemaps.allowed-domains") String _allowedDomains,
            @ConfigProperty(name = "logserver.sourcemaps.translate-domains") String _translateDomains,
            @ConfigProperty(name = "logserver.mdc.extra-labels") String _mdcExtraLabels,
            @ConfigProperty(name = "logserver.sourcemaps.request-extra-headers") String _sourceMapsRequestHeaders) {
        try {
            // configure allowed domains
            for (String domain : _allowedDomains.split(","))
                allowedDomains.add(Pattern.compile(domain));

            // configure translate domains
            for (String keyval : _translateDomains.split(",")) {
                Pattern key = Pattern.compile(keyval.split("=")[0]);
                URL value;
                value = new URL(keyval.split("=")[1]);
                translatedDomains.put(key, value);
            }

            // configure extra labels mdc
            for (String keyval : _mdcExtraLabels.split(",")) {
                String key = keyval.split(":")[0];
                String value = keyval.split(":")[1];
                mdcExtraLabels.put(key, value);
            }

            for (String keyval : _sourceMapsRequestHeaders.split(",")) {
                String key = keyval.split(":")[0];
                String value = keyval.split(":")[1];
                sourceMapsRequestHeaders.put(key, value);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("configuration problem", e);
        }
    }

    public boolean domainIsAllowed(String domain) {
        for (Pattern allowedDomain : allowedDomains)
            if (allowedDomain.matcher(domain).matches())
                return true;
        return false;
    }

    public String translate(String url) {
        try {
            URL originalURL = new URL(url);
            String domain = originalURL.getAuthority();
            for (Pattern originalDomain : translatedDomains.keySet()) {
                if (originalDomain.matcher(domain).matches()) {
                    URL target = translatedDomains.get(originalDomain);
                    URL newURL = new URL(target.getProtocol(), target.getHost(), target.getPort(),
                            originalURL.getFile());
                    return newURL.toString();
                }
            }
            return url;
        } catch (Exception e) {
            throw new RuntimeException("error to translate url", e);
        }
    }

    public void fillMDCContext() {
        mdcExtraLabels.forEach((key, val) -> {
            MDC.put(key, val);
        });
    }

    public Request fillRequestExtraHeaders(Request request){
        for (var key : sourceMapsRequestHeaders.keySet())
            request = request.addHeader(key,sourceMapsRequestHeaders.get(key));

        return request;
    }
}
