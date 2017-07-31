package bgroup.service;

import bgroup.app.MainApp;
import bgroup.model.MikrotikIp;
import bgroup.model.UserFromUtm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by VSB on 31.07.2017.
 * sshUtmUsersToMikrotik
 */
public class MikrotikIpService implements Runnable {
    static final Logger logger = LoggerFactory.getLogger(MikrotikIpService.class);

    MikrotikIp mikrotikIp;


    public MikrotikIpService(MikrotikIp mikrotikIp) {
        this.mikrotikIp = mikrotikIp;

    }

    //@Override
    public void run() {
        checkListAllowTraffic();
    }

    void checkListAllowTraffic() {
        for (UserFromUtm user : mikrotikIp.getUsers()) {
            logger.info("user3: {}", user.toString());
        }
    }

}
