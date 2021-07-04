package com.fijimf.deepfijomega.controllers.admin.forms;

import com.fijimf.deepfijomega.entity.content.Post;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class PostForm {
    private final long id;
    private final String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime date;
    private final boolean keepDate;
    private final int pinPosition;
    private final String content;
    private final boolean hidden;

    public PostForm(long id, String title, LocalDateTime date, boolean keepDate, String content, int pinPosition, boolean hidden) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.keepDate = keepDate;

        this.content = content;
        this.pinPosition = pinPosition;
        this.hidden = hidden;
    }

    public static PostForm create() {
        return new PostForm(0L, "", LocalDateTime.now(), true, "", 0, false);
    }

    public static PostForm of(Post post) {
        return new PostForm(post.getId(), post.getTitle(), post.getDate(), true, post.getContent(), post.getPinPosition(), post.isHidden());
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

    public boolean isKeepDate() {
        return keepDate;
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

    public Post updatePost(Post p) {
        p.setContent(getContent());
        if (!isKeepDate()) {
            p.setDate(LocalDateTime.now());
        }
        p.setHidden(isHidden());
        p.setTitle(getTitle());
        p.setPinPosition(getPinPosition());
        return p;
    }

    public Post createPost() {
        return new Post(0L, getTitle(), LocalDateTime.now(), getContent(), getPinPosition(), isHidden());
    }
}
