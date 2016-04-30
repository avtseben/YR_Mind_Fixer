package ru.alexandertsebenko.yr_mind_fixer;

public class Room {
    private long id;
    private String roomName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoom() {
        return roomName;
    }

    public void setRoom(String roomName) {
        this.roomName = roomName;
    }
    @Override
    public String toString() {
        return roomName;
    }
}
