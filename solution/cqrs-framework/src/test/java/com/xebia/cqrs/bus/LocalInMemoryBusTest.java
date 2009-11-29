package com.xebia.cqrs.bus;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.xebia.cqrs.util.EqualsSupport;


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
        subject.send(new TestCommand("hello"));
        assertTrue(testCommandHandlerCalled);
        assertEquals("hello", commandHandler.lastMessage);
    }
    
    @Test
    public void shouldSendEventToRegisteredHandler() {
        subject.publish(new TestEvent(""));
        assertTrue(testEventHandlerCalled);
    }
    
    @Test
    public void shouldFailToReplyWhenThereIsNoCurrentMessage() {
        try {
            subject.reply(new TestEvent("foo"));
            fail("MessageHandlingException expected");
        } catch (MessageHandlingException expected) {
            assertEquals("no current message to reply to", expected.getMessage());
        }
    }
    
    @Test
    public void shouldInvokeMessageHandlerForReply() {
        subject.setHandlers(eventHandler, new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                subject.reply(new TestEvent(""));
                assertFalse(testEventHandlerCalled);
            }
        });

        subject.sendAndWaitForResponse(new TestCommand("test command"));
        
        assertTrue(testEventHandlerCalled);
    }
    
    @Test
    public void shouldPostponeInvokingHandlersUntilCurrentMessageHasBeenProcessed() {
        subject.setHandlers(eventHandler, new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                subject.publish(new TestEvent(""));
                assertFalse(testEventHandlerCalled);
            }
        });

        subject.send(new TestCommand("test command"));
        
        assertTrue(testEventHandlerCalled);
    }
    
    @Test
    public void shouldRespondWithRepliedMessages() {
        subject.setHandlers(eventHandler, new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                subject.reply(new TestEvent("event"));
            }
        });
        
        Response response = subject.sendAndWaitForResponse(new TestCommand("hello"));
        
        assertTrue(response.containsReplyOfType(TestEvent.class));
        assertEquals("event", response.getReplyOfType(TestEvent.class).getMessage());
    }

    @Test
    public void shouldSupportNestedSendingOfCommands() {
        subject.setHandlers(eventHandler, new AbstractHandler<TestCommand>(TestCommand.class) {
            public void handleMessage(TestCommand message) {
                if (!testCommandHandlerCalled) {
                    subject.reply(new TestEvent("top"));
                    testCommandHandlerCalled = true;
                    Response response = subject.sendAndWaitForResponse(new TestCommand("there"));
                    assertTrue(response.containsReplyOfType(TestEvent.class));
                    assertEquals("nested", response.getReplyOfType(TestEvent.class).getMessage());
                } else {
                    subject.reply(new TestEvent("nested"));
                }
            }
        });
        
        Response response = subject.sendAndWaitForResponse(new TestCommand("hello"));
        
        assertTrue(response.containsReplyOfType(TestEvent.class));
        assertEquals("top", response.getReplyOfType(TestEvent.class).getMessage());
    }

    private static class TestCommand extends EqualsSupport {
        private final String message;

        public TestCommand(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private static class TestEvent extends EqualsSupport {
        private final String message;

        public TestEvent(String message) {
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
