package bgroup.service;

import bgroup.dao.UserFromUtmDaoImpl;
import bgroup.model.UserFromUtm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by VSB on 06.07.2017.
 * sshUtmUsersToMikrotik
 */
@Service("userFromUtmService")
@Transactional
public class UserFromUtmServiceImpl implements UserFromUtmService {
    static final Logger logger = LoggerFactory.getLogger(UserFromUtmServiceImpl.class);

    @Autowired
    private UserFromUtmDaoImpl userFromUtmDao;

    //@Override
    public List<UserFromUtm> getUserList() {
        if (userFromUtmDao == null) {
            logger.error("error2");
            //userFromUtmDao = new UserFromUtmDaoImpl();
        }
        List<UserFromUtm> usersFromUtm = userFromUtmDao.getUserFromUtmList();
        return usersFromUtm;
    }
}
