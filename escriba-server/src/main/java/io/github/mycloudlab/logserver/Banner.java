package io.github.mycloudlab.logserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Banner {

    private static final Logger LOGGER = Logger.getLogger("escriba-server");

    void onStart(@Observes StartupEvent ev) throws Exception {

        try (
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("default_banner.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); //
        ) {
            String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            LOGGER.info(contents);
        }

    }
}
