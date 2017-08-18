package com.reborn.first.acceptor;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import quickfix.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuqian_sx on 2017-7-27.
 */
public class MyAcceptor
{
    static {
        PropertyConfigurator.configure("D:\\资料\\QuickFixJ\\QuickFixTest\\src\\main\\resources\\log4j.properties");
    }

    private static Logger log = Logger.getLogger(MyAcceptor.class);

    private  SocketAcceptor acceptor = null;
    public MyAcceptor(SessionSettings sessionSettings) throws ConfigError, IOException
    {
        Application application = new MyAcceptorApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
        LogFactory logFactory = new FileLogFactory(sessionSettings);

        MessageFactory messageFactory = new DefaultMessageFactory();
        acceptor = new SocketAcceptor(application,messageStoreFactory,sessionSettings,logFactory,messageFactory);

    }

    private static InputStream getSettingsInputStream(String[] args) throws FileNotFoundException
    {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = MyAcceptor.class.getClassLoader().getResourceAsStream("acceptor.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + MyAcceptor.class.getName() + " [configFile].");
            System.exit(1);
        }
        return inputStream;
    }

    public static void main(String[] args)
    {
        try
        {
            InputStream inputStream  = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();

            MyAcceptor acceptor = new MyAcceptor(settings);
            acceptor.start();

            System.out.println("acceptor start!!!");
            //System.out.println("press <enter> to quit");
            /*System.in.read();

            acceptor.stop();*/
            //System.out.println("acceptor stop!!!");
        }catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void stop()
    {
        acceptor.stop();
    }

    private void start() throws ConfigError
    {
        acceptor.start();
    }
}
