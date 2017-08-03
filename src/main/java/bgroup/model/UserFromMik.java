package bgroup.model;

/**
 * Created by VSB on 01.08.2017.
 * sshUtmUsersToMikrotik
 */
public class UserFromMik {

    private String ipF;
    private String ipQ;
    private Integer id;
    private Integer speedLimit;

    public UserFromMik(Integer id, String ipF, Integer speedLimit) {
        this.id = id;
        this.ipF = ipF;
        this.speedLimit = speedLimit;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public String getIpF() {
        return ipF;
    }

    public void setIpF(String ipF) {
        this.ipF = ipF;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpQ() {
        return ipQ;
    }

    public void setIpQ(String ipQ) {
        this.ipQ = ipQ;
    }
}
