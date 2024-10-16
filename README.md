# 1. Introduction

An event-based broker and channel system is crucial for distributed systems that utilize asynchronous communication between actors. The system ensures the reliable transmission of events (messages) between distributed processes or actors. This document outlines the specification for an event-driven architecture focusing on brokers and channels.

# 2. Event-Based Broker System Overview

The broker acts as an intermediary, receiving and routing messages between actors. It manages communication by providing message queues for each actor and ensuring that messages are delivered based on event triggers.
Key Components:

- Actors: Independent entities that perform tasks. Actors communicate via events.
- Broker: The central message dispatcher that handles the routing and management of events.
- Channels: The conduits through which events are transmitted. These are typically based on protocols like TCP for reliable delivery.

# 3. Broker Design

The broker is designed to handle communication between distributed actors. Each actor registers with the broker, which maintains a message queue for each actor. The broker's responsibilities include:

- Message Dispatching: Ensuring events reach the correct recipient.
- Queue Management: Handling message queues for actors, ensuring FIFO (First In, First Out) delivery.
- Channel Management: Setting up and managing channels, typically using TCP/IP to provide reliable and ordered message delivery.

# 4. Event Channels

Event channels facilitate communication between actors. A key requirement for the channels is ensuring reliable delivery. Channels are designed using TCP/IP to maintain the order and reliability of the messages.
Code Design Explanation

The code design for the event-oriented channel involves two main components:

- Listener: Defines the actions that should occur when data is written, read, or when a channel is available.
- Channel: Represents the communication channel that actors use to send and receive messages. It allows setting a listener for event notifications, reading from the channel, and writing to the channel.

### Listener Interface

The Listener interface specifies the event-based methods that the system will use to notify when data is written or read on a channel and when the channel is available for further communication.

``` java

interface Listener {
    void written(int nbytes);  // Called when data has been written to the channel
    void read(byte[] bytes);   // Called when data has been read from the channel
    void available();          // Called when the channel becomes available
}
```
- `written(int nbytes):` This method is called after data has been successfully written to the channel. It indicates how many bytes were written.
- `read(byte[] bytes):` This method is triggered when new data is read from the channel. The received data is passed as a byte array.
- `available():` Notifies when the channel becomes available for new operations, signaling that it is ready to read or write more data.


### Channel Class

The Channel class is responsible for handling low-level communication between actors. It offers methods to set a listener, read from the channel, and write to the channel.

```java

class Channel {
    private Listener listener;

    // Sets the listener to be notified for events on this channel
    void setListener(Listener l) {
        this.listener = l;
    }

    // Reads bytes from the channel into the provided byte array
    int read(byte[] bytes, int offset, int length) {
        // Logic to read data from the channel (TCP, etc.)
        int bytesRead = 0;
        // Notify listener that data has been read
        if (listener != null) {
            listener.read(bytes);
        }
        return bytesRead;
    }

    // Writes bytes to the channel from the provided byte array
    int write(byte[] bytes, int offset, int length) {
        // Logic to write data to the channel
        int bytesWritten = 0;
        // Notify listener that data has been written
        if (listener != null) {
            listener.written(bytesWritten);
        }
        return bytesWritten;
    }
}
```
- `setListener(Listener l):` This method sets the Listener that will receive notifications when events (such as data read or write) occur on the channel.
- `read(byte[] bytes, int offset, int length):` Reads a specified number of bytes from the channel into the byte array starting from the given offset. After reading, it triggers the read() method of the listener.
- `write(byte[] bytes, int offset, int length):` Writes a specified number of bytes to the channel from the byte array starting from the given offset. After writing, it triggers the written() method of the listener.

### Design Considerations

- The channel operates asynchronously, using the Listener interface to notify when the channel is ready or when operations (read/write) have occurred. This event-driven model ensures the actors can remain responsive, handling communication events without blocking their tasks.
- The Channel class allows data transmission between actors using reliable mechanisms (e.g., TCP), while the Listener provides a clean abstraction for handling asynchronous events triggered by these data transfers.

## 6. Consensus in Distributed Systems

Reaching consensus is a challenge in distributed systems. The broker must ensure that all actors in a group agree on the state of a message or a decision.

# 7. Channel Design and Optimization

Channels must be designed to handle network conditions effectively. To achieve this, the system employs several mechanisms:

- Lossless and FIFO Delivery: Ensured by using TCP sockets for communication between actors.
- Handling Network Partitions: In the event of network partitions, channels must buffer messages and attempt to deliver them when the network is restored.

Channels also utilize round-trip messaging to handle the voluntary closing of queues and ensure actors are notified when channels are closed, reducing the risk of message loss.

