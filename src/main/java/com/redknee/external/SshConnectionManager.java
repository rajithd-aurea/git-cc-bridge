package com.redknee.external;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.redknee.util.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SshConnectionManager {

    private final String username;
    private final String password;
    private final String hostname;

    private Session session;
    private ChannelShell channel;

    public SshConnectionManager(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
    }

    private Session getSession() {
        if (session == null || !session.isConnected()) {
            session = connect(hostname, username, password);
        }
        return session;
    }

    private Channel getShellChannel() {
        if (channel == null || !channel.isConnected()) {
            try {
                channel = (ChannelShell) getSession().openChannel("shell");
                channel.connect();

            } catch (Exception e) {
                log.error("Error while opening channel: ", e);
            }
        }
        return channel;
    }

    private Session connect(String hostname, String username, String password) {
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(username, hostname, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);

            log.info("Connecting SSH to {} - Please wait for few seconds... ", hostname);
            session.connect();
            log.info("Connected!");
        } catch (Exception e) {
            log.error("An error occurred while connecting to " + hostname + ": " + e);
        }

        return session;

    }

    public int executeCommands(List<String> commands) {
        try {
            Channel channel = getShellChannel();
            sendCommands(channel, commands);
            int exitStatus = readChannelOutput(channel);
            log.info("Finished sending commands. Exit code is {}", exitStatus);
            return exitStatus;
        } catch (Exception e) {
            log.error("An error ocurred during executeCommands: " + e);
        }
        return Constants.ERROR_EXIT_CODE;
    }

    public void copyFile(String localFile, String remoteFile) {
        FileInputStream fis = null;
        try {
            Session session = getSession();
            boolean ptimestamp = true;

            // exec 'scp -t rfile' remotely
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + remoteFile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            File _lfile = new File(localFile);

            if (ptimestamp) {
                command = "T" + (_lfile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    System.exit(0);
                }
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = _lfile.length();
            command = "C0644 " + filesize + " ";
            if (localFile.lastIndexOf('/') > 0) {
                command += localFile.substring(localFile.lastIndexOf('/') + 1);
            } else {
                command += localFile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send a content of lfile
            fis = new FileInputStream(localFile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
            out.close();

            channel.disconnect();
            session.disconnect();


        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ee) {
            }
        }


    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    private void sendCommands(Channel channel, List<String> commands) {
        try {
            PrintStream out = new PrintStream(channel.getOutputStream());

            out.println("#!/bin/bash");
            commands.forEach(out::println);
            out.println("exit");

            out.flush();
        } catch (Exception e) {
            log.error("Error while sending commands: ", e);
        }

    }

    private int readChannelOutput(Channel channel) {
        byte[] buffer = new byte[1024];

        try {
            InputStream in = channel.getInputStream();
            String line = "";
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    log.info(line);
                }

                if (line.contains("logout")) {
                    break;
                }

                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        } catch (Exception e) {
            log.error("Error while reading channel output: ", e);
        }
        return channel.getExitStatus();
    }

    public void close() {
        channel.disconnect();
        session.disconnect();
        log.info("Disconnected channel and session");
    }
}
