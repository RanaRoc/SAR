package impl;

import code.MessageQueue;
import impl.events.ReceiveEvent;
import impl.events.SendEvent;
public class MessageQueueImpl extends MessageQueue {

    private boolean isClosed;
    private ChannelImpl channel;
    private MessageListener listener;
    private Message message;
    private Message messageLength;
    private MessageQueueImpl remoteQueue;
    private Message sizeSent;

    public MessageQueueImpl(ChannelImpl channel) {
        super();
        this.channel = channel;
        this.isClosed = false;
        this.messageLength = new Message(4);
    }

    @Override
    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void send(Message message) {
        SendEvent event = new SendEvent(this, message);
        new ReceiveEvent(remoteQueue);
    }

    public boolean assureSend(Message message) {
        int bytesSent = 0;
        int blockSize = 16;

        if (sizeSent == null) {
            int length = message.getLength();
            sizeSent = createMessageSize(length);
        }

        try {
            if (sizeSent.getOffset() < 4) {
                bytesSent += channel.write(sizeSent.getBytes(), 0, 4);
                sizeSent.setOffset(sizeSent.getOffset() + bytesSent);
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        bytesSent = 0;
        int remaining = message.getLength() - message.getOffset();
        int toSend = Math.min(blockSize, remaining);

        if (message.getOffset() < message.getLength()) {
            try {
                bytesSent += channel.write(message.getBytes(), message.getOffset(), toSend);
                message.setOffset(message.getOffset() + bytesSent);
            } catch (Exception e) {
                return false;
            }
        }

        if (message.getOffset() < message.getLength()) {
            return false;
        }

        sizeSent = null;
        listener.sent(message);
        return true;
    }

    public boolean assureReceive() {
        int bytesReceived = 0;
        int blockSize = 16;

        if (messageLength.getOffset() < 4) {
            try {
                bytesReceived += channel.read(messageLength.getBytes(), messageLength.getOffset(), 4 - messageLength.getOffset());
                messageLength.setOffset(messageLength.getOffset() + bytesReceived);
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        bytesReceived = 0;

        if (message == null) {
            int length = extractMessageLength();
            message = new Message(length);
        }

        if (message.getOffset() < message.getLength()) {
            int remaining = message.getLength() - message.getOffset();
            int toReceive = Math.min(blockSize, remaining);

            try {
                bytesReceived += channel.read(message.getBytes(), message.getOffset(), toReceive);
                message.setOffset(message.getOffset() + bytesReceived);
                if (message.getOffset() < message.getLength()) {
                    return false;
                }
            } catch (Exception e) {
               
                return false;
            }
        }

        listener.received(message.getBytes());
        resetMessageLength();
        resetMessage();
        return true;
    }

    @Override
    public void close() {
        channel.disconnect();
    }

    @Override
    public boolean closed() {
        return isClosed;
    }

    private void resetMessageLength() {
        messageLength = new Message(4);
    }

    private void resetMessage() {
        message = null;
    }

    public void setMsq(MessageQueueImpl mq) {
        remoteQueue = mq;
    }

    private Message createMessageSize(int length) {
        Message size = new Message(4);
        size.getBytes()[0] = (byte) ((length & 0xFF000000) >> 24);
        size.getBytes()[1] = (byte) ((length & 0x00FF0000) >> 16);
        size.getBytes()[2] = (byte) ((length & 0x0000FF00) >> 8);
        size.getBytes()[3] = (byte) (length & 0x000000FF);
        return size;
    }

    private int extractMessageLength() {
        byte[] sizeBytes = messageLength.getBytes();
        return ((sizeBytes[0] & 0xFF) << 24) |
               ((sizeBytes[1] & 0xFF) << 16) |
               ((sizeBytes[2] & 0xFF) << 8) |
               (sizeBytes[3] & 0xFF);
    }
}
