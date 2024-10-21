package event.impl;

import mixed.impl.Message;

public interface ChannelListener {
void received(byte[] bytes);

void closed();

void sent(byte[] bytes);
}
