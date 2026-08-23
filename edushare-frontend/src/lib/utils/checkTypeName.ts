import type { FeedItem, FeedTypeMeta, Knowledge, Lesson, LessonFeedMeta, Question, QuestionFeedMeta } from "$lib/generated/types";


export function isLessonFeedMeta(item?: FeedTypeMeta | null): item is LessonFeedMeta {
  return item?.__typename === 'LessonFeedMeta';
}

export function isQuestionFeedMeta(item?: FeedTypeMeta | null): item is QuestionFeedMeta {
  return item?.__typename === 'QuestionFeedMeta';
}

export function isQuestion(knowledge: any): knowledge is Question {
   return knowledge?.__typename === 'Question' || knowledge?.type === 'QUESTION';
}

export function isLesson(knowledge: any): knowledge is Lesson {
   return knowledge?.__typename === 'Lesson' || knowledge?.type === 'LESSON';
}