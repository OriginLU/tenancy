package com.zeroone.tenancy.config;

import com.zeroone.tenancy.template.NativeQueryHibernateTemplate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateConfiguration {


    @Bean
    public NativeQueryHibernateTemplate nativeQueryHibernateTemplate(@Qualifier("entityManagerFactory")EntityManagerFactory emf) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        return new NativeQueryHibernateTemplate(sessionFactory);
    }
}
