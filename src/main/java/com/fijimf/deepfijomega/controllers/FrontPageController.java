package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.content.Post;
import com.fijimf.deepfijomega.repository.PostRepository;
import com.fijimf.deepfijomega.utils.MarkdownRenderer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FrontPageController {

    private final PostRepository postRepository;
    private final MarkdownRenderer markdownRenderer;
    private final Comparator<Post> pinnedPostComparator = (o1, o2) -> (o1.getPinPosition() == o2.getPinPosition()) ? o1.getDate().compareTo(o2.getDate()) : o1.getPinPosition() - o2.getPinPosition();
    private final Comparator<Post> recentPostComparator = Comparator.comparing(Post::getDate);

    public static class MarkedUpPost {
        private final Post post;
        private final String markup;

        public MarkedUpPost(Post post, String markup) {
            this.post = post;
            this.markup = markup;
        }

        public Post getPost() {
            return post;
        }

        public String getMarkup() {
            return markup;
        }
    }

    @Autowired
    public FrontPageController(PostRepository postRepository, MarkdownRenderer markdownRenderer) {
        this.postRepository = postRepository;
        this.markdownRenderer = markdownRenderer;
    }

    @GetMapping("/index")
    public String manageUsers(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        List<Post> pinnedPosts = new ArrayList<>();
        List<Post> recentPosts = new ArrayList<>();
        posts.forEach(p -> {
            if (p.getPinPosition() > 0) {
                pinnedPosts.add(p);
            } else {
                if (p.getDate().isAfter(LocalDateTime.now().minusMonths(1))) {
                    recentPosts.add(p);
                }
            }
        });

        pinnedPosts.sort(pinnedPostComparator);
        recentPosts.sort(recentPostComparator);
        pinnedPosts.addAll(recentPosts.subList(0,Math.min(recentPosts.size(),5)));
        List<MarkedUpPost> markedUpPosts = pinnedPosts.stream().map(p->new MarkedUpPost(p,markdownRenderer.renderMarkup(p.getContent()))).collect(Collectors.toList());
        model.addAttribute("posts", markedUpPosts);
        return "index";
    }

}
