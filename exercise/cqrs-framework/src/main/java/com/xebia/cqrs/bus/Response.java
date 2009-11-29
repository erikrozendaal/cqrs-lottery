package com.xebia.cqrs.bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.xebia.cqrs.events.Notification;

public class Response {

    private final List<Notification> notifications = new ArrayList<Notification>();

    public Response(Notification... notifications) {
        this(Arrays.asList(notifications));
    }
    
    public Response(Collection<? extends Notification> notifications) {
        this.notifications.addAll(notifications);
    }
    
    public List<Notification> getEvents() {
        return notifications;
    }

    public boolean containsEventOfType(Class<?> type) {
        for (Notification notification : notifications) {
            if (type.isInstance(notification)) {
                return true;
            }
        }
        return false;
    }

    public <T extends Notification> T getNotificationOfType(Class<T> type) {
        List<T> notifications = getNotificationsOfType(type);
        if (notifications.isEmpty()) {
            throw new IllegalArgumentException("no notification of type " + type.getName());
        } else if (notifications.size() > 1) {
            throw new IllegalArgumentException("multiple notifications of type " + type.getName());
        } else {
            return notifications.get(0);
        }
    }
    
    public <T extends Notification> List<T> getNotificationsOfType(Class<T> type) {
        ArrayList<T> result = new ArrayList<T>();
        for (Notification notification : notifications) {
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
