package com.codegym.lastproject.service.impl;

import com.codegym.lastproject.model.Comment;
import com.codegym.lastproject.repository.CommentRepository;
import com.codegym.lastproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByHouseId(Long houseId) {
        return commentRepository.findAllByHouseId(houseId);
    }
}
