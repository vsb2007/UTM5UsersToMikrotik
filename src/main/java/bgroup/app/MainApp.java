package bgroup.app;

import bgroup.configuration.AppConfig;
import bgroup.configuration.HibernateConfiguration;
import bgroup.dao.UserFromUtmDaoImpl;
import bgroup.model.UserFromUtm;
import bgroup.service.UserFromUtmService;
import bgroup.service.UserFromUtmServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.List;

/**
 * Created by VSB on 06.07.2017.
 * sshUtmUsersToMikrotik
 */
public class MainApp {
    static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        //ApplicationContext context = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        logger.info("start");
        UserFromUtmService service = (UserFromUtmService) context.getBean("userFromUtmService");
        if (service==null){
            logger.error("service is null");
        }
        List<UserFromUtm> usersFromUtm = service.getUserList();
        if (usersFromUtm != null) {
            for (UserFromUtm userFromUtm : usersFromUtm) {
                System.out.println(userFromUtm.getLogin() + " " + userFromUtm.getIp());
            }
        }
    }
}
