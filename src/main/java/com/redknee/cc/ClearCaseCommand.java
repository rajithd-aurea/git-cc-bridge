package com.redknee.cc;

import com.ibm.rational.clearcase.remote_core.cmds.Checkout;
import com.ibm.rational.clearcase.remote_core.cmds.Checkout.NonLatestTreatment;
import com.ibm.rational.clearcase.remote_core.copyarea.CopyArea;
import com.ibm.rational.clearcase.remote_core.copyarea.CopyAreaFile;
import com.ibm.rational.clearcase.remote_core.rpc.Session;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import java.io.File;
import java.net.URL;

public class ClearCaseCommand extends BaseClearCase {

    protected Session session;
    private CopyArea root;
    private String extraPath;
    private CopyAreaFile[] files;
    protected CopyArea copyArea;

    public ClearCaseCommand() {
        try {
            String url = new URL("http://taranis.ber.office.redknee.com:16080/ccrc").toString();
            session = new Session(url, new Credentials("rdelanth","",""));
            setRoot("/Users/rajith/projects/crossover/redknee/clearcase_path");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setRoot(String configPath) throws Exception {
        root = new CopyAreaFile(new File(configPath)).getCopyArea();
        extraPath = configPath.substring(root.getRoot().length() + 1)
                + File.separatorChar;
        String[] includes = new String[] { "." };
        files = new CopyAreaFile[includes.length];
        for (int i = 0; i < includes.length; i++) {
            files[i] = new CopyAreaFile(new File(new File(configPath),
                    includes[i]));
        }
        copyArea = files[0].getCopyArea();
    }

    public void checkout(String file){
        run(new Checkout(session, log(Checkout.Listener.class), null, false,
                (String) null, false, false, NonLatestTreatment.FAIL,
                singleFile(file)));
    }

    private CopyAreaFile[] singleFile(String file) {
        return new CopyAreaFile[] { copyFile(file) };
    }

    private CopyAreaFile copyFile(String file) {
        return new CopyAreaFile(root, extraPath + file);
    }


}
