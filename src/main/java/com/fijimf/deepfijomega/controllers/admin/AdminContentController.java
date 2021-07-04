package com.fijimf.deepfijomega.controllers.admin;

import com.fijimf.deepfijomega.controllers.admin.forms.PostForm;
import com.fijimf.deepfijomega.entity.content.Post;
import com.fijimf.deepfijomega.repository.PostRepository;
import com.fijimf.deepfijomega.utils.MarkdownRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminContentController {
    private final PostRepository postRepository;
    private final MarkdownRenderer markdownRenderer;

    @Autowired
    public AdminContentController(PostRepository postRepository, MarkdownRenderer markdownRenderer) {
        this.postRepository = postRepository;
        this.markdownRenderer = markdownRenderer;
    }

    @GetMapping("/admin/posts")
    public String managePosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return ("admin/posts");
    }

    @GetMapping("/admin/posts/view/{id}")
    public String manageUsers(Model model, @PathVariable("id") Long id) {
        Post post = postRepository.findById(id).orElseThrow(()->new RuntimeException("Post for" + id + "not found"));
        model.addAttribute("post", post);
        model.addAttribute("markup", markdownRenderer.renderMarkup(post.getContent()));
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
    public ModelAndView save(@ModelAttribute PostForm form, Model model) {
        Post p = postRepository.findById(form.getId()).map(form::updatePost).orElse(form.createPost());
        model.addAttribute("post", postRepository.save(p));
        return new ModelAndView("redirect:/admin/posts/view/" + p.getId());
    }

    @GetMapping("/admin/posts/delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        postRepository.deleteById(id);
        return new ModelAndView("redirect:/admin/posts");
    }
}
