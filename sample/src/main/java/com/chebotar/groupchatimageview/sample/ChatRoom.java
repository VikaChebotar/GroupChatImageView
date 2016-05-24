package com.chebotar.groupchatimageview.sample;

import java.util.List;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoom {
    private int id;
    private List<User> participants;
    private String lastMessage;
    private String lastMessageTime;

    public ChatRoom(int id, List<User> participants, String lastMessage, String lastMessageTime) {
        this.id = id;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
