# Specification 

## General description

This project represents a **communication channel between tasks**, it starts off with the class Broker that manages a task or multiple ones, the tasks use the channel to send information between each other. A channel is created when a broker is, a channel can't exist if a broker doesn't. The broker is of course synchronous 

Two types of brokers exist, a **client broker** and a **server broker**, they each are supposed to have a different name making the names unique and is used to connect with another broker followed by a port number. The name of the broker has to be the same as the other broker uses to connect to this broker, the port number should also be the same as the two broker uses to connect or accept or they won't communicate with each other. You should'nt have two times the same broker name in the same port number.

You can of course have the number of brokers that you want, a broker can have the number of tasks he wants, if two tasks that are in the same broker want to communicate, they are able to. A task is able to communicate with multiple brokers at the same time, a task has a static method called **getBroker** so you could be able to access the broker at any time during the execution of a task.

A channel is able to send bytes and receive them, this TCP channel is **FIFO**, **lossless** and **bidirectional** , which means that it ensures reliable, ordered, and error-checked delivery of a stream of bytes compared to UDP which is faster but doesn't ensure that there will be no errors.

We weren't fully capable to make sure that the project is multi-threaded, we tried to accomplish that by using a byte table as a parameter to the read function of the channel which makes it optimized, returning an int number defining the numbers of bytes that the channel successfully read managing errors if it returns -1. Joined by using the same method to make sure that the write method is synchronous, that still doesn't make the channel multi-threaded. **It is not multi-threaded**, but you can still use multiple threads but you will have to ensure that there will be no errors.

In our implementation, the **connect method** is designed to establish a robust and reliable connection between a client broker and a server broker. When a client broker invokes connect, it uses the provided broker name and port number to locate and communicate with the target server broker. The method initiates a network connection to ensure reliability and order in data transmission. Upon successfully establishing this connection, the method creates and returns a Channel object. This Channel acts as the conduit for all subsequent communication between the brokers. The connection setup process includes error handling mechanisms to manage scenarios where the server broker is unreachable, the port is incorrect, or any other connection issues arise. In such cases, the method may throw an exception or return a null value to indicate failure, providing clear feedback for troubleshooting.

The **disconnect method** in our implementation is responsible for gracefully closing an active connection. When invoked on a Channel object, this method ensures that all resources associated with the channel are properly released, and the connection is terminated. Internally, it might involve shutting down network streams, closing sockets, and cleaning up any temporary resources. The disconnection process is carefully managed to avoid data loss and ensure that the connection is closed in a consistent and reliable manner. Once disconnect has been called, the Channel is no longer valid for communication.

After invoking disconnect, the disconnected method provides a way to verify the current state of the channel. This method checks the internal status of the channel to determine if it has been successfully closed. If the channel is no longer active, disconnected returns true, signaling that the channel is in a disconnected state. Conversely, if the channel is still open or the disconnection process has not completed, it returns false. This **status check is essential** for managing operations and preventing attempts to use a closed channel, which could lead to exceptions or undefined behavior.

The **interaction between connect, disconnect, and disconnected** is crucial for managing the lifecycle of a communication channel. A successful connect operation results in a new channel that can be used for communication. When the channel is no longer needed, disconnect is called to close the connection and free resources. The disconnected method then allows us to confirm that the channel has been properly closed. This workflow ensures that connections are managed efficiently and that the system remains stable and responsive. The implementation of these methods involves careful handling of network operations and resource management to provide a seamless communication experience.

In essence, this implementation aims to provide a reliable and clean method for managing broker connections, ensuring that channels are properly opened, used, and closed, while also offering mechanisms to check the state of these channels effectively.


## What can it used for ?

This project can be used in various scenarios where inter-task communication is required. Some potential use cases include:

- Enabling microservices to communicate efficiently, for example, a payment service communicating with an order service in an e-commerce application.

- Managing communication between multiple concurrent tasks within the same application, like a web server handling multiple client requests simultaneously.
  
- Creating client-server applications where tasks need to communicate over a network, such as a chat application where messages are exchanged between clients through a server.