# Specification 

## General description

This project represents a **communication channel between tasks**, it starts off with the class Broker that manages a task or multiple ones, the tasks use the channel to send information between each other. A channel is created when a broker is, a channel can't exist if a broker doesn't. The broker is of course synchronous 

Two types of brokers exist, a **client broker** and a **server broker**, they each are supposed to have a different name making the names unique and is used to connect with another broker followed by a port number. The name of the broker has to be the same as the other broker uses to connect to this broker, the port number should also be the same as the two broker uses to connect or accept or they won't communicate with each other. You should'nt have two times the same broker name in the same port number.

You can of course have the number of brokers that you want, a broker can have the number of tasks he wants, if two tasks that are in the same broker want to communicate, they are able to. A task is able to communicate with multiple brokers at the same time, a task has a static method called **getBroker** so you could be able to access the broker at any time during the execution of a task.

A channel is able to send bytes and receive them, this TCP channel is **FIFO**, **lossless** and **bidirectional** , which means that it ensures reliable, ordered, and error-checked delivery of a stream of bytes compared to UDP which is faster but doesn't ensure that there will be no errors.

We weren't fully capable to make sure that the project is multi-threaded, we tried to accomplish that by using a byte table as a parameter to the read function of the channel which makes it optimized, returning an int number defining the numbers of bytes that the channel successfully read managing errors if it returns -1. Joined by using the same method to make sure that the write method is synchronous, that still doesn't make the channel multi-threaded. **It is not multi-threaded**, but you can still use multiple threads but you will have to ensure that there will be no errors.

## What can it used for ?

This project can be used in various scenarios where inter-task communication is required. Some potential use cases include:

- **Microservices Architecture**: Enabling microservices to communicate efficiently, for example, a payment service communicating with an order service in an e-commerce application.

- **Concurrent Programming**: Managing communication between multiple concurrent tasks within the same application, like a web server handling multiple client requests simultaneously.
  
- **Networked Applications**: Creating client-server applications where tasks need to communicate over a network, such as a chat application where messages are exchanged between clients through a server.