package bgroup.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import bgroup.app.MainApp;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "bgroup.configuration" })
public class HibernateConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "bgroup.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
     }
	
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(MainApp.jdbcDriverClassName);
        dataSource.setUrl(MainApp.jdbcUrl);
        dataSource.setUsername(MainApp.jdbcUserName);
        dataSource.setPassword(MainApp.jdbcPassword);
        return dataSource;
    }
    
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", MainApp.hibernateDialect);
        properties.put("hibernate.show_sql", MainApp.hibernateShowSql);
        properties.put("hibernate.format_sql", MainApp.hibernateFormatSql);
        properties.put("hibernate.hbm2ddl.auto",MainApp.hibernateHbm2ddl);
        return properties;        
    }
    
	@Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
       HibernateTransactionManager txManager = new HibernateTransactionManager();
       txManager.setSessionFactory(s);
       return txManager;
    }

    /*@Bean
    public UserFromUtmService userFromUtmService(){
        return new UserFromUtmServiceImpl();
    }*/
}

