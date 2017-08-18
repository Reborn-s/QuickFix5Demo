package com.reborn.first.acceptor;

import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.field.*;

import java.util.Date;

/**
 * Created by xuqian_sx on 2017-7-27.
 */
public class MyAcceptorApplication extends MessageCracker implements Application
{
    private static Logger log = Logger.getLogger(MyAcceptor.class);

    public void onCreate(SessionID sessionID)
    {
        System.out.println("acceptor:服务器启动时候调用此方法创建");
    }

    public void onLogon(SessionID sessionID)
    {
        System.out.println("acceptor:客户端登陆成功");
    }

    public void onLogout(SessionID sessionID)
    {
        System.out.println("acceptor:客户端断开连接");
    }

    public void toAdmin(Message message, SessionID sessionID)
    {
        System.out.println("acceptor:发送会话消息成功");
    }

    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
    {
        System.out.println("acceptor from admin:接收会话类型消息时调用此方法");
        try
        {
            crack(message,sessionID);
        } catch (UnsupportedMessageType unsupportedMessageType)
        {
            unsupportedMessageType.printStackTrace();
        }
    }

    public void toApp(Message message, SessionID sessionID) throws DoNotSend
    {
        System.out.println("acceptor to App:发送业务消息成功");
        System.out.println(sessionID.toString());
    }

    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
    {
        System.out.println("acceptor from App："+message.toString());
        crack(message,sessionID);
    }

    public void onMessage(Message message,SessionID sessionID)
    {
        try
        {
            System.out.println("业务逻辑实现统一写在此方法中");
            String msgType = message.getHeader().getString(35);
            Session session = Session.lookupSession(sessionID);
            System.out.println("服务器接收到用户信息订阅==" + msgType);
            if(msgType.equals(MsgType.LOGON))
            {
                session.logon();
                session.sentLogon();
            }else
            {
                log.info("acceptor发送消息-----"+session);
                sendMessage(sessionID);
//                sendQuoteMessage(sessionID);
            }

        }catch (FieldNotFound e)
        {
            e.printStackTrace();
        } catch (SessionNotFound sessionNotFound)
        {
            sessionNotFound.printStackTrace();
        }
    }

    private void sendMessage(SessionID sessionID) throws SessionNotFound
    {
        quickfix.fix50sp2.NewOrderSingle newOrderSingle = new quickfix.fix50sp2.NewOrderSingle(
                new ClOrdID("1234"),new Side(Side.BUY),new TransactTime(new Date()),new OrdType(OrdType.LIMIT)
        );

        newOrderSingle.set(new Symbol("00001"));
        newOrderSingle.set(new OrderQty(330));
        Session.sendToTarget(newOrderSingle,sessionID);
        log.info(newOrderSingle);
        log.info(sessionID);
    }

    /*private void sendQuoteMessage(SessionID sessionID) throws SessionNotFound
    {
        quickfix.fix42.Quote quote = new quickfix.fix42.Quote(
                new QuoteID("1234"),
                new Symbol("330")
        );

        MyStringField sf = new MyStringField("This is myString");
        quote.setField(sf);
        Session.sendToTarget(quote,sessionID);
        log.info(quote);
    }*/

    public void onMessage(quickfix.fix50sp2.NewOrderSingle order, SessionID sessionID) throws FieldNotFound,
            UnsupportedMessageType, IncorrectTagValue, SessionNotFound
    {
        quickfix.fix50sp2.NewOrderSingle newOrderSingle = new quickfix.fix50sp2.NewOrderSingle(
                new ClOrdID("1234"),
                new Side(Side.BUY),
                new TransactTime(new Date()),
                new OrdType(OrdType.LIMIT)
        );
        newOrderSingle.set(new Symbol("00001"));
        newOrderSingle.set(new OrderQty(330));
        Session.sendToTarget(newOrderSingle,sessionID);
    }
}
