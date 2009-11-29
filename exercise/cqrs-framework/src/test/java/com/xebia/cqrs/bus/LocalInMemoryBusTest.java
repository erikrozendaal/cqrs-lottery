package com.xebia.cqrs.bus;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.xebia.cqrs.commands.Command;
import com.xebia.cqrs.events.Event;
import com.xebia.cqrs.events.Notification;


public class LocalInMemoryBusTest {
    
    private LocalInMemoryBus subject;
    private TestCommandHandler commandHandler;
    private TestEventHandler eventHandler;

    private boolean testCommandHandlerCalled;
    private boolean testEventHandlerCalled;
    
    @Before
    public void setUp() {
        commandHandler = new TestCommandHandler();
        eventHandler = new TestEventHandler();
        subject = new LocalInMemoryBus();
        subject.setHandlers(commandHandler, eventHandler);
    }
    
    @Test
    public void shouldSendCommandToRegisteredHandler() {
        subject.sendAndWaitForResponse(new TestCommand("hello"));
        assertTrue(testCommandHandlerCalled);
        assertEquals("hello", commandHandler.lastMessage);
    }
    
    @Test
    public void shouldSendEventToRegisteredHandler() {
        subject.publish(new TestEvent(""));
        assertTrue(testEventHandlerCalled);
    }
    
    @Test
    public void shouldFailToReplyToPublishedEvents() {
        subject.setHandlers(new AbstractHandler<TestEvent>(TestEvent.class) {
            public void handleMessage(TestEvent message) {
                try {
                    subject.reply(new TestEvent("foo"));
                    fail("MessageHandlingException expected");
                } catch (MessageHandlingException expected) {
                }
            }
        });
        
        subject.publish(new TestEvent(""));
    }
    
    @Test
    public void shouldPostponeInvokingHandlersUntilCurrentMessageHasBeenProcessed() {
        subject.setHandlers(eventHandler, new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                subject.publish(new TestEvent(""));
                assertFalse(testEventHandlerCalled);
            }
        });

        subject.sendAndWaitForResponse(new TestCommand("test command"));
        
        assertTrue(testEventHandlerCalled);
    }
    
    @Test
    public void shouldRespondWithRepliedMessages() {
        subject.setHandlers(new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                subject.reply(new TestEvent("event"));
            }
        });
        
        Response response = subject.sendAndWaitForResponse(new TestCommand("hello"));
        
        assertTrue(response.containsEventOfType(TestEvent.class));
        assertEquals("event", response.getNotificationOfType(TestEvent.class).getMessage());
    }

    @Test
    public void shouldSupportNestedSendingOfCommands() {
        subject.setHandlers(new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                if (!testCommandHandlerCalled) {
                    subject.reply(new TestEvent("top"));
                    testCommandHandlerCalled = true;
                    Response response = subject.sendAndWaitForResponse(new TestCommand("there"));
                    assertTrue(response.containsEventOfType(TestEvent.class));
                    assertEquals("nested", response.getNotificationOfType(TestEvent.class).getMessage());
                } else {
                    subject.reply(new TestEvent("nested"));
                }
            }
        });
        
        Response response = subject.sendAndWaitForResponse(new TestCommand("hello"));
        
        assertTrue(response.containsEventOfType(TestEvent.class));
        assertEquals("top", response.getNotificationOfType(TestEvent.class).getMessage());
    }

    private static class TestCommand extends Command {
        private static final long serialVersionUID = 1L;
        private final String message;

        public TestCommand(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private static class TestEvent extends Event implements Notification {
        private static final long serialVersionUID = 1L;
        private final String message;

        public TestEvent(String message) {
            super(UUID.randomUUID(), 0);
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private class TestCommandHandler extends AbstractHandler<TestCommand> {
        private String lastMessage;
        
        public TestCommandHandler() {
            super(TestCommand.class);
        }

        public void handleMessage(TestCommand command) {
            assertSame(command, subject.getCurrentMessage());
            lastMessage = command.getMessage();
            testCommandHandlerCalled = true;
        }
    }
    
    private class TestEventHandler extends AbstractHandler<TestEvent> {
        public TestEventHandler() {
            super(TestEvent.class);
        }

        public void handleMessage(TestEvent event) {
            assertSame(event, subject.getCurrentMessage());
            testEventHandlerCalled = true;
        }
    }
    
}
