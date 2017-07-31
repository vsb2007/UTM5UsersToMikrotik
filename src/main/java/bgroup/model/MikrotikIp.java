package bgroup.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by VSB on 31.07.2017.
 * sshUtmUsersToMikrotik
 */
public class MikrotikIp {
    Integer goupId;
    String ip;
    List<UserFromUtm> users;

    public MikrotikIp(Integer goupId, String ip) {
        this.goupId = goupId;
        this.ip = ip;
        users = new LinkedList<UserFromUtm>();
    }

    public List<UserFromUtm> getUsers() {
        return users;
    }

    public void setUsers(List<UserFromUtm> users) {
        this.users = users;
    }

    public Integer getGoupId() {
        return goupId;
    }

    public void setGoupId(Integer goupId) {
        this.goupId = goupId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        int size = 0;
        if (this.getUsers() != null) size = this.getUsers().size();
        return this.getGoupId() + ":" + this.getIp() + ": size=" + size;
    }
}
