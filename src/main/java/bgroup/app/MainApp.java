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
        /*
        String str = "*89503367777 отец";
        str = str.trim().replaceAll(" |\\-|\\.", "");
        str = str.replaceAll("[^0-9]","");

        System.out.println(str);

        System.exit(-1);
        */
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
        //lines = manager.getResponseFromHost(HOSTNAME, USERNAME, PASSWORD, command);
        //logger.debug(lines);

        /*
        чекаем нитями по мироктикам
         */
        //ArrayList<MikrotikIpService> siteUserArrayList = new ArrayList<MikrotikIpService>(mikrotikIpList.size());
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        List<MikrotikIpService> mikrotikIpServiceList = new ArrayList<MikrotikIpService>();
        for (MikrotikIp mikrotikIp : mikrotikIpList) {
            logger.info("start: {}", mikrotikIp);
            MikrotikIpService mikrotikIpService = new MikrotikIpService(mikrotikIp);
            mikrotikIpServiceList.add(mikrotikIpService);
            //Thread thread = new Thread(mikrotikIpService);
            mikrotikIpService.start();
            //thread.start();
            //threadArrayList.add(thread);
        }
        /*
        for (Thread thread : threadArrayList) {
            try {
                thread.join();
                thread.
                //thread.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        while (mikrotikIpServiceList.size() > 0) {
            for (int i = 0; i < mikrotikIpServiceList.size(); i++) {
                MikrotikIpService mikrotikIpService = mikrotikIpServiceList.get(i);
                if (mikrotikIpService != null && mikrotikIpService.isAlive()) {
                    logger.info("mik:" + mikrotikIpService.getMikrotikIp().getIp() + " " + (System.currentTimeMillis() - mikrotikIpService.getStartTime()) + " " + mikrotikIpService.isAlive());
                    if (mikrotikIpService.isAlive()) {
                        long jTime = System.currentTimeMillis();
                        if (mikrotikIpService.isInterrupted() == false
                                && (jTime - mikrotikIpService.getStartTime()) > 3 * 60 * 1000) {
                            mikrotikIpService.getMikrotikIp().setInterrupt(true);
                            mikrotikIpService = null;
                            logger.info("set3 " + mikrotikIpService.getMikrotikIp().getIp() + " to null: {}", jTime);
                        }
                    } else {
                        mikrotikIpServiceList.remove(i);
                        i--;
                        logger.info("set1 " + mikrotikIpService.getMikrotikIp().getIp() + " to null");
                    }
                } else {
                    mikrotikIpServiceList.remove(i);
                    i--;
                    logger.info("set2 " + mikrotikIpService.getMikrotikIp().getIp() + " to null");
                }
            }
            try {
                logger.info("sleeeeep.....");
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                logger.error(e.toString());
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
                    //logger.error("unknown users: {}", user);
                    continue;
                }
            }
        }
        return false;
    }

}
