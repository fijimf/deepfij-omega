package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.controllers.forms.PostForm;
import com.fijimf.deepfijomega.entity.content.Post;
import com.fijimf.deepfijomega.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AdminContentController {
    @Autowired
    PostRepository postRepository;


    @GetMapping("/admin/posts")
    public String managePosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return ("admin/posts");
    }

    @GetMapping("/admin/posts/view/{id}")
    public String manageUsers(Model model, @PathVariable("id") Long id) {
        model.addAttribute("post", postRepository.findById(id));
        return "admin/viewPost";
    }

    @GetMapping("/admin/posts/edit/{id}")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("postForm", postRepository.findById(id).map(PostForm::of).orElse(PostForm.create()));
        return "admin/editPost";
    }

    @GetMapping("/admin/posts/create")
    public String create(Model model) {
        model.addAttribute("postForm", PostForm.create());
        return "admin/editPost";
    }

    @PostMapping("/admin/posts/save")
    public String save(@ModelAttribute PostForm form, Model model) {
        Post p = postRepository.findById(form.getId()).map(form::updatePost).orElse(form.createPost());
        model.addAttribute("post", postRepository.save(p));
        return "/admin/viewPost";
    }

    @GetMapping("/admin/posts/delete/{id}")
    public ModelAndView delete(Model model, @PathVariable("id") Long id) {
        postRepository.deleteById(id);
        return new ModelAndView("redirect:/admin/posts");
    }
}
