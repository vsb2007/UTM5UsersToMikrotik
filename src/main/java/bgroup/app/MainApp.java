package bgroup.app;

import bgroup.configuration.AppConfig;
import bgroup.model.UserFromUtm;
import bgroup.service.UserFromUtmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.List;

public class MainApp {
    static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    public static String HOSTNAME = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String dropLIST = "";
    public static String dbHOSTNAME = "";
    public static String dbNAME = "";

    public static String jdbcDriverClassName = "";
    public static String jdbcUrl = "";
    public static String jdbcUserName = "";
    public static String jdbcPassword = "";
    public static String ipFILE = "";

    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        logger.info("start");
        UserFromUtmService service = (UserFromUtmService) context.getBean("userFromUtmService");
        if (service == null) {
            logger.error("service is null");
        }
        List<UserFromUtm> usersFromUtm = service.getUserList();

        if (usersFromUtm != null) {
            for (UserFromUtm userFromUtm : usersFromUtm) {
                System.out.println(userFromUtm.getLogin() + " " + userFromUtm.getIp());
            }
        }
        SshClient manager = new SshClient();
        String command;
        String lines = null;
        command = "ip firewall address-list print terse";
        //lines = manager.connectAndExecuteListCommand(HOSTNAME, USERNAME, PASSWORD, command);
        logger.debug(lines);
    }
}
