# Specification 

## General description

### Overview : Broker / Channel

This project represents a **communication channel between tasks**, it starts off with the class Broker that manages a task or multiple ones, the tasks use the channel to send information between each other. A channel is created when a broker is, a channel can't exist if a broker doesn't. The broker is of course synchronous 

Two types of brokers exist, a **client broker** and a **server broker**, they each are supposed to have a different name making the names unique and is used to connect with another broker followed by a port number. The name of the broker has to be the same as the other broker uses to connect to this broker, the port number should also be the same as the two broker uses to connect or accept or they won't communicate with each other. You should'nt have two times the same broker name in the same port number.

A channel is a communication channel, a point-to-point stream of bytes, able to send bytes and receive them, this TCP connected channel is **FIFO**, **lossless** and **bidirectional** , which means that it ensures reliable, ordered, and error-checked delivery of a stream of bytes compared to UDP which is faster but doesn't ensure that there will be no errors.

- It is entirely thread-safe for the two tasks to read or write at   either end point of the channels concurrently. 
- Locally, at one end point, two tasks, one reading and the other writing, operating concurrently is safe also. 
- However, concurrent read operations or concurrent write operations are not safe on the same end point.  

A channel is either connected or disconnected. It is created connected and it becomes disconnected when either side requests a disconnect. There is no notion of the end of stream for a connected stream. To mark the end of a stream, the corresponding channel is simply disconnected.

You can of course have the number of brokers that you want, a broker can have the number of tasks he wants, if two tasks that are in the same broker want to communicate, they are able to. A task is able to communicate with multiple brokers at the same time, a task has a static method called **getBroker** so you could be able to access the broker at any time during the execution of a task.


### Connecting

When a channel is created, it is connected originnally, when a connect matches an accept. the name of the channel is the same as the remote broker, the given port is the one of an accept on that remote broker.

There isn't an order to the connect and accpet function, the first operation executed always waits for the second operation, which means that both of these operation are blocking until they both connect, they both return a fully connected and usable full-dulex chanel. To solve this problem, we can use a time-out, limiting the wait for the connection to happen.


In our implementation, the **connect method** is designed to establish a robust and reliable connection between a client broker and a server broker. When a client broker invokes connect, it uses the provided broker name and port number to locate and communicate with the target server broker. The method initiates a network connection to ensure reliability and order in data transmission. 

Upon successfully establishing this connection, the method creates and returns a Channel object. This Channel acts as the conduit for all subsequent communication between the brokers. The connection setup process includes error handling mechanisms to manage scenarios where the server broker is unreachable, the port is incorrect, or any other connection issues arise. In such cases, the method may throw an exception or return a null value to indicate failure, providing clear feedback for troubleshooting. But if the remote broker exists, the connect blocks until there is a matching accept otherwise so that a channel can be
constructed and returned. 

### Writing 

` write(byte[] bytes,int offset,int length)int`

When writing, the given byte array contains the bytes to write
from the given offset and for the given length. The range [offset,offset+length[ must be within the array boundaries, without wrapping around at either ends. 

The method "write" returns the number of bytes actually written that
may not be zero or negative. If zero would be return, the write operation blocks instead until it can make some progress.

Nota Bene: a channel is a stream, so although the write operation 
does take a range of bytes to write from an array of bytes, the
semantics is one that writes one byte at a time in the stream.

The method "write" blocks if there is no room to write any byte.
The rationale is to avoid spinning when an application tries to send
a certain number of bytes and the stream can make no progress. 
Here is an example:
```Java
  void send(byte[] bytes) {
    int remaining = bytes.length;
    int offset = 0;
    while (remaining!=0) {
      int n = channel.write(bytes,offset,remaining);
      offset += n;
      remaining -= n;
    }
  }
```
If the method "write" is currently blocked and the channel becomes
disconnected, the method will throw a channel exception. Invoking a write operation on a disconnected also throws a channel exception.

### Reading

`read(byte[] bytes,int offset,int length)int`

When reading, the given byte array will contain the bytes read,
starting at the given offset. The given length provides the maximum number of bytes to read. The range [offset,offset+length[ must be within the array boundaries, without wrapping around at either ends.

The method "read" will return the number of bytes actually read, that may not be zero or negative. If zero would be returned, the method "read" blocks instead, until some bytes become available.

The rationale is that a loop trying to read a given length, looping over until all the needed bytes are read will not induce an active polling. Here is an example:
```Java
  void receive(byte[] bytes) throws DisconnectedException {
    int remaining = bytes.length;
    int offset = 0;
    while (remaining!=0) {
      int n = channel.read(bytes,offset,remaining);
      offset += n;
      remaining -= n;
    }
  }
```
The end of stream is the same as being as the channel being disconnected, so the method will throw an exception (DisconnectedException). 

Note: notice that the disconnected exception does not always indicate an error, rarely in fact. The end of stream is an exceptional situation, but it is not an error. Remember that exceptions are not only for errors, but for exceptional situations, hence their name.
The disconnected exception may give some extra information regarding an error if an internal error caused the channel to disconnect.   

### Disconnecting 

The **disconnect method** in our implementation is responsible for gracefully closing an active connection. When invoked on a Channel object, this method ensures that all resources associated with the channel are properly released, and the connection is terminated. Internally, it might involve shutting down network streams, closing sockets, and cleaning up any temporary resources. The disconnection process is carefully managed to avoid data loss and ensure that the connection is closed in a consistent and reliable manner. Once disconnect has been called, the Channel is no longer valid for communication.

After invoking disconnect, the disconnected method provides a way to verify the current state of the channel. This method checks the internal status of the channel to determine if it has been successfully closed. If the channel is no longer active, disconnected returns true, signaling that the channel is in a disconnected state. Conversely, if the channel is still open or the disconnection process has not completed, it returns false. This **status check is essential** for managing operations and preventing attempts to use a closed channel, which could lead to exceptions or undefined behavior.

The **interaction between connect, disconnect, and disconnected** is crucial for managing the lifecycle of a communication channel. A successful connect operation results in a new channel that can be used for communication. When the channel is no longer needed, disconnect is called to close the connection and free resources. The disconnected method then allows us to confirm that the channel has been properly closed. This workflow ensures that connections are managed efficiently and that the system remains stable and responsive. The implementation of these methods involves careful handling of network operations and resource management to provide a seamless communication experience.

In essence, this implementation aims to provide a reliable and clean method for managing broker connections, ensuring that channels are properly opened, used, and closed, while also offering mechanisms to check the state of these channels effectively.

### Brokers and Multi-tasking

We weren't fully capable to make sure that the project is multi-threaded, we tried to accomplish that by using a byte table as a parameter to the read function of the channel which makes it optimized, returning an int number defining the numbers of bytes that the channel successfully read managing errors if it returns -1. Joined by using the same method to make sure that the write method is synchronous, that still doesn't make the channel multi-threaded. **It is not multi-threaded**, but you can still use multiple threads but you will have to ensure that there will be no errors.





## What can it used for ?

This project can be used in various scenarios where inter-task communication is required. Some potential use cases include:

- Enabling microservices to communicate efficiently, for example, a payment service communicating with an order service in an e-commerce application.

- Managing communication between multiple concurrent tasks within the same application, like a web server handling multiple client requests simultaneously.
  
- Creating client-server applications where tasks need to communicate over a network, such as a chat application where messages are exchanged between clients through a server.