package com.fijimf.deepfijomega.controllers.forms;

import com.fijimf.deepfijomega.entity.quote.Quote;
import com.fijimf.deepfijomega.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Random;

@Service
public class QuoteEnrichingAdapter extends HandlerInterceptorAdapter {
    @Autowired
    private final QuoteRepository quoteRepository;

    private final Random random = new Random();

    public QuoteEnrichingAdapter(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String k = (String) modelAndView.getModel().get("quote-key");
        getQuote(k).ifPresent(q -> modelAndView.addObject("quote", q));
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
