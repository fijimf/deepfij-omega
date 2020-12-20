package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.repository.ModelRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticManager {
    private final ModelRunRepository repo;

    @Autowired
    public StatisticManager(ModelRunRepository repo) {
        this.repo = repo;
    }
}
