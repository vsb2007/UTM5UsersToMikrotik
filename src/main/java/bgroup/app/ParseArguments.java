package bgroup.app;

import bgroup.model.MikrotikIp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

public class ParseArguments {
    static final Logger logger = LoggerFactory.getLogger(ParseArguments.class);

    public static void parseArguments(String arg) throws Exception {
        logger.info("Start parsing: {}", arg);
        BufferedReader input = new BufferedReader(new FileReader(arg));
        String line;
        while ((line = input.readLine()) != null) {
            String param = "";
            String value = "";
            line = line.trim();
            char[] chars = line.toCharArray();
            boolean flag = false;
            if (chars != null && chars.length > 0 && (chars[0] == '#' || chars[0] == ';')) continue;
            try {
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '=') {
                        flag = true;
                        continue;
                    }
                    if (chars[i] != ' ' && !flag) {
                        param += chars[i];
                        continue;
                    }
                    if (flag) {
                        if (chars[i] != ' ' && chars[i] != '"' && chars[i] != ';') {
                            value += chars[i];
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
            if (param.equals("mik_host")) {
                MainApp.HOSTNAME = value;
            } else if (param.equals("mik_user")) {
                MainApp.USERNAME = value;
            } else if (param.equals("mik_password")) {
                MainApp.PASSWORD = value;
            } else if (param.equals("db_url")) {
                MainApp.dbHOSTNAME = value;
            } else if (param.equals("db_name")) {
                MainApp.dbNAME = value;

            } else if (param.equals("jdbc.username")) {
                MainApp.jdbcUserName = value;
            } else if (param.equals("jdbc.password")) {
                MainApp.jdbcPassword = value;
            } else if (param.equals("jdbc.driverClassName")) {
                MainApp.jdbcDriverClassName = value;
            } else if (param.equals("jdbc.url")) {
                MainApp.jdbcUrl = value;
            } else if (param.equals("hibernate.dialect")) {
                MainApp.hibernateDialect = value;
            } else if (param.equals("hibernate.show_sql")) {
                MainApp.hibernateShowSql = value;
            } else if (param.equals("hibernate.format_sql")) {
                MainApp.hibernateFormatSql = value;
            } else if (param.equals("hibernate.hbm2ddl")) {
                MainApp.hibernateHbm2ddl = value;
            } else if (param.equals("mikGroup")) {
                String[] groupIp = getGroupIp(value);
                if (groupIp != null) {
                    Integer group = null;
                    try {
                        group = Integer.parseInt(groupIp[0]);
                    } catch (Exception e) {
                        logger.error("Ошибка: {}", groupIp[0] + "\n" + e.toString());
                        continue;
                    }
                    MainApp.mikrotikIpList.add(new MikrotikIp(group, groupIp[1],groupIp[2],groupIp[3],groupIp[4]));
                }
            }
            logger.info("Param: {}:{}", param, value);
        }
    }

    private static String[] getGroupIp(String value) {
        String[] groupIp = value.split(":");
        if (groupIp.length != 5) return null;
        else return groupIp;
    }
}
