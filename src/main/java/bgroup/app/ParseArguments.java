package bgroup.app;

import java.io.BufferedReader;
import java.io.FileReader;

public class ParseArguments {
    public static void parseArguments(String arg) throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(arg));
        String line;
        while ((line = input.readLine()) != null) {
            String param = "";
            String value = "";
            char[] chars = line.toCharArray();
            boolean flag = false;
            if (chars[0] == '#' || chars[0] == ';') continue;
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
            if (param.equals("mik_host")) {
                MainApp.HOSTNAME = value;
            } else if (param.equals("mik_user")) {
                MainApp.USERNAME = value;
            } else if (param.equals("mik_password")) {
                MainApp.PASSWORD = value;
            } else if (param.equals("mik_list")) {
                MainApp.dropLIST = value;
            } else if (param.equals("db_url")) {
                MainApp.dbHOSTNAME = value;
            } else if (param.equals("db_name")) {
                MainApp.dbNAME = value;

            } else if (param.equals("jdbc.username")) {
                MainApp.jdbcUserName = value;
            } else if (param.equals("jdbc.password")) {
                MainApp.jdbcPassword = value;
            }

            else if (param.equals("jdbc.driverClassName")) {
                MainApp.jdbcDriverClassName = value;
            } else if (param.equals("jdbc.url")) {
                MainApp.jdbcUrl = value;
            }
        }
    }
}
