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

export type CommentGetInput = {
  number?: InputMaybe<Scalars['Int']['input']>;
  size?: InputMaybe<Scalars['Int']['input']>;
  sort?: InputMaybe<Scalars['String']['input']>;
};

export type CommentPage = {
  __typename?: 'CommentPage';
  content: Array<Comment>;
  number: Scalars['Int']['output'];
  size: Scalars['Int']['output'];
  totalElements: Scalars['Int']['output'];
  /**  Đổi page -> number */
  totalPages: Scalars['Int']['output'];
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
  comments?: Maybe<CommentPage>;
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
  input?: InputMaybe<CommentGetInput>;
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
  comments?: Maybe<CommentPage>;
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
  input?: InputMaybe<CommentGetInput>;
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
  updateLesson: Lesson;
  updateQuestion: Question;
};


export type MutationCreateLessonArgs = {
  input: CreateLessonInput;
};


export type MutationCreateQuestionArgs = {
  input: CreateQuestionInput;
};


export type MutationUpdateLessonArgs = {
  input: UpdateLessonInput;
};


export type MutationUpdateQuestionArgs = {
  input: UpdateQuestionInput;
};

export type Query = {
  __typename?: 'Query';
  _empty?: Maybe<Scalars['String']['output']>;
  getFeed: FeedPage;
  knowledge?: Maybe<Knowledge>;
  searchFeed: FeedSearchResult;
};


export type QueryGetFeedArgs = {
  input?: InputMaybe<FeedQueryInput>;
};


export type QueryKnowledgeArgs = {
  id: Scalars['ID']['input'];
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
  comments?: Maybe<CommentPage>;
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
  input?: InputMaybe<CommentGetInput>;
};

export type QuestionFeedMeta = {
  __typename?: 'QuestionFeedMeta';
  acceptedAnswerId?: Maybe<Scalars['ID']['output']>;
  content?: Maybe<Scalars['String']['output']>;
  isResolved?: Maybe<Scalars['Boolean']['output']>;
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

export type CommentGetInput = {
  number?: number | null | undefined;
  size?: number | null | undefined;
  sort?: string | null | undefined;
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
  commentInput?: CommentGetInput | null | undefined;
}>;


export type GetKnowledgeDetailQuery = { knowledge:
    | { __typename: 'Lesson', contentMarkdown: string | null, estimateTimeInMinutes: number | null, level: LessonLevel | null, id: string, title: string, type: KnowledgeType, abstractText: string | null, thumbnailUrl: string | null, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null, currentUserVote: number | null, category: { id: string, name: string } | null, owner: { id: string, username: string | null, avatarUrl: string | null }, comments: { totalElements: number, totalPages: number, number: number, size: number, content: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, rootCommentId: string | null, replyToCommentId: string | null, replyToUserName: string | null, replyCount: number, createdAt: string, updatedAt: string | null }> } | null }
    | { __typename: 'Question', content: string | null, isResolved: boolean, acceptedAnswerId: string | null, id: string, title: string, type: KnowledgeType, abstractText: string | null, thumbnailUrl: string | null, viewsCount: number, voteScore: number, commentCount: number, createdAt: string | null, currentUserVote: number | null, category: { id: string, name: string } | null, owner: { id: string, username: string | null, avatarUrl: string | null }, comments: { totalElements: number, totalPages: number, number: number, size: number, content: Array<{ id: string, knowledgeId: string, userId: string, userName: string, userAvatarUrl: string | null, content: string, rootCommentId: string | null, replyToCommentId: string | null, replyToUserName: string | null, replyCount: number, createdAt: string, updatedAt: string | null }> } | null }
   | null };
