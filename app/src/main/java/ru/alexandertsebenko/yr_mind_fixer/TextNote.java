package ru.alexandertsebenko.yr_mind_fixer;

public class TextNote {
    private long id;
    private String comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTextNote() {
        return comment;
    }

    public void setTextNote(String comment) {
        this.comment = comment;
    }
    @Override
    public String toString() {
        return comment;
    }
}
