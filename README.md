
**QueueBroker Class**
The QueueBroker class is an abstract class that provides a framework for managing message queues. It includes methods for binding and unbinding to ports, as well as connecting to other message queues. The class also defines interfaces for handling acceptance and connection events. This class is designed to be thread-safe and should handle synchronization to ensure consistent state across multiple threads.

**Constructor:**

`QueueBroker(String name):` Initializes the QueueBroker with a given name. This constructor is intended to be called by subclasses.
Interfaces:

**`AcceptListener:`** An interface for handling acceptance events.

`void accepted(MessageQueue queue):` Called when a message queue is accepted. Implementations of this method should handle the logic for when a new message queue connection is accepted.
***Detailed Explanation:***
*`Purpose:`* This method is invoked when a new message queue connection is successfully accepted.
**Parameters:**
`MessageQueue queue:` The message queue that has been accepted.
`Usage:` Implement this method to define what actions should be taken when a new message queue connection is accepted. For example, you might want to start processing messages from the newly accepted queue.
`Example:`
```Java
class MyAcceptListener implements QueueBroker.AcceptListener {
    @Override
    public void accepted(MessageQueue queue) {
        System.out.println("New queue accepted: " + queue.getName());
        // Start processing messages from the queue
    }
}
```


**`ConnectListener:`** An interface for handling connection events.

`void connected(MessageQueue queue):` Called when a connection to a message queue is established. Implementations of this method should handle the logic for when a connection to another message queue is successfully made.
**Detailed Explanation:**
*`Purpose:`* This method is invoked when a connection to another message queue is successfully established.
**Parameters:**
`MessageQueue queue:` The message queue that has been connected to.
`Usage:` Implement this method to define what actions should be taken when a connection to another message queue is established. For example, you might want to start sending messages to the connected queue.
`Example:`
```Java
class MyConnectListener implements QueueBroker.ConnectListener {
    @Override
    public void connected(MessageQueue queue) {
        System.out.println("Connected to queue: " + queue.getName());
        // Start sending messages to the queue
    }
}
```

`void refused():` Called when a connection to a message queue is refused. Implementations of this method should handle the logic for when a connection attempt fails.
**Detailed Explanation:**
*`Purpose:`* This method is invoked when a connection attempt to a message queue is refused.
**Parameters:** None.
`Usage:` Implement this method to define what actions should be taken when a connection attempt fails. For example, you might want to retry the connection or log an error message.

`Example:`
```Java
class MyConnectListener implements QueueBroker.ConnectListener {
    @Override
    public void refused() {
        System.out.println("Connection refused.");
        // Retry connection or log error
    }
}
```
**Methods:**

`boolean bind(int port, AcceptListener listener):` Binds the QueueBroker to a specified port and sets an AcceptListener to handle acceptance events. This method is synchronized to ensure thread safety when binding to a port. It returns true if the binding is successful, otherwise false.

`boolean unbind(int port):` Unbinds the QueueBroker from a specified port. This method is synchronized to ensure thread safety when unbinding from a port. It returns true if the unbinding is successful, otherwise false.

`boolean connect(String name, int port, ConnectListener listener):` Connects the QueueBroker to another message queue identified by name and port, and sets a ConnectListener to handle connection events. This method is synchronized to ensure thread safety when establishing connections. It returns true if the connection is successful, otherwise false.

**MessageQueue Class**
The MessageQueue class is a class that provides a framework for managing individual message queues. It includes methods for sending and receiving messages, setting listeners, and closing the queue. The class also defines an interface for handling message and closure events. This class is designed to be thread-safe and should handle synchronization to ensure consistent state across multiple threads.

**Constructor:**

`MessageQueue(String name):` Initializes the MessageQueue with a given name. This constructor is intended to be called by subclasses.
Interface:

`Listener:` An interface for handling message and closure events.
**`void received(byte[] msg):`** Called when a message is received. Implementations of this method should handle the logic for processing received messages.
**Detailed Explanation:**
`Purpose:` This method is invoked when a message is received by the message queue.
**`Parameters:`**
`byte[] msg:` The message received in byte array format.
`Usage:` Implement this method to define what actions should be taken when a message is received. For example, you might want to process the message or forward it to another component.
`Example:`

```Java
class MyListener implements MessageQueue.Listener {
    @Override
    public void received(byte[] msg) {
        System.out.println("Message received: " + new String(msg));
        // Process the message
    }
}
```
**`void closed():`** Called when the message queue is closed. Implementations of this method should handle the logic for when the message queue is closed.
**Detailed Explanation:**
`Purpose:` This method is invoked when the message queue is closed.
`Parameters:` None.
`Usage:` Implement this method to define what actions should be taken when the message queue is closed. For example, you might want to release resources or notify other components.
`Example:`

**Methods:**

`void setListener(Listener l):` Sets a Listener to handle message and closure events. This method is synchronized to ensure thread safety when setting the listener.

`boolean send(byte[] bytes):` Sends a message represented by a byte array through the message queue. This method is synchronized to ensure thread safety when sending messages. It returns true if the message is successfully sent, otherwise false. There is also the same method but with additionnal parameters : offset and length.

`void close():` Closes the message queue. This method is synchronized to ensure thread safety when closing the queue.

`boolean closed():` Checks if the message queue is closed. This method is synchronized to ensure thread safety when checking the state of the queue. It returns true if the queue is closed, otherwise false.

**Thread Safety and Synchronization**
Both QueueBroker and MessageQueue classes are designed to be thread-safe. This is achieved by synchronizing methods that modify the state of the objects or perform critical operations. Synchronization ensures that only one thread can execute these methods at a time, preventing race conditions and ensuring consistent state across multiple threads.

**Thinking ownership**

Asynchronous sending and performance requires you to think about the ownership transfer during the send operation. To avoid a copy or a definitive owner transfer, the queue must notify when a send operation is finished, returning the ownership of the buffer. It is also possible to use cookies to give a context to the message queue like how websites do. We could also replace the table of bytes by a class Message that will contain all the context and information needed, this will make it easier for further development by using classes that extend Message. This is all to manage aliasing. The best method that we will implement here is making sure that Message is notified when it is being sent, by using the messages as the receivers of the notification "sent", seems to separate better the ownership management from the reception of messages. It will also ensure that the messages are sent in order. Managing ownership in this application is important because it helps maintain data integrity and ensures that resources are properly allocated and released, preventing potential memory leaks and race conditions. 


**Summary**
The QueueBroker and MessageQueue abstract classes provide a structured way to manage message queues and their interactions. The QueueBroker class handles the higher-level operations of binding, unbinding, and connecting to message queues, while the MessageQueue class handles the lower-level operations of sending and receiving messages, as well as managing listeners for these events. Both classes are designed to be thread-safe, with synchronized methods to ensure consistent state across multiple threads. The use of interfaces allows for flexible handling of acceptance, connection, message reception, and closure events.