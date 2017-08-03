package bgroup.service;

import bgroup.model.UserFromMik;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


/**
 * Created by VSB on 01.08.2017.
 * sshUtmUsersToMikrotik
 */
public class UsersFromMikService {
    static final Logger logger = LoggerFactory.getLogger(UsersFromMikService.class);
    private HashMap<Integer, UserFromMik> usersFromMik;

    public UsersFromMikService(String linesFirewallList, String stringQ, String listName) {
        logger.info("start UsersFromMikService\n");
        String[] stringsF = linesFirewallList.split("\n");
        for (String str : stringsF) {
            UserFromMik userFromMik = getUserFromMikFromFLString(str);
            if (userFromMik == null) {
                continue;
            } else {
            }
            if (this.usersFromMik == null) {
                this.usersFromMik = new HashMap<Integer, UserFromMik>();
            }
            this.usersFromMik.put(userFromMik.getId(), userFromMik);
        }
        setSpeedLimitFromStringQ(stringQ);
    }

    private void setSpeedLimitFromStringQ(String stringQ) {
        logger.info("start setSpeedLimitFromStringQ");
        String[] stringsQ = stringQ.split("\n");
        for (String str : stringsQ) {
            str = str.trim();
            if (str.length() < 5) {
                continue;
            }
            str = str.replaceAll("(\\s){2,}", " ");
            String[] words = str.split(" ");
            if (words.length < 10) {
                continue;
            }
            String[] idStr = words[1].split("=");
            if (idStr.length != 2 || !idStr[0].equals("comment")) {
                continue;
            }
            Integer id = null;
            String ip = null;
            try {
                id = Integer.parseInt(idStr[1]);
            } catch (Exception e) {
                continue;
            }
            if (id == null) {
                continue;
            }
            String[] ipStr = words[3].split("=");
            if (ipStr.length != 2 || (!ipStr[0].equals("target") && !ipStr[0].equals("target-addresses"))) {
                continue;
            }
            ip = ipStr[1].split("/")[0];
            String[] speedsStr = words[9].split("=");
            if (speedsStr.length != 2 || !speedsStr[0].equals("max-limit")) {
                speedsStr = words[11].split("=");
                if (speedsStr.length != 2 || !speedsStr[0].equals("max-limit"))
                    continue;
            }
            String speedStr = speedsStr[1].split("k")[0];
            Integer speed = null;
            try {
                speed = Integer.parseInt(speedStr);
            } catch (Exception e) {
                continue;
            }
            //logger.debug("'" + id + "'" + ip + "'" + speed);
            if (usersFromMik != null && usersFromMik.containsKey(id) && speed != null && ip != null) {
                usersFromMik.get(id).setSpeedLimit(speed);
                usersFromMik.get(id).setIpQ(ip);
            }
        }
    }

    private UserFromMik getUserFromMikFromFLString(String str) {
        str = str.trim();
        if (str.length() < 5) return null;
        str = str.replaceAll("(\\s){2,}", " ");
        String[] words = str.split(" ");
        String[] idStr = words[1].split("=");
        if (idStr.length != 2 || !idStr[0].equals("comment")) {
            return null;
        }
        Integer id = null;
        String ip = null;
        try {
            id = Integer.parseInt(idStr[1]);
        } catch (Exception e) {
            return null;
        }
        if (id == null) return null;
        String[] ipStr = words[3].split("=");
        if (ipStr.length != 2 || !ipStr[0].equals("address")) {
            return null;
        }
        ip = ipStr[1].split("/")[0];
        //logger.debug("{} - {}", id, ip);
        return new UserFromMik(id, ip, null);
    }

    public HashMap<Integer, UserFromMik> getUsersFromMik() {
        return usersFromMik;
    }

    public void setUsersFromMik(HashMap<Integer, UserFromMik> usersFromMik) {
        this.usersFromMik = usersFromMik;
    }
}
