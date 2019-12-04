package com.codegym.lastproject.service.impl;

import com.codegym.lastproject.repository.CommentRepository;
import com.codegym.lastproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
}
