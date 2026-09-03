/** Internal type. DO NOT USE DIRECTLY. */
type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
/** Internal type. DO NOT USE DIRECTLY. */
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
};

export type Category = {
  __typename?: 'Category';
  id: Scalars['ID']['output'];
  name: Scalars['String']['output'];
};

export type ChatMessage = {
  __typename?: 'ChatMessage';
  clientTempId: Scalars['String']['output'];
  content: Scalars['String']['output'];
  createdAt: Scalars['String']['output'];
  id: Scalars['ID']['output'];
  replyToContentPreview?: Maybe<Scalars['String']['output']>;
  replyToMessageId?: Maybe<Scalars['ID']['output']>;
  replyToUserName?: Maybe<Scalars['String']['output']>;
  roomId: Scalars['ID']['output'];
  userAvatarUrl?: Maybe<Scalars['String']['output']>;
  userId: Scalars['ID']['output'];
  userName: Scalars['String']['output'];
};

export type ChatRoom = {
  __typename?: 'ChatRoom';
  description?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  name: Scalars['String']['output'];
  unreadCount: Scalars['Int']['output'];
};

export type Comment = {
  __typename?: 'Comment';
  content: Scalars['String']['output'];
  createdAt: Scalars['String']['output'];
  deletedAt?: Maybe<Scalars['String']['output']>;
  deletedBy?: Maybe<Scalars['ID']['output']>;
  id: Scalars['ID']['output'];
  knowledgeId: Scalars['ID']['output'];
  replyCount: Scalars['Int']['output'];
  replyToCommentId?: Maybe<Scalars['ID']['output']>;
  replyToUserName?: Maybe<Scalars['String']['output']>;
  rootCommentId?: Maybe<Scalars['ID']['output']>;
  updatedAt?: Maybe<Scalars['String']['output']>;
  userAvatarUrl?: Maybe<Scalars['String']['output']>;
  userId: Scalars['ID']['output'];
  userName: Scalars['String']['output'];
};

export type CommentCursorPaging = {
  __typename?: 'CommentCursorPaging';
  hasMore: Scalars['Boolean']['output'];
  items: Array<Comment>;
  nextCursor?: Maybe<Scalars['String']['output']>;
};

export type CommentQueryInput = {
  cursor?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
};

export type CreateLessonInput = {
  abstractText?: InputMaybe<Scalars['String']['input']>;
  allowComment?: Scalars['Boolean']['input'];
  categoryId?: InputMaybe<Scalars['ID']['input']>;
  contentMarkdown?: InputMaybe<Scalars['String']['input']>;
  estimateTimeInMinutes?: InputMaybe<Scalars['Int']['input']>;
  isPreview?: Scalars['Boolean']['input'];
  isPublic?: Scalars['Boolean']['input'];
  level?: InputMaybe<LessonLevel>;
  thumbnailUrl?: InputMaybe<Scalars['String']['input']>;
  title: Scalars['String']['input'];
};

export type CreateQuestionInput = {
  abstractText?: InputMaybe<Scalars['String']['input']>;
  allowComment?: Scalars['Boolean']['input'];
  categoryId?: InputMaybe<Scalars['ID']['input']>;
  content?: InputMaybe<Scalars['String']['input']>;
  isPublic?: Scalars['Boolean']['input'];
  thumbnailUrl?: InputMaybe<Scalars['String']['input']>;
  title: Scalars['String']['input'];
};

export type CursorPaginateRequest = {
  beforeId?: InputMaybe<Scalars['ID']['input']>;
  limit: Scalars['Int']['input'];
};

export type CursorPagingResponse = {
  __typename?: 'CursorPagingResponse';
  beforeId?: Maybe<Scalars['ID']['output']>;
  hasMore: Scalars['Boolean']['output'];
  items: Array<ChatMessage>;
};

export type EnumOption = {
  __typename?: 'EnumOption';
  code: Scalars['String']['output'];
  displayName: Scalars['String']['output'];
};

export type FeedItem = {
  __typename?: 'FeedItem';
  abstractText?: Maybe<Scalars['String']['output']>;
  allowComment: Scalars['Boolean']['output'];
  categoryId?: Maybe<Scalars['ID']['output']>;
  categoryName?: Maybe<Scalars['String']['output']>;
  commentCount: Scalars['Int']['output'];
  isPublic: Scalars['Boolean']['output'];
  knowledgeId: Scalars['ID']['output'];
  ownerAvatarUrl?: Maybe<Scalars['String']['output']>;
  ownerId: Scalars['ID']['output'];
  ownerName: Scalars['String']['output'];
  sourceCreatedAt: Scalars['String']['output'];
  thumbnailUrl?: Maybe<Scalars['String']['output']>;
  title: Scalars['String']['output'];
  type: KnowledgeType;
  typeMeta?: Maybe<FeedTypeMeta>;
  viewsCount: Scalars['Int']['output'];
  voteScore: Scalars['Int']['output'];
};

export type FeedPage = {
  __typename?: 'FeedPage';
  hasMore: Scalars['Boolean']['output'];
  items: Array<FeedItem>;
  nextCursor?: Maybe<Scalars['String']['output']>;
};

export type FeedQueryInput = {
  cursor?: InputMaybe<Scalars['String']['input']>;
  limit?: InputMaybe<Scalars['Int']['input']>;
};

export type FeedSearchInput = {
  categoryId?: InputMaybe<Scalars['ID']['input']>;
  keyword?: InputMaybe<Scalars['String']['input']>;
  level?: InputMaybe<LessonLevel>;
  page?: InputMaybe<Scalars['Int']['input']>;
  size?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Scalars['String']['input']>;
  type?: InputMaybe<KnowledgeType>;
};

export type FeedSearchResult = {
  __typename?: 'FeedSearchResult';
  items: Array<FeedItem>;
  page: Scalars['Int']['output'];
  totalCount: Scalars['Int']['output'];
  totalPages: Scalars['Int']['output'];
};

export type FeedTypeMeta = LessonFeedMeta | QuestionFeedMeta;

export type Knowledge = {
  abstractText?: Maybe<Scalars['String']['output']>;
  allowComment: Scalars['Boolean']['output'];
  category?: Maybe<Category>;
  commentCount: Scalars['Int']['output'];
  comments: CommentCursorPaging;
  createdAt?: Maybe<Scalars['String']['output']>;
  currentUserVote?: Maybe<Scalars['Int']['output']>;
  id: Scalars['ID']['output'];
  isPublic: Scalars['Boolean']['output'];
  owner: UserSimple;
  thumbnailUrl?: Maybe<Scalars['String']['output']>;
  title: Scalars['String']['output'];
  type: KnowledgeType;
  viewsCount: Scalars['Int']['output'];
  voteScore: Scalars['Int']['output'];
};


export type KnowledgeCommentsArgs = {
  input?: InputMaybe<CommentQueryInput>;
};

export type KnowledgePagePayload = {
  __typename?: 'KnowledgePagePayload';
  content: Array<Knowledge>;
  number: Scalars['Int']['output'];
  size: Scalars['Int']['output'];
  totalElements: Scalars['Int']['output'];
  totalPages: Scalars['Int']['output'];
};

export type KnowledgeType =
  | 'LESSON'
  | 'QUESTION';

export type Lesson = Knowledge & {
  __typename?: 'Lesson';
  abstractText?: Maybe<Scalars['String']['output']>;
  allowComment: Scalars['Boolean']['output'];
  category?: Maybe<Category>;
  commentCount: Scalars['Int']['output'];
  comments: CommentCursorPaging;
  contentMarkdown?: Maybe<Scalars['String']['output']>;
  createdAt?: Maybe<Scalars['String']['output']>;
  currentUserVote?: Maybe<Scalars['Int']['output']>;
  estimateTimeInMinutes?: Maybe<Scalars['Int']['output']>;
  id: Scalars['ID']['output'];
  isPublic: Scalars['Boolean']['output'];
  level?: Maybe<LessonLevel>;
  owner: UserSimple;
  thumbnailUrl?: Maybe<Scalars['String']['output']>;
  title: Scalars['String']['output'];
  type: KnowledgeType;
  viewsCount: Scalars['Int']['output'];
  voteScore: Scalars['Int']['output'];
};


export type LessonCommentsArgs = {
  input?: InputMaybe<CommentQueryInput>;
};

export type LessonFeedMeta = {
  __typename?: 'LessonFeedMeta';
  contentMarkdown?: Maybe<Scalars['String']['output']>;
  estimateTimeInMinutes?: Maybe<Scalars['Int']['output']>;
  level?: Maybe<LessonLevel>;
};

export type LessonLevel =
  | 'ADVANCED'
  | 'BEGINNER'
  | 'INTERMEDIATE';

export type Mutation = {
  __typename?: 'Mutation';
  _empty?: Maybe<Scalars['String']['output']>;
  createLesson: Lesson;
  createQuestion: Question;
  deleteKnowledge: Scalars['Boolean']['output'];
  deleteMessage: Scalars['Boolean']['output'];
  markRead: Scalars['Boolean']['output'];
  sendMessage: ChatMessage;
  updateLesson: Lesson;
  updateQuestion: Question;
};


export type MutationCreateLessonArgs = {
  input: CreateLessonInput;
};


export type MutationCreateQuestionArgs = {
  input: CreateQuestionInput;
};


export type MutationDeleteKnowledgeArgs = {
  id: Scalars['ID']['input'];
};


export type MutationDeleteMessageArgs = {
  messageId: Scalars['ID']['input'];
  roomId: Scalars['ID']['input'];
};


export type MutationMarkReadArgs = {
  messageId: Scalars['ID']['input'];
  roomId: Scalars['ID']['input'];
};


export type MutationSendMessageArgs = {
  request: SendMessageInput;
  roomId: Scalars['ID']['input'];
};


export type MutationUpdateLessonArgs = {
  input: UpdateLessonInput;
};


export type MutationUpdateQuestionArgs = {
  input: UpdateQuestionInput;
};

export type MyKnowledgeFilterInput = {
  number?: InputMaybe<Scalars['Int']['input']>;
  size?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Scalars['String']['input']>;
};

export type Query = {
  __typename?: 'Query';
  _empty?: Maybe<Scalars['String']['output']>;
  categories: Array<Category>;
  getFeed: FeedPage;
  knowledge?: Maybe<Knowledge>;
  knowledgeListByUsername: KnowledgePagePayload;
  lessonLevels: Array<EnumOption>;
  listCommentReplies: Array<Comment>;
  listRootComments: CommentCursorPaging;
  messages: CursorPagingResponse;
  myKnowledgeList: KnowledgePagePayload;
  rooms: Array<ChatRoom>;
  searchFeed: FeedSearchResult;
};


export type QueryGetFeedArgs = {
  input?: InputMaybe<FeedQueryInput>;
};


export type QueryKnowledgeArgs = {
  id: Scalars['ID']['input'];
};


export type QueryKnowledgeListByUsernameArgs = {
  input?: InputMaybe<MyKnowledgeFilterInput>;
  username: Scalars['String']['input'];
};


export type QueryListCommentRepliesArgs = {
  knowledgeId: Scalars['ID']['input'];
  rootCommentId: Scalars['ID']['input'];
};


export type QueryListRootCommentsArgs = {
  input?: InputMaybe<CommentQueryInput>;
  knowledgeId: Scalars['ID']['input'];
};


export type QueryMessagesArgs = {
  request: CursorPaginateRequest;
  roomId: Scalars['ID']['input'];
};


export type QueryMyKnowledgeListArgs = {
  input?: InputMaybe<MyKnowledgeFilterInput>;
};


export type QuerySearchFeedArgs = {
  input: FeedSearchInput;
};

export type Question = Knowledge & {
  __typename?: 'Question';
  abstractText?: Maybe<Scalars['String']['output']>;
  acceptedAnswerId?: Maybe<Scalars['ID']['output']>;
  allowComment: Scalars['Boolean']['output'];
  category?: Maybe<Category>;
  commentCount: Scalars['Int']['output'];
  comments: CommentCursorPaging;
  content?: Maybe<Scalars['String']['output']>;
  createdAt?: Maybe<Scalars['String']['output']>;
  currentUserVote?: Maybe<Scalars['Int']['output']>;
  id: Scalars['ID']['output'];
  isPublic: Scalars['Boolean']['output'];
  isResolved: Scalars['Boolean']['output'];
  owner: UserSimple;
  thumbnailUrl?: Maybe<Scalars['String']['output']>;
  title: Scalars['String']['output'];
  type: KnowledgeType;
  viewsCount: Scalars['Int']['output'];
  voteScore: Scalars['Int']['output'];
};


export type QuestionCommentsArgs = {
  input?: InputMaybe<CommentQueryInput>;
};

export type QuestionFeedMeta = {
  __typename?: 'QuestionFeedMeta';
  acceptedAnswerId?: Maybe<Scalars['ID']['output']>;
  content?: Maybe<Scalars['String']['output']>;
  isResolved?: Maybe<Scalars['Boolean']['output']>;
};

export type SendMessageInput = {
  content: Scalars['String']['input'];
  replyToMessageId?: InputMaybe<Scalars['ID']['input']>;
};

export type UpdateLessonInput = {
  abstractText?: InputMaybe<Scalars['String']['input']>;
  allowComment?: InputMaybe<Scalars['Boolean']['input']>;
  categoryId?: InputMaybe<Scalars['ID']['input']>;
  contentMarkdown?: InputMaybe<Scalars['String']['input']>;
  estimateTimeInMinutes?: InputMaybe<Scalars['Int']['input']>;
  id: Scalars['ID']['input'];
  isPublic?: InputMaybe<Scalars['Boolean']['input']>;
  level?: InputMaybe<LessonLevel>;
  thumbnailUrl?: InputMaybe<Scalars['String']['input']>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type UpdateQuestionInput = {
  abstractText?: InputMaybe<Scalars['String']['input']>;
  allowComment?: InputMaybe<Scalars['Boolean']['input']>;
  categoryId?: InputMaybe<Scalars['ID']['input']>;
  content?: InputMaybe<Scalars['String']['input']>;
  id: Scalars['ID']['input'];
  isPublic?: InputMaybe<Scalars['Boolean']['input']>;
  isResolved?: InputMaybe<Scalars['Boolean']['input']>;
  thumbnailUrl?: InputMaybe<Scalars['String']['input']>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type UserSimple = {
  __typename?: 'UserSimple';
  avatarUrl?: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  username?: Maybe<Scalars['String']['output']>;
};

export type CommentQueryInput = {
  cursor?: string | null | undefined;
  limit?: number | null | undefined;
};

export type CreateLessonInput = {
  abstractText?: string | null | undefined;
  allowComment?: boolean;
  categoryId?: string | number | null | undefined;
  contentMarkdown?: string | null | undefined;
  estimateTimeInMinutes?: number | null | undefined;
  isPreview?: boolean;
  isPublic?: boolean;
  level?: LessonLevel | null | undefined;
  thumbnailUrl?: string | null | undefined;
  title: string;
};

export type CreateQuestionInput = {
  abstractText?: string | null | undefined;
  allowComment?: boolean;
  categoryId?: string | number | null | undefined;
  content?: string | null | undefined;
  isPublic?: boolean;
  thumbnailUrl?: string | null | undefined;
  title: string;
};

export type CursorPaginateRequest = {
  beforeId?: string | number | null | undefined;
  limit: number;
};

export type FeedQueryInput = {
  cursor?: string | null | undefined;
  limit?: number | null | undefined;
};

export type FeedSearchInput = {
  categoryId?: string | number | null | undefined;
  keyword?: string | null | undefined;
  level?: LessonLevel | null | undefined;
  page?: number | null | undefined;
  size?: number | null | undefined;
  sort?: string | null | undefined;
  type?: KnowledgeType | null | undefined;
};

export type KnowledgeType =
  | 'LESSON'
  | 'QUESTION';

export type LessonLevel =
  | 'ADVANCED'
  | 'BEGINNER'
  | 'INTERMEDIATE';

export type MyKnowledgeFilterInput = {
  number?: number | null | undefined;
  size?: number | null | undefined;
  sort?: string | null | undefined;
};

export type UpdateLessonInput = {
  abstractText?: string | null | undefined;
  allowComment?: boolean | null | undefined;
  categoryId?: string | number | null | undefined;
  contentMarkdown?: string | null | undefined;
  estimateTimeInMinutes?: number | null | undefined;
  id: string | number;
  isPublic?: boolean | null | undefined;
  level?: LessonLevel | null | undefined;
  thumbnailUrl?: string | null | undefined;
  title?: string | null | undefined;
};

export type UpdateQuestionInput = {
  abstractText?: string | null | undefined;
  allowComment?: boolean | null | undefined;
  categoryId?: string | number | null | undefined;
  content?: string | null | undefined;
  id: string | number;
  isPublic?: boolean | null | undefined;
  isResolved?: boolean | null | undefined;
  thumbnailUrl?: string | null | undefined;
  title?: string | null | undefined;
};

export type RoomsQueryVariables = Exact<{ [key: string]: never; }>;


export type RoomsQuery = { rooms: Array<{ id: string, name: string, description: string | null, unreadCount: number }> };

export type MessagesQueryVariables = Exact<{
  roomId: string | number;
  request: CursorPaginateRequest;
}>;


export type MessagesQuery = { messages: { beforeId: string | null, hasMore: boolean, items: Array<{ id: string, roomId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, replyToMessageId: string | null, replyToUserName: string | null, replyToContentPreview: string | null, createdAt: string, clientTempId: string }> } };

export type GetFeedQueryVariables = Exact<{
  input?: FeedQueryInput | null | undefined;
}>;


export type GetFeedQuery = { getFeed: { nextCursor: string | null, hasMore: boolean, items: Array<{ knowledgeId: string, type: KnowledgeType, ownerId: string, ownerName: string, ownerAvatarUrl: string | null, title: string, abstractText: string | null, thumbnailUrl: string | null, categoryId: string | null, categoryName: string | null, viewsCount: number, voteScore: number, commentCount: number, sourceCreatedAt: string, typeMeta:
        | { __typename: 'LessonFeedMeta', level: LessonLevel | null, estimateTimeInMinutes: number | null, contentMarkdown: string | null }
        | { __typename: 'QuestionFeedMeta', isResolved: boolean | null, acceptedAnswerId: string | null, content: string | null }
       | null }> } };

export type SearchFeedQueryVariables = Exact<{
  input: FeedSearchInput;
}>;


export type SearchFeedQuery = { searchFeed: { totalCount: number, totalPages: number, page: number, items: Array<{ knowledgeId: string, type: KnowledgeType, ownerId: string, ownerName: string, ownerAvatarUrl: string | null, title: string, abstractText: string | null, thumbnailUrl: string | null, categoryId: string | null, categoryName: string | null, viewsCount: number, voteScore: number, commentCount: number, sourceCreatedAt: string, typeMeta:
        | { __typename: 'LessonFeedMeta', level: LessonLevel | null, estimateTimeInMinutes: number | null, contentMarkdown: string | null }
        | { __typename: 'QuestionFeedMeta', isResolved: boolean | null, acceptedAnswerId: string | null, content: string | null }
       | null }> } };

export type GetKnowledgeDetailQueryVariables = Exact<{
  id: string | number;
  commentInput?: CommentQueryInput | null | undefined;
}>;


export type GetKnowledgeDetailQuery = { knowledge:
    | { __typename: 'Lesson', contentMarkdown: string | null, estimateTimeInMinutes: number | null, level: LessonLevel | null, id: string, title: string, type: KnowledgeType, abstractText: string | null, thumbnailUrl: string | null, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null, currentUserVote: number | null, category: { id: string, name: string } | null, owner: { id: string, username: string | null, avatarUrl: string | null }, comments: { hasMore: boolean, nextCursor: string | null, items: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, rootCommentId: string | null, replyToCommentId: string | null, replyToUserName: string | null, replyCount: number, createdAt: string, updatedAt: string | null }> } }
    | { __typename: 'Question', content: string | null, isResolved: boolean, acceptedAnswerId: string | null, id: string, title: string, type: KnowledgeType, abstractText: string | null, thumbnailUrl: string | null, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null, currentUserVote: number | null, category: { id: string, name: string } | null, owner: { id: string, username: string | null, avatarUrl: string | null }, comments: { hasMore: boolean, nextCursor: string | null, items: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, rootCommentId: string | null, replyToCommentId: string | null, replyToUserName: string | null, replyCount: number, createdAt: string, updatedAt: string | null }> } }
   | null };

export type ListRootCommentsQueryVariables = Exact<{
  knowledgeId: string | number;
  input?: CommentQueryInput | null | undefined;
}>;


export type ListRootCommentsQuery = { listRootComments: { hasMore: boolean, nextCursor: string | null, items: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, replyCount: number, createdAt: string }> } };

export type ListCommentRepliesQueryVariables = Exact<{
  knowledgeId: string | number;
  rootCommentId: string | number;
}>;


export type ListCommentRepliesQuery = { listCommentReplies: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, rootCommentId: string | null, replyToCommentId: string | null, replyToUserName: string | null, replyCount: number, createdAt: string, updatedAt: string | null, deletedAt: string | null, deletedBy: string | null }> };

export type CreateLessonMutationVariables = Exact<{
  input: CreateLessonInput;
}>;


export type CreateLessonMutation = { createLesson: { id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, contentMarkdown: string | null, level: LessonLevel | null, estimateTimeInMinutes: number | null, createdAt: string | null, viewsCount: number, voteScore: number, commentCount: number, currentUserVote: number | null, owner: { id: string }, category: { id: string, name: string } | null } };

export type CreateQuestionMutationVariables = Exact<{
  input: CreateQuestionInput;
}>;


export type CreateQuestionMutation = { createQuestion: { id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, content: string | null, isResolved: boolean, acceptedAnswerId: string | null, createdAt: string | null, viewsCount: number, voteScore: number, commentCount: number, currentUserVote: number | null, owner: { id: string }, category: { id: string, name: string } | null } };

export type GetMyKnowledgeListQueryVariables = Exact<{
  input?: MyKnowledgeFilterInput | null | undefined;
}>;


export type GetMyKnowledgeListQuery = { myKnowledgeList: { totalElements: number, totalPages: number, number: number, size: number, content: Array<
      | { level: LessonLevel | null, estimateTimeInMinutes: number | null, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null }
      | { isResolved: boolean, content: string | null, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null }
    > } };

export type GetKnowledgeQueryVariables = Exact<{
  id: string | number;
}>;


export type GetKnowledgeQuery = { knowledge:
    | { contentMarkdown: string | null, level: LessonLevel | null, estimateTimeInMinutes: number | null, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, category: { id: string } | null }
    | { content: string | null, isResolved: boolean, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, category: { id: string } | null }
   | null };

export type UpdateQuestionMutationVariables = Exact<{
  input: UpdateQuestionInput;
}>;


export type UpdateQuestionMutation = { updateQuestion: { id: string, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, content: string | null, isResolved: boolean, category: { id: string } | null } };

export type UpdateLessonMutationVariables = Exact<{
  input: UpdateLessonInput;
}>;


export type UpdateLessonMutation = { updateLesson: { id: string, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, allowComment: boolean, contentMarkdown: string | null, level: LessonLevel | null, estimateTimeInMinutes: number | null, category: { id: string } | null } };

export type KnowledgeListByUsernameQueryVariables = Exact<{
  username: string;
  input?: MyKnowledgeFilterInput | null | undefined;
}>;


export type KnowledgeListByUsernameQuery = { knowledgeListByUsername: { totalElements: number, totalPages: number, number: number, size: number, content: Array<
      | { level: LessonLevel | null, estimateTimeInMinutes: number | null, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null }
      | { isResolved: boolean, content: string | null, id: string, type: KnowledgeType, title: string, abstractText: string | null, thumbnailUrl: string | null, isPublic: boolean, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null }
    > } };

export type GetReferenceDataQueryVariables = Exact<{ [key: string]: never; }>;


export type GetReferenceDataQuery = { lessonLevels: Array<{ code: string, displayName: string }>, categories: Array<{ id: string, name: string }> };
