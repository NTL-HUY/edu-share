package com.nbh.edushare.modules.interaction.adapter;

import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.interaction.mapper.CommentMapper;
import com.nbh.edushare.modules.interaction.repository.CommentRepository;
import com.nbh.edushare.modules.knowledge.port.CommentQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentQueryAdapter implements CommentQueryPort {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Optional<CommentResponse> getCommentById(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .map(commentMapper::toResponse);
    }
}
