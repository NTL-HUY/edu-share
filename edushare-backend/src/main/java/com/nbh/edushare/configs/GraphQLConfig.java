package com.nbh.edushare.configs;

import com.nbh.edushare.modules.knowledge.dto.response.LessonDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.QuestionDetailResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.type("Knowledge", typeWriting -> typeWriting
                .typeResolver(env -> {
                    Object src = env.getObject();
                    if (src instanceof LessonDetailResponse) {
                        return env.getSchema().getObjectType("Lesson");
                    } else if (src instanceof QuestionDetailResponse) {
                        return env.getSchema().getObjectType("Question");
                    }
                    return null;
                })
        );
    }
}
