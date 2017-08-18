package com.reborn.first.initiator;

import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.fix50sp2.Message;
import quickfix.MessageCracker;
import quickfix.fix50sp2.NewOrderSingle;

/**
 * Created by xuqian_sx on 2017-7-27.
 */
public class MyInitiatorApplication extends MessageCracker implements Application
{
    private static Logger log = Logger.getLogger(MyInitiatorApplication.class);

    public void onCreate(SessionID sessionID)
    {
        System.out.println("initiator:session created!");
    }

    public void onLogon(SessionID sessionID)
    {
        System.out.println("initiator:logon");
    }

    public void onLogout(SessionID sessionID)
    {
        System.out.println("initiator:on logout");
    }

    public void toAdmin(quickfix.Message message, SessionID sessionID)
    {
        System.out.println("initiator:to admin--->"+message);
    }

    public void fromAdmin(quickfix.Message message, SessionID sessionID)
    {
        System.out.println("initiator:from admin--->"+message);
    }

    public void toApp(quickfix.Message message, SessionID sessionID)
    {
        System.out.println("initiator:to App");
    }

    public void fromApp(quickfix.Message message, SessionID sessionID)
    {
        System.out.println("initiator from app---->"+message.toString());

        try
        {
            crack(message,sessionID);
        } catch (UnsupportedMessageType unsupportedMessageType)
        {
            unsupportedMessageType.printStackTrace();
        } catch (FieldNotFound fieldNotFound)
        {
            fieldNotFound.printStackTrace();
        } catch (IncorrectTagValue incorrectTagValue)
        {
            incorrectTagValue.printStackTrace();
        }
    }

    public void onMessage(quickfix.fix50sp2.NewOrderSingle order, SessionID sessionID)
    {
        System.out.println("initiator:收到NewOrderSingle消息！");
        System.out.println(order.toString());
    }

    /*public void onMessage(quickfix.fix50sp2.Quote quote,SessionID sessionID) throws FieldNotFound
    {
        System.out.println("initiator:收到Quote消息！");
        StringField mySf = quote.getField(new MyStringField());
        System.out.println("MyStringField的值为："+mySf.getValue());
    }*/
}
