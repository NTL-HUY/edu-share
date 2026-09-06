package com.nbh.edushare.modules.knowledge.mapper;

import com.nbh.edushare.modules.knowledge.dto.command.CreateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.response.QuestionDetailResponse;
import com.nbh.edushare.modules.knowledge.pojo.Question;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface QuestionMapper {

    @Mapping(target = "type", constant = "QUESTION")
    @Mapping(target = "isResolved", constant = "false")
    Question toEntity(CreateQuestionCommand command);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    QuestionDetailResponse toDetailResponse(Question question);

    void updateEntityFromCommand(UpdateQuestionCommand command, @MappingTarget Question question);
}
