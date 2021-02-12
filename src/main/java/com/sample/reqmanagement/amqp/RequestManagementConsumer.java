package com.sample.reqmanagement.amqp;

import com.rabbitmq.client.Channel;
import com.sample.reqmanagement.dto.RequestDetailsDto;
import com.sample.reqmanagement.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestManagementConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(RequestManagementConsumer.class);

    @Autowired
    CustomerService customerService;


    @RabbitListener(queues="${spring.rabbitmq.queue.name}", containerFactory="containerFactory")
    public void receiveMessage(@Payload RequestDetailsDto requestDetailsDto, Channel channel, @Header(value= AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String methodPrefix = "#receiveMessage(): ";
        try{
            LOG.info("The message Consumed from Queue is : "+ requestDetailsDto.toString());
            //customerService.consumingFromQueue(requestDetailsDto);
        }
        catch(Exception ex){
            LOG.error(methodPrefix + "Unexpected exception occurred.", ex);
        }
        finally{
            // Acknowledge the message. The message will be removed from queue.
            LOG.info(methodPrefix + "Acknowledging the message to remove it from the queue");
            channel.basicAck(tag, false);
        }
    }
}
