package com.reborn.first.acceptor;

import quickfix.Group;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageUtils;
import quickfix.field.MsgType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static quickfix.FixVersions.BEGINSTRING_FIXT11;
import static quickfix.FixVersions.FIX50SP2;

/**
 * Created by xuqian_sx on 2017-8-11.
 * When the begin string is FIXT.1.1 quickfix will treat the message as FIX50 with the DefaultMessageFactory. So it will automatically generate a FIX.5.0 message.
 The resolution is to write your own custom message factory to generate a SP2 message when the transport is FIXT.1.1. Here's how I did it.
 Write a custom message factory implementing quickfix.MessageFactory interface. You can copy the DefaultMessageFactory code and change the create() method as follows.
 *
 */
public class MessageFactory5SP2 implements MessageFactory
{
    private final Map<String, MessageFactory> messageFactories = new ConcurrentHashMap();

    public MessageFactory5SP2() {
        this.addFactory("FIX.4.0");
        this.addFactory("FIX.4.1");
        this.addFactory("FIX.4.2");
        this.addFactory("FIX.4.3");
        this.addFactory("FIX.4.4");
        this.addFactory("FIXT.1.1");
        this.addFactory("FIX.5.0");
        this.addFactory("FIX.5.0SP1");
        this.addFactory("FIX.5.0SP2");
    }

    private void addFactory(String beginString) {
        String packageVersion = beginString.replace(".", "").toLowerCase();

        try {
            this.addFactory(beginString, "quickfix." + packageVersion + ".MessageFactory");
        } catch (ClassNotFoundException var4) {
            ;
        }

    }

    public void addFactory(String beginString, String factoryClassName) throws ClassNotFoundException {
        Class factoryClass = null;

        try {
            factoryClass = Class.forName(factoryClassName);
        } catch (ClassNotFoundException var5) {
            Thread.currentThread().getContextClassLoader().loadClass(factoryClassName);
        }

        if(factoryClass != null) {
            this.addFactory(beginString, factoryClass);
        }

    }

    public void addFactory(String beginString, Class<? extends MessageFactory> factoryClass) {
        try {
            MessageFactory factory = (MessageFactory)factoryClass.newInstance();
            this.messageFactories.put(beginString, factory);
        } catch (Exception var4) {
            throw new RuntimeException("can't instantiate " + factoryClass.getName(), var4);
        }
    }

    public Message create(String beginString, String msgType) {
        MessageFactory messageFactory = messageFactories.get(beginString);
        if (beginString.equals(BEGINSTRING_FIXT11)) {
            // The default message factory assumes that only FIX 5.0 will be
            // used with FIXT 1.1 sessions. A more flexible approach will require
            // an extension to the QF JNI API. Until then, you will need a custom
            // message factory if you want to use application messages prior to
            // FIX 5.0 with a FIXT 1.1 session.
            //
            // TODO: how do we support 50/50SP1/50SP2 concurrently?
            //
            // If you need to determine admin message category based on a data
            // dictionary, then use a custom message factory and don't use the
            // static method used below.
            if (!MessageUtils.isAdminMessage(msgType)) {
                messageFactory = messageFactories.get(FIX50SP2);
            }
        }

        if (messageFactory != null) {
            return messageFactory.create(beginString, msgType);
        }

        Message message = new Message();
        message.getHeader().setString(MsgType.FIELD, msgType);

        return message;
    }

    public Group create(String beginString, String msgType, int correspondingFieldID) {
        MessageFactory messageFactory = (MessageFactory)this.messageFactories.get(beginString);
        if(messageFactory != null) {
            return messageFactory.create(beginString, msgType, correspondingFieldID);
        } else {
            throw new IllegalArgumentException("Unsupported FIX version: " + beginString);
        }
    }
}
