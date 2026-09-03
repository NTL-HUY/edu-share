// lib/types/feed.types.ts
export type FeedItemType = 'LESSON' | 'QUESTION' | string;

export interface LessonFeedMeta {
  __typename?: 'LessonFeedMeta';
  level: string;
  estimateTimeInMinutes: number;
  contentMarkdown: string;
}

export interface QuestionFeedMeta {
  __typename?: 'QuestionFeedMeta';
  isResolved: boolean;
  acceptedAnswerId: string | null;
  content: string;
}

export type FeedItemTypeMeta = LessonFeedMeta | QuestionFeedMeta;

export interface FeedItem {
  knowledgeId: string;
  type: FeedItemType;
  ownerId: string;
  ownerName: string;
  ownerAvatarUrl: string | null;
  title: string;
  abstract: string | null;
  thumbnailUrl: string | null;
  categoryId: string;
  categoryName: string;
  viewsCount: number;
  voteScore: number;
  commentCount: number;
  sourceCreatedAt: string;
  typeMeta: FeedItemTypeMeta | null;
}

export interface FeedQueryInput {
  cursor?: string | null;
  limit?: number;
}

export interface FeedResponse {
  items: FeedItem[];
  nextCursor: string | null;
  hasMore: boolean;
}