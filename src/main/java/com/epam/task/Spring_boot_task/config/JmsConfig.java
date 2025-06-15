package com.epam.task.Spring_boot_task.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        ActiveMQConnectionFactory targetConnectionFactory = new ActiveMQConnectionFactory();
        targetConnectionFactory.setBrokerURL("tcp://localhost:61616");
        targetConnectionFactory.setUserName("admin");
        targetConnectionFactory.setPassword("admin");

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(targetConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(10);
        return cachingConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory cachingConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.setReceiveTimeout(5000); // timeout in ms for waiting reply
        jmsTemplate.setPubSubDomain(false); // we're using queues, not topics
        return jmsTemplate;
    }
}
