package com.sample.reqmanagement.amqp;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("publisher")
public class RequestManagementPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RequestManagementPublisher.class);

    @Autowired
    @Qualifier("amqpTemplate")
    private AmqpTemplate amqpTemplate;

    @Value("${spring.rabbitmq.queue.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.queue.routing.key}")
    private String routingKey;

    public Boolean publishMessageForProcessing(Message message){
        String methodPrefix = "publishMessageForProcessing(Message message, String routingKey): ";
        logger.info(methodPrefix+"Start");
        Boolean returnVal = Boolean.TRUE;
        try{
            logger.debug(methodPrefix+"Publishing to exchange");
            logger.info("Exchange "+exchange);
            logger.info("routingKey "+routingKey);
            logger.info("message "+message.toString());
            amqpTemplate.send(exchange, routingKey, message);
        }
        catch(Exception ex){
            logger.error("Exception occurred while publishing message.", ex);
            returnVal = Boolean.FALSE;
        }
        logger.info(methodPrefix+"End");
        return returnVal;
    }
}
