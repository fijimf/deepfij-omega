package com.fijimf.deepfijomega.controllers;

import com.fijimf.deepfijomega.entity.quote.Quote;
import com.fijimf.deepfijomega.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class QuoteEnrichingAdapter extends HandlerInterceptorAdapter {
    private final QuoteRepository quoteRepository;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    public QuoteEnrichingAdapter(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView!=null) {
            String k = (String) modelAndView.getModel().get("quote-key");
            getQuote(k).ifPresent(q -> modelAndView.addObject("quote", q));
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    private Optional<Quote> getQuote(String tag) {
        if (tag == null || random.nextBoolean()) {
            return quoteRepository.getRandomQuote();
        } else {
            return quoteRepository.getRandomQuote(tag).or(quoteRepository::getRandomQuote);
        }
    }
}
