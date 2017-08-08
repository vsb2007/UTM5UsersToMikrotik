package bgroup.service;

import bgroup.app.SshClient;
import bgroup.model.MikrotikIp;
import bgroup.model.UserFromUtm;
import bgroup.model.UserFromMik;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by VSB on 31.07.2017.
 * sshUtmUsersToMikrotik
 */
public class MikrotikIpService extends Thread {
    static final Logger logger = LoggerFactory.getLogger(MikrotikIpService.class);

    private MikrotikIp mikrotikIp;
    private long startTime;

    public MikrotikIpService(MikrotikIp mikrotikIp) {
        this.mikrotikIp = mikrotikIp;
        this.startTime = System.currentTimeMillis();
    }

    public MikrotikIp getMikrotikIp() {
        return mikrotikIp;
    }

    public void setMikrotikIp(MikrotikIp mikrotikIp) {
        this.mikrotikIp = mikrotikIp;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void run() {
        checkListAllowTraffic();
    }

    void checkListAllowTraffic() {
        String commandsForMik = "";
        if (!mikrotikIp.isInterrupt()) {
            String commandOne = " /ip firewall address-list print terse  where  list~\"" + mikrotikIp.getListName() + "\"";
            String linesFirewallList = getLinesResponseFromMikrotik(mikrotikIp, commandOne);
            commandOne = "queue simple print terse";
            String linesQ = getLinesResponseFromMikrotik(mikrotikIp, commandOne);
            //logger.debug(linesQ);
            UsersFromMikService usersFromMik = new UsersFromMikService(linesFirewallList, linesQ, mikrotikIp.getListName());
            commandsForMik = getCommandForMik(usersFromMik);
            //logger.debug("\n" + commandsForMik);
        }
        String[] commands = commandsForMik.split("\n");
        int count = 0;
        String command = "";
        for (int i = 0; i < commands.length; i++) {
            if (mikrotikIp.isInterrupt()) {
                break;
            }
            command += commands[i] + "\n";
            count++;
            if (count % 100 == 0) {
                getLinesResponseFromMikrotik(mikrotikIp, command);
                logger.debug("100:");
                logger.debug(command);
                command = "";
                continue;
            }

        }
        logger.debug("last:");
        logger.debug(command);
        getLinesResponseFromMikrotik(mikrotikIp, command);
/*
        for (UserFromUtm user : mikrotikIp.getUsers()) {
            if (!mikrotikIp.isInterrupt())
                logger.info("user: {}", user.toString());
            else break;
        }
*/
    }

    private String getCommandForMik(UsersFromMikService usersFromMikService) {
        if (mikrotikIp == null || mikrotikIp.getUsers() == null || mikrotikIp.getUsers().size() < 1
                || mikrotikIp.getListName() == null) {
            return null;
        }
        List<UserFromUtm> usersU = mikrotikIp.getUsers();
        if (usersFromMikService == null || usersU == null) return null;
        HashMap<Integer, UserFromMik> usersM = usersFromMikService.getUsersFromMik();
        if (usersM == null) return null;
        StringBuilder command = new StringBuilder();
        //logger.debug("UsersU: {}", usersU.size());
        for (UserFromUtm userU : usersU) {
            //logger.debug("UserU:{}", userU);
            try {
                if (usersM != null && usersM.containsKey(userU.getId()) && usersM.get(userU.getId()).getSpeedLimit() != null
                        && userU.getSpeedLimit() != null) {
                    if (usersM.get(userU.getId()).getIpF() != null && usersM.get(userU.getId()).getIpQ() != null) {
                    /*
                    проверяем Ip
                     */
                        if (!usersM.get(userU.getId()).getIpF().equals(userU.getIp()) || !usersM.get(userU.getId()).getIpQ().equals(userU.getIp())) {

                            command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n");
                            command.append("/queue simple add name=" + userU.getId() + " target=" + userU.getIp() + "/" +
                                    userU.getMask() + " max-limit=" + userU.getSpeedLimit() + "K/" + userU.getSpeedLimit() + "K" +
                                    " comment=" + userU.getId() + "\n");
                            command.append("/ip firewall address-list remove numbers=[find comment=" + userU.getId() + "]\n");
                            command.append("/ip firewall address-list add address=" + userU.getIp() + "/" + userU.getMask() +
                                    " list=" + mikrotikIp.getListName() + " comment=" + userU.getId() + "\n");
                        }
                    /*
                    проверяем скорости
                     */
                        if (usersM.get(userU.getId()).getSpeedLimit().intValue() != userU.getSpeedLimit().intValue()) {
                            command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n");
                            command.append("/queue simple add name=" + userU.getId() + " target=" + userU.getIp() + "/" +
                                    userU.getMask() + " max-limit=" + userU.getSpeedLimit() + "K/" + userU.getSpeedLimit() + "K" +
                                    " comment=" + userU.getId() + "\n");
                        }
                    }
                    usersM.remove(userU.getId());
                    //continue;
                }
            /*
            есть совпадения, но скорость снимается
             */
                else if (usersM.containsKey(userU.getId()) && usersM.get(userU.getId()).getSpeedLimit() != null
                        && userU.getSpeedLimit() == null) {
                    command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n");
                    usersM.remove(userU.getId());
                }
            /*
            есть совпадения, но скорость не уставнолена
             */
                else if (usersM.containsKey(userU.getId()) && usersM.get(userU.getId()).getSpeedLimit() == null
                        && userU.getSpeedLimit() != null) {
                    command.append("/queue simple add name=" + userU.getId() + " target=" + userU.getIp() + "/" +
                            userU.getMask() + " max-limit=" + userU.getSpeedLimit() + "k/" + userU.getSpeedLimit() + "k" +
                            " comment=" + userU.getId() + "\n");
                    usersM.remove(userU.getId());
                }
            /*
            есть совпадения - проверяем совпадение ip
             */
                else if (usersM.containsKey(userU.getId()) && usersM.get(userU.getId()).getSpeedLimit() == null
                        && userU.getSpeedLimit() == null) {
                    //command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n");
                    if (usersM.get(userU.getId()).getIpF() != null && usersM.get(userU.getId()).getIpQ() != null) {
                    /*
                    проверяем Ip
                     */
                        if (!usersM.get(userU.getId()).getIpF().equals(userU.getIp()) || !usersM.get(userU.getId()).getIpQ().equals(userU.getIp())) {

                            command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n");
                            command.append("/queue simple add name=" + userU.getId() + " target=" + userU.getIp() + "/" +
                                    userU.getMask() + " max-limit=" + userU.getSpeedLimit() + "K/" + userU.getSpeedLimit() + "K" +
                                    " comment=" + userU.getId() + "\n");
                            command.append("/ip firewall address-list remove numbers=[find comment=" + userU.getId() + "]\n");
                            command.append("/ip firewall address-list add address=" + userU.getIp() + "/" + userU.getMask() +
                                    " list=" + mikrotikIp.getListName() + " comment=" + userU.getId() + "\n");
                        }
                    }
                    usersM.remove(userU.getId());
                } else if (!usersM.containsKey(userU.getId())) {
                    command.append("/queue simple remove numbers=[find comment=" + userU.getId() + "]\n" +
                            "/queue simple add name=" + userU.getId() + " target=" + userU.getIp() + "/" +
                            userU.getMask() + " max-limit=" + userU.getSpeedLimit() + "K/" + userU.getSpeedLimit() + "K" +
                            " comment=" + userU.getId() + "\n");
                    command.append("/ip firewall address-list add address=" + userU.getIp() + "/" + userU.getMask() +
                            " list=" + mikrotikIp.getListName() + " comment=" + userU.getId() + "\n");
                    usersM.remove(userU.getId());
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        for (Map.Entry<Integer, UserFromMik> pair : usersM.entrySet()) {
            command.append("/queue simple remove numbers=[find comment=" + pair.getValue().getId() + "]\n");
            command.append("/ip firewall address-list remove numbers=[find comment=" + pair.getValue().getId() + "]\n");
        }
        return command.toString();
    }

    private String getLinesResponseFromMikrotik(MikrotikIp mikrotikIp, String command) {
        SshClient manager = new SshClient();
        String lines = manager.getResponseFromHost(mikrotikIp, command);
        return lines;
    }

}
