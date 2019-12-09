package com.codegym.lastproject.service;

import com.codegym.lastproject.model.Comment;

import java.util.List;

public interface CommentService {
    void save(Comment comment);

    List<Comment> findByHouseId(Long houseId);

    void delete(Long id);
}
