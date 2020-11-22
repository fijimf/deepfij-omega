package com.fijimf.deepfijomega.entity.content;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private LocalDateTime date;
    private String content;
    private int pinPosition;
    private boolean hidden;


    public Post() {
    }

    public Post(long id, String title, LocalDateTime date, String content, int pinPosition, boolean hidden) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.pinPosition = pinPosition;
        this.hidden = hidden;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public int getPinPosition() {
        return pinPosition;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPinPosition(int pinPosition) {
        this.pinPosition = pinPosition;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

