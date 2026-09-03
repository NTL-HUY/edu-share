package com.nbh.edushare.modules.chat.util;

import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import com.nbh.edushare.modules.chat.repository.ChatMessageRepository;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageBuilder {

    private static final int PREVIEW_MAX_LENGTH = 100;
    private final ChatMessageRepository chatMessageRepository;


    public BuildResult build(Long roomId, UserBaseProjection sender, String content,
                             Long replyToMessageId, String clientTempId) {

        ChatMessage message = new ChatMessage();
        message.setRoomId(roomId);
        message.setUserId(sender.getId());
        message.setUserName(sender.getUsername());
        message.setUserAvatarUrl(sender.getAvatarUrl());
        message.setContent(content);
        if (clientTempId != null) {
            message.setClientTempId(clientTempId);
        }

        if (replyToMessageId != null) {
            ChatMessage original = chatMessageRepository.findById(replyToMessageId)
                    .filter(m -> m.getDeletedAt() == null)
                    .filter(m -> m.getRoomId().equals(roomId))
                    .orElse(null);

            if (original == null) {
                return BuildResult.failed("Tin nhắn để phản hồi không tồn tại");
            }

            message.setReplyToMessageId(original.getId());
            message.setReplyToUserName(original.getUserName());
            message.setReplyToContentPreview(buildPreview(original.getContent()));
        }

        return BuildResult.success(message);
    }

    private String buildPreview(String content) {
        if (content == null) return null;
        return content.length() <= PREVIEW_MAX_LENGTH
                ? content
                : content.substring(0, PREVIEW_MAX_LENGTH) + "...";
    }

    public record BuildResult(ChatMessage message, String errorReason) {
        public static BuildResult success(ChatMessage message) {
            return new BuildResult(message, null);
        }
        public static BuildResult failed(String reason) {
            return new BuildResult(null, reason);
        }
        public boolean isFailed() {
            return message == null;
        }
    }
}