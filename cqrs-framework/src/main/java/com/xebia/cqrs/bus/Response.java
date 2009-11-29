package com.xebia.cqrs.bus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Iterables;

public class Response {

    private final List<Object> messages = new ArrayList<Object>();

    public Response() {
    }
    
    public Response(Iterable<?> messages) {
        Iterables.addAll(this.messages, messages);
    }
    
    public List<Object> getMessages() {
        return messages;
    }

    public boolean containsReplyOfType(Class<?> type) {
        for (Object message : messages) {
            if (type.isInstance(message)) {
                return true;
            }
        }
        return false;
    }

    public <T> T getReplyOfType(Class<T> type) {
        List<T> notifications = getRepliesOfType(type);
        if (notifications.isEmpty()) {
            throw new IllegalArgumentException("no notification of type " + type.getName());
        } else if (notifications.size() > 1) {
            throw new IllegalArgumentException("multiple notifications of type " + type.getName());
        } else {
            return notifications.get(0);
        }
    }
    
    public <T> List<T> getRepliesOfType(Class<T> type) {
        ArrayList<T> result = new ArrayList<T>();
        for (Object notification : messages) {
            if (type.isInstance(notification)) {
                result.add(type.cast(notification));
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
