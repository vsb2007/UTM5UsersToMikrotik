package bgroup.dao;

import bgroup.model.UserFromUtm;

import org.hibernate.Query;
import org.hibernate.Session;

import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VSB on 06.07.2017.
 * sshUtmUsersToMikrotik
 */
@Repository("userFromUtmDao")
public class UserFromUtmDaoImpl extends AbstractDao<Integer, UserFromUtm> implements UserFromUtmDao {
    static final Logger logger = LoggerFactory.getLogger(UserFromUtmDaoImpl.class);

    public List<UserFromUtm> getUserFromUtmList() {
        Session session = getSession();

        Query query = session.createSQLQuery(
                "SELECT \n" +
                        "users.login,\n" +
                        "accounts.id,\n" +
                        " users_groups_link.group_id as groupId,\n" +
                        " \n" +
                        " groups.group_name as groupName,\n" +
                        " \n" +
                        " CAST(inet_ntoa(ip_groups.ip & 0xFFFFFFFF) AS CHAR(10000) CHARACTER SET utf8) as ip,\n" +
                        "CAST(inet_ntoa(ip_groups.mask & 0xFFFFFFFF) AS CHAR(10000) CHARACTER SET utf8) as mask\n" +
                        ", dynashape_data.curr_limit as speedLimit\n" +
                        "FROM users \n" +
                        "left join accounts on users.basic_account = accounts.id AND accounts.is_deleted = 0 and accounts.block_id = 0\n" +
                        "left join users_groups_link on users.basic_account = users_groups_link.user_id\n" +
                        "left join groups on groups.id = users_groups_link.group_id\n" +
                        "left join service_links on  service_links.account_id = accounts.id and service_links.is_deleted = 0\n" +
                        "left join dynashape_data on dynashape_data.slink_id = service_links.id and dynashape_data.direction=1\n" +
                        "left join iptraffic_service_links on iptraffic_service_links.id = service_links.id and iptraffic_service_links.is_deleted = 0\n" +
                        "left join ip_groups on iptraffic_service_links.ip_group_id = ip_groups.ip_group_id AND ip_groups.is_deleted = 0\n" +
                        "\n" +
                        "where users.is_deleted = 0 and accounts.block_id = 0\n" +
                        "\n")
                .setResultTransformer(Transformers.aliasToBean(UserFromUtm.class));

        List<UserFromUtm> result = null;
        try {
            result = query.list();
        } catch (Exception e) {
            logger.error(e.toString());
        }
        if (result != null && result.size() > 1) {
            logger.info("что-то нашли");
            return result;
        } else {
            logger.info("ничего не найдено");
        }
        return null;
    }
}
