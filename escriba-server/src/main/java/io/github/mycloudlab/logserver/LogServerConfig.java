package io.github.mycloudlab.logserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.apache.http.client.methods.HttpUriRequest;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.MDC;

@ApplicationScoped
public class LogServerConfig {

    private List<Pattern> allowedDomains = new ArrayList<>();
    private Map<Pattern, URL> translatedDomains = new HashMap<>();
    private Map<String, String> mdcExtraLabels = new HashMap<>();
    private Map<String, String> sourceMapsRequestHeaders = new HashMap<>();

    public LogServerConfig() {

        // configure allowed domains
        Optional<List<String>> _allowedDomains = ConfigProvider.getConfig()
                .getOptionalValues("logserver.sourcemaps.allowed-domains", String.class);

        if (_allowedDomains.isPresent()) {
            _allowedDomains.get().forEach((domain) -> {
                allowedDomains.add(Pattern.compile(domain));
            });
        }

        // configure translate domains
        Optional<List<String>> _translateDomains = ConfigProvider.getConfig()
                .getOptionalValues("logserver.sourcemaps.translate-domains", String.class);

        if (_translateDomains.isPresent()) {
            _translateDomains.get().forEach((keyval) -> {
                Pattern key = Pattern.compile(keyval.split("=")[0]);
                URL value;
                try {
                    value = new URL(keyval.split("=")[1]);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("configuration problem", e);
                }
                translatedDomains.put(key, value);
            });
        }

        // configure extra labels mdc
        Optional<List<String>> _mdcExtraLabels = ConfigProvider.getConfig()
                .getOptionalValues("logserver.mdc.extra-labels", String.class);

        if (_mdcExtraLabels.isPresent()) {
            _mdcExtraLabels.get().forEach((keyval) -> {
                String key = keyval.split(":")[0];
                String value = keyval.split(":")[1];
                mdcExtraLabels.put(key, value);
            });
        }

        // configure extra request headers sourcemap
        Optional<List<String>> _sourceMapsRequestHeaders = ConfigProvider.getConfig()
                .getOptionalValues("logserver.sourcemaps.request-extra-headers", String.class);

        if (_sourceMapsRequestHeaders.isPresent()) {
            _sourceMapsRequestHeaders.get().forEach((keyval) -> {
                String key = keyval.split(":")[0];
                String value = keyval.split(":")[1];
                sourceMapsRequestHeaders.put(key, value);
            });
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

    public HttpUriRequest fillRequestExtraHeaders(HttpUriRequest request) {
        for (String key : sourceMapsRequestHeaders.keySet())
            request.addHeader(key, sourceMapsRequestHeaders.get(key));

        return request;
    }
}
