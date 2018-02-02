package com.asela.camel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRoutes extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRoutes.class);
    
    @Override
    public void configure() throws Exception {

        from("timer:appTimer1")
            .stop()
            .to("log:Hello");
        
        from("timer:appTimer2?delay=10")
            .stop()
            .log("Body ${body}")
            .process(exchange -> {
                LOG.info("Exchange ID : {} | Message Exchange Pattern: {}", exchange.getExchangeId(), exchange.getPattern()); 
                exchange.getProperties().forEach( (key,value) -> LOG.info("Properties : {} - {}", key, value));
                
                exchange.getIn().getHeaders().forEach( (key,value) -> LOG.info("In Headers : {} - {}", key, value));
                exchange.getIn().getAttachmentObjects().forEach( (key,value) -> LOG.info("In Attachments : {} - {}", key, value));
                LOG.info("In Body: {}", exchange.getIn().getBody());
                
                exchange.getOut().getHeaders().forEach( (key,value) -> LOG.info("Out Headers : {} - {}", key, value));
                exchange.getOut().getAttachmentObjects().forEach( (key,value) -> LOG.info("Out Attachments : {} - {}", key, value));
                LOG.info("Out Body: {}", exchange.getOut().getBody());

            });
        
        from("timer:appTimer3?delay=1s&repeatCount=1")
            .stop()
            .process(this::printExchange)
            .process(exchange -> {
                printExchange(exchange);
                exchange.getOut().setBody(new BigDecimal("1").divide(new BigDecimal("3"), 100, RoundingMode.HALF_UP));
            })
            .process(this::printExchange);
            
        from("timer:appTimer4?delay=1s&repeatCount=1")
            .stop()
            .process(this::printExchange)
            .to("direct:sayHello");
        
        from("direct:sayHello")
            .log("Body : ${body}")
            .process(exchange -> {
                printExchange(exchange);
                String message = exchange.getIn().getBody(String.class);
                exchange.getOut().setBody(message + " is " + Boolean.valueOf(Long.valueOf(message.split(" ")[1]) % 2 == 0));
            });
        
        from("timer:getusers?delay=1s&repeatCount=1")
            .to("direct:getUsers");
        

        from("direct:getUsers")
            .setHeader(Exchange.CONTENT_TYPE, constant("GET"))
            .to("http4://jsonplaceholder.typicode.com/users")
            .log("body : ${body}");
            


    }

    private void printExchange(Exchange exchange) {
        LOG.info("Exchange ID : {} | Message Exchange Pattern: {}", exchange.getExchangeId(), exchange.getPattern()); 
        exchange.getProperties().forEach( (key,value) -> LOG.info("Properties : {} - {}", key, value));
        
        printMessage(exchange.getIn(), "IN");
        printMessage(exchange.getOut(), "OUT");
    }

    private void printMessage(Message inMessage, String tag) {
        inMessage.getHeaders().forEach( (key,value) -> LOG.info("{} Headers : {} - {}",tag, key, value));
        inMessage.getAttachmentObjects().forEach( (key,value) -> LOG.info("{} Attachments : {} - {}",tag,  key, value));
        LOG.info("{} Body: {}", tag, inMessage.getBody());
    }

}
