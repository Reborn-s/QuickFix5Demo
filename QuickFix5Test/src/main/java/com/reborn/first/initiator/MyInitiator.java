package com.reborn.first.initiator;

import com.reborn.first.acceptor.MessageFactory5SP2;
import org.apache.log4j.Logger;
import quickfix.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuqian_sx on 2017-7-27.
 */
public class MyInitiator
{
    private final static Logger log = Logger.getLogger(MyInitiator.class);
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
    public MyInitiator(SessionSettings sessionSettings) throws ConfigError, IOException
    {
        Application application = new MyInitiatorApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
        LogFactory logFactory = new FileLogFactory(sessionSettings);
        MessageFactory messageFactory = new MessageFactory5SP2();
        initiator = new SocketInitiator(application,messageStoreFactory,sessionSettings,logFactory,messageFactory);

    }

    private static InputStream getSettingsInputStream(String[] args) throws FileNotFoundException
    {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = MyInitiator.class.getClassLoader().getResourceAsStream("initiator.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("configfile not found");
            System.exit(1);
        }
        return inputStream;
    }

    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            InputStream inputStream  = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();

            MyInitiator myInitiator = new MyInitiator(settings);
            myInitiator.logon();

            System.in.read();

        }catch (Exception e)
        {
            System.out.println(e);
        }
    }

}
