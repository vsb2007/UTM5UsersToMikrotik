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
    String userName;
    String password;
    String listName;
    List<UserFromUtm> users;
    boolean isInterrupt = false;


    public MikrotikIp(Integer goupId, String ip,String userName, String password,String listName) {
        this.goupId = goupId;
        this.ip = ip;
        this.userName = userName;
        this.password = password;
        this.listName = listName;
        this.users = new LinkedList<UserFromUtm>();

    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }

    @Override
    public String toString() {
        int size = 0;
        if (this.getUsers() != null) size = this.getUsers().size();
        return this.getGoupId() + ":" + this.getIp() + ": size=" + size;
    }
}
