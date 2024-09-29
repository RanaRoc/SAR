# Specification 

## General description

### Overview : Broker / Channel

This project represents a **communication channel between tasks using message queues**, it starts off with the class Broker that manages a task or multiple ones, the tasks use the message queue to send information between each other. A **message queue** is created when a broker is, a message queue can't exist if a broker doesn't. The broker is of course synchronous.

Two types of queue brokers exist, a **client queue broker** and a **server queue broker**, they each are supposed to have a different name making the names unique and is used to connect with another broker followed by a port number. The name of the broker has to be the same as the other broker uses to connect to this broker, the port number should also be the same as the two broker uses to connect or accept or they won't communicate with each other. You should'nt have two times the same broker name in the same port number.

You need a Broker to create a queue broker, the queue broker does the same thing as the broker but it ensures that messages are sent and received as a whole with a variable-sized payload of bytes. That is, using the message queue.

A message queue is a communication channel,a point-to-point stream of bytes, able to send messages and receive them, this **UDP** connected channel is connectionless, unreliable, and unordered, which means that it provides faster delivery of a stream of bytes compared to TCP but does not ensure reliable, ordered, or error-checked delivery.

- It is entirely thread-safe for the two tasks to read or write at   either end point of the message queue concurrently. 
- Locally, at one end point, two tasks, one reading and the other writing, operating concurrently is safe also. 
- However, concurrent read operations or concurrent write operations are not safe on the same end point.  

A message queue is either connected or disconnected. It is created connected and it becomes disconnected when either side requests a disconnect. 

You can of course have the number of queue brokers that you want, a queue broker can have the number of tasks he wants, if two tasks that are in the same queue broker want to communicate, they are able to. A task is able to communicate with multiple queue brokers at the same time, a task has a static method called **getBroker** and **getQueueBroker** so you could be able to access the broker and queue broker at any time during the execution of a task.

Nota Bene : A queue broker uses the broker that is given as a parameter in its constructor to create everything it must. Message queue will also implement the class channel, the difference is the send and receive methods, there can be some losses when sending messages with message queues and the length of the messages can vary.

### Connecting

When a message queue is created, it is connected originnally, when a connect matches an accept. the name of the message queue is the same as the remote queue broker, the given port is the one of an accept on that remote queue broker.

There isn't an order to the connect and accpet function, the first operation executed always waits for the second operation, which means that both of these operation are blocking until they both connect, they both return a fully connected and usable full-dulex chanel. To solve this problem, we can use a time-out, limiting the wait for the connection to happen.

In our implementation, the **connect method** is designed to establish a robust and reliable connection between a client queue broker and a server queue broker. When a client queue broker invokes connect, it uses the provided queue broker name and port number to locate and communicate with the target server queue broker. The method initiates a network connection to ensure reliability and order in data transmission. 

Upon successfully establishing this connection, the method creates and returns a message queue object. This message queue acts as the conduit for all subsequent communication between the queue brokers. The connection setup process includes error handling mechanisms to manage scenarios where the server queue broker is unreachable, the port is incorrect, or any other connection issues arise. In such cases, the method may throw an exception or return a null value to indicate failure, providing clear feedback for troubleshooting. But if the remote queue broker exists, the connect blocks until there is a matching accept otherwise so that a message queue can be
constructed and returned. 

### Writing 

` write(byte[] bytes,int offset,int length)int`

When writing, the given byte array contains the bytes to write
from the given offset and for the given length. The range [offset,offset+length[ must be within the array boundaries, without wrapping around at either ends. 

The method "write" returns the number of bytes actually written that
may not be zero or negative. If zero would be return, this must mean that there was an error in sending the packet (message) ; The write method will be using the circular buffer.

The send method validates the offset and length, writes data to the buffer, waiting if the buffer is full and finally notifies other threads when data is added to the buffer. The buffer and send should be synchronized to ensure thread safety
```Java
  void send(byte[] bytes) {
    for (int i = 0; i < length; i++) {
                while (buffer.full()) {
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Thread interrupted while waiting for buffer space", e);
                    }
                }
                buffer.push(bytes[offset + i]);
                buffer.notifyAll();
    }
  }
```
If the method "write" is currently blocked and the message queue becomes
disconnected, the method will throw an exception. Invoking a write operation on a disconnected also throws an exception.

### Reading

`read(byte[] bytes,int offset,int length)int`

When receiving, it will read the whole message sent on the other side. The message returns a table of bytes that represents a message that was sent by the other side. Since its a UDP channel, there can be losses when sending messages but the process is very fast. The messages can also be received but not in order.


The rationale is that a loop trying to read a given length, looping over until all the needed bytes are read will not induce an active polling. Here is an example:
```Java
   public byte[] receive(int length) throws IOException {
        byte[] receivedData = new byte[length];
        int bytesRead = 0;
        synchronized (receiveBuffer) {
            while (bytesRead < length) {
                while (receiveBuffer.empty()) {
                    try {
                        receiveBuffer.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Thread interrupted while waiting for buffer data", e);
                    }
                }
                receivedData[bytesRead++] = receiveBuffer.pull();
                receiveBuffer.notifyAll();
            }
        }
        return receivedData;
    }
```
The end of stream is the same as being as the channel being disconnected, so the method will throw an exception (DisconnectedException). 

The provided receive method does not directly ensure the characteristics of UDP, but it does handle the reception of data in a way that is consistent with UDP's behavior. Here are some key points:

- Losses and Order: UDP is connectionless and does not guarantee message delivery, order, or error-checking. The receive method reads data from the CircularBuffer, which means it will handle whatever data has been received, but it does not ensure that all messages are received or that they are received in order.

- Blocking and Exceptions: The method waits for data to be available in the buffer and throws an exception if interrupted, which is consistent with handling the asynchronous nature of UDP.

- End of Stream: The method description mentions throwing a DisconnectedException when the end of the stream is reached, which aligns with handling exceptional situations in UDP communication.

Note: notice that the disconnected exception does not always indicate an error, rarely in fact. The end of stream is an exceptional situation, but it is not an error. Remember that exceptions are not only for errors, but for exceptional situations, hence their name.
The disconnected exception may give some extra information regarding an error if an internal error caused the message queue to disconnect.   

- Synchronous Operations: Both send and receive methods perform their operations in a blocking manner. They wait for the buffer to have space (in the case of send) or data (in the case of receive) before proceeding. This means that the methods will block the calling thread until the required condition is met.

- Thread Safety: The methods use synchronization (synchronized blocks) to ensure that the operations on the CircularBuffer are thread-safe. This means that multiple threads can safely call send and receive concurrently without causing data corruption or inconsistent states.
- 
### Disconnecting 

The **disconnect method** in our implementation is responsible for gracefully closing an active connection, it cam be called in either side of the connection, so it requires an asynchronous protocol. When invoked on a message queue object, this method ensures that all resources associated with the message queue are properly released, and the connection is terminated. Internally, it might involve shutting down network streams, closing sockets, and cleaning up any temporary resources. The disconnection process is carefully managed to avoid data loss and ensure that the connection is closed in a consistent and reliable manner. Once disconnect has been called, the message queue is no longer valid for communication.

`both ends may call the method "disconnect" concurrently and the protocol to disconnect the message queue must still work.`



After invoking disconnect, the disconnected method provides a way to verify the current state of the message queue. This method checks the internal status of the message queue to determine if it has been successfully closed. If the message queue is no longer active, disconnected returns true, signaling that the message queue is in a disconnected state, also this should make `read` and `write` illegal, only `disconnected` can be called. If read and write are invoked when disconnected returns true, a disconnected exception will be thrown.Conversely, if the message queue is still open or the disconnection process has not completed, it returns false. This **status check is essential** for managing operations and preventing attempts to use a closed message queue, which could lead to exceptions or undefined behavior.

The main issue is that there may be still bytes in transit, bytes that the local side must be able to reads. By in transit, we mean bytes that were written by that remote side, before it disconnected the message queue, and these bytes have not been read on a local side. 
Therefore, if we want the local side to be able to read these last bytes, the local side should not be considered disconnected until all these bytes have been read or the message queue is locally disconnected.

This means that the local side will only become disconnected when the remote has been disconnected and there are no more in-transit bytes to read. This means that a local message queue appears as not yet disconnected although its far side has already been disconnected. This means that we need to specify how should local write operations behave in 
this half-disconnected state. The simplest is to drop the bytes silently, as if they were written, preserving the local illusion that the message queue is still connected. 

First, allowing to read the last bytes in transit is mandatory since it is likely that a communication will end by writing some bytes and then disconnecting. Something like saying "bye" and 
then hanging up.


`One should resist the temptation to adopt an immediate synchronous disconnection. Indeed, it would not be possible if our message queues would not be implemented over shared memory. Disconnecting would imply sending a control message to inform the other side and thus the disconnect protocol would be asynchronous. `


The **interaction between connect, disconnect, and disconnected** is crucial for managing the lifecycle of a communication message queue. A successful connect operation results in a new message queue that can be used for communication. When the message queue is no longer needed, disconnect is called to close the connection and free resources. The disconnected method then allows us to confirm that the message queue has been properly closed. This workflow ensures that connections are managed efficiently and that the system remains stable and responsive. The implementation of these methods involves careful handling of network operations and resource management to provide a seamless communication experience.

In essence, this implementation aims to provide a reliable and clean method for managing broker connections, ensuring that message queues are properly opened, used, and closed, while also offering mechanisms to check the state of these message queues effectively.

### Queue brokers and Multi-tasking

The question about the relationship between tasks, queue brokers, and message queues.

Since a connect is blocking, a task may not try to connect to the same
name and port concurrently, but multiple tasks can. Similarly, only
one task may accept on a given port on a given queue broker. But different 
tasks on different queue brokers may accept on the same port number. And 
of course, multiple tasks may accept on different ports on the same
queue broker.

Since the operations "read" and "write" may block the calling task,
it is important to specify what happens if the message queue is disconnected
while tasks are blocked. The blocked operations will throw an exception (DisconnectedException). This must happen when the message queue is disconnected from either sides. This means that it is safe for a task to disconnect a message queue on the same side that another task is currently blocked on.

We know that each task is related to a queue broker, by its constructor. But a queue broker can be used by multiple tasks. Therefore queue brokers may be shared between tasks, so queue brokers must be thread-safe, using proper synchronized internally.


## What can it used for ?

This UDP message channel can be used in various scenarios where fast, connectionless communication is required. Some potential use cases include:

- Real-time Data Streaming: Transmitting real-time data such as live video or audio streams where occasional data loss is acceptable but low latency is crucial.

- Gaming: Enabling fast communication between game clients and servers, where speed is more critical than reliability.

- Broadcasting Messages: Sending broadcast or multicast messages to multiple recipients simultaneously, such as in a local network for service discovery.