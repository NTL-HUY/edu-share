package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.dto.request.IncomingChatMessage;
import com.nbh.edushare.modules.chat.event.ChatMessageEvent;
import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    ChatMessageEvent toChatMessageEvent(IncomingChatMessage request, Long userId);

    // Các field này KHÔNG có trong ChatMessageEvent → phải set tay sau khi map,
    // nên khai báo ignore để MapStruct không báo lỗi "unmapped target property"
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userAvatarUrl", ignore = true)
    @Mapping(target = "replyToUserName", ignore = true)
    @Mapping(target = "replyToContentPreview", ignore = true)
    ChatMessage fromChatMessageEvent(ChatMessageEvent chatMessageEvent);
}
