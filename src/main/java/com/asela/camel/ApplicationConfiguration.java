package com.asela.camel;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    CamelContext camelContext;

    @Autowired
    ApplicationRoutes applicationRoutes;

    @PostConstruct
    public void init() {
        try {
            camelContext.addRoutes(applicationRoutes);
            camelContext.getRoutes().stream().map(Object::toString).forEach(LOG::info);
            camelContext.start();
            
            ProducerTemplate pt = camelContext.createProducerTemplate();
            String response = pt.requestBody("direct:getUsers", "hello " + ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L), String.class);
            LOG.info("response: {}", response);
            
        } catch (Exception e) {
            LOG.error("Error in Camel Setup " , e);
        }
    }

}
