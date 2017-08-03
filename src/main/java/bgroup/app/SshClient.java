package bgroup.app;

import bgroup.model.MikrotikIp;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by VSB on 07.07.2017.
 * sshUtmUsersToMikrotik
 */
public class SshClient {
    static final Logger logger = LoggerFactory.getLogger(SshClient.class);
    private static final int SSH_PORT = 22;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024;

    public String getResponseFromHost(String host, String username,
                                      String password, String command) {
        String response = null;
        try {
            Session session = null;
            session = initSession(host, username, password);
            Channel channel = initChannel(command, session);
            InputStream in = channel.getInputStream();
            channel.connect();
            response = getDataFromChannel(channel, in);
            in.close();
            channel.getExitStatus();
            channel.disconnect();
            //closeConnection();
            session.disconnect();
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
        return response;
    }

    private Session initSession(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, SSH_PORT);
        session.setPassword(password);
        UserInfo userInfo = new MyUserInfo();
        session.setUserInfo(userInfo);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(CONNECTION_TIMEOUT);
        return session;
    }

    public String getResponseFromHost(MikrotikIp mikrotikIp, String command) {
        return getResponseFromHost(mikrotikIp.getIp(), mikrotikIp.getUserName(), mikrotikIp.getPassword()
                , command);
    }

    public class MyUserInfo implements UserInfo {
        private String password;

        public void showMessage(String message) {
            System.out.println(message);
        }

        public boolean promptYesNo(String message) {
            System.out.println(message);
            return true;
        }

        //@Override
        public String getPassphrase() {
            return null;
        }

        //@Override
        public String getPassword() {
            return this.password;
        }

        //@Override
        public boolean promptPassphrase(String arg0) {
            System.out.println(arg0);
            return true;
        }

        //@Override
        public boolean promptPassword(String arg0) {
            System.out.println(arg0);
            this.password = arg0;
            return true;
        }
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        Channel channel = session.openChannel("exec");
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(commands);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        return channel;
    }

    private String getDataFromChannel(Channel channel, InputStream in)
            throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[BUFFER_SIZE];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                if (exitStatus != 0)
                    logger.info("exit-status: " + exitStatus);
                break;
            }
            trySleep(1000);
        }
        return result.toString();
    }

    private void trySleep(int sleepTimeInMilliseconds) {
        try {
            Thread.sleep(sleepTimeInMilliseconds);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
