package bgroup.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFromUtm {
    static final Logger logger = LoggerFactory.getLogger(UserFromUtm.class);
    private String login;
    private Integer id;
    private Integer groupId;
    private String groupName;
    private String ip;
    private String mask;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return this.login + " " +
                this.id + " " +
                this.groupId + " " +
                this.groupName + " " +
                this.ip + " " +
                this.mask
                ;
    }
}