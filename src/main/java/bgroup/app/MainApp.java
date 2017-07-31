package bgroup.app;

import bgroup.configuration.AppConfig;
import bgroup.model.MikrotikIp;
import bgroup.model.UserFromUtm;
import bgroup.service.MikrotikIpService;
import bgroup.service.UserFromUtmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

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
    public static String hibernateDialect = "";
    public static String hibernateShowSql = "";
    public static String hibernateFormatSql = "";
    public static String hibernateHbm2ddl = "";
    public static String ipFILE = "";
    public static List<MikrotikIp> mikrotikIpList = new LinkedList<MikrotikIp>();

    public static void main(String[] args) {
        try {
            ParseArguments.parseArguments(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Не возможнго прочесть аргументы");
        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        logger.info("start");
        UserFromUtmService service = (UserFromUtmService) context.getBean("userFromUtmService");
        if (service == null) {
            logger.error("service is null");
        }
        List<UserFromUtm> usersFromUtm = service.getUserList();

        boolean flag = usersToMikrotikIp(usersFromUtm, mikrotikIpList);

        SshClient manager = new SshClient();
        String command;
        String lines = null;
        command = "ip firewall address-list print terse";
        //lines = manager.connectAndExecuteListCommand(HOSTNAME, USERNAME, PASSWORD, command);
        //logger.debug(lines);

        /*
        чекаем нитями по мироктикам
         */
        //ArrayList<MikrotikIpService> siteUserArrayList = new ArrayList<MikrotikIpService>(mikrotikIpList.size());
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        for (MikrotikIp mikrotikIp : mikrotikIpList) {
            logger.info("start: {}", mikrotikIp);
            Thread thread = new Thread(new MikrotikIpService(mikrotikIp));
            thread.start();
            threadArrayList.add(thread);
        }
        for (Thread thread : threadArrayList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean usersToMikrotikIp(List<UserFromUtm> usersFromUtm, List<MikrotikIp> mikrotikIpList) {
        HashMap<Integer, MikrotikIp> mikGroup = new HashMap<Integer, MikrotikIp>();
        for (MikrotikIp mikrotikIp : mikrotikIpList) {
            //logger.debug("put " + mikrotikIp.getIp() + "to " + mikrotikIp.getGoupId());
            mikGroup.put(mikrotikIp.getGoupId(), mikrotikIp);
        }
        for (UserFromUtm user : usersFromUtm) {
            if (user != null && user.getGroupId() != null && user.getIp() != null) {
                if (mikGroup.containsKey(user.getGroupId())) {
                    // logger.debug("mikIp: {}",mikGroup.get(user.getGroupId()));
                    mikGroup.get(user.getGroupId()).getUsers().add(user);
                } else {
                    logger.error("unknown users: {}", user);
                }
            }
        }
        return false;
    }

}
