import * as Types from './types';
import { GraphQLClient, type RequestOptions } from 'graphql-request';
import gql from 'graphql-tag';
type GraphQLClientRequestHeaders = RequestOptions['requestHeaders'];

export const GetFeedDocument = gql`
    query GetFeed($input: FeedQueryInput) {
  getFeed(input: $input) {
    items {
      knowledgeId
      type
      ownerId
      ownerName
      ownerAvatarUrl
      title
      abstractText
      thumbnailUrl
      categoryId
      categoryName
      viewsCount
      voteScore
      commentCount
      sourceCreatedAt
      typeMeta {
        __typename
        ... on LessonFeedMeta {
          level
          estimateTimeInMinutes
          contentMarkdown
        }
        ... on QuestionFeedMeta {
          isResolved
          acceptedAnswerId
          content
        }
      }
    }
    nextCursor
    hasMore
  }
}
    `;
export const SearchFeedDocument = gql`
    query searchFeed($input: FeedSearchInput!) {
  searchFeed(input: $input) {
    items {
      knowledgeId
      type
      ownerId
      ownerName
      ownerAvatarUrl
      title
      abstractText
      thumbnailUrl
      categoryId
      categoryName
      viewsCount
      voteScore
      commentCount
      sourceCreatedAt
      typeMeta {
        __typename
        ... on LessonFeedMeta {
          level
          estimateTimeInMinutes
          contentMarkdown
        }
        ... on QuestionFeedMeta {
          isResolved
          acceptedAnswerId
          content
        }
      }
    }
    totalCount
    totalPages
    page
  }
}
    `;
export const GetKnowledgeDetailDocument = gql`
    query GetKnowledgeDetail($id: ID!, $commentInput: CommentQueryInput) {
  knowledge(id: $id) {
    __typename
    id
    title
    type
    abstractText
    thumbnailUrl
    category {
      id
      name
    }
    viewsCount
    voteScore
    commentCount
    createdAt
    currentUserVote
    owner {
      id
      username
      avatarUrl
    }
    ... on Lesson {
      contentMarkdown
      estimateTimeInMinutes
      level
    }
    ... on Question {
      content
      isResolved
      acceptedAnswerId
    }
    comments(input: $commentInput) {
      items {
        id
        knowledgeId
        userId
        userName
        userAvatarUrl
        content
        rootCommentId
        replyToCommentId
        replyToUserName
        replyCount
        createdAt
        updatedAt
      }
      hasMore
      nextCursor
    }
  }
}
    `;
export const ListRootCommentsDocument = gql`
    query ListRootComments($knowledgeId: ID!, $input: CommentQueryInput) {
  listRootComments(knowledgeId: $knowledgeId, input: $input) {
    hasMore
    nextCursor
    items {
      id
      knowledgeId
      userId
      userName
      userAvatarUrl
      content
      replyCount
      createdAt
    }
  }
}
    `;
export const ListCommentRepliesDocument = gql`
    query listCommentReplies($knowledgeId: ID!, $rootCommentId: ID!) {
  listCommentReplies(knowledgeId: $knowledgeId, rootCommentId: $rootCommentId) {
    id
    knowledgeId
    userId
    userName
    userAvatarUrl
    content
    rootCommentId
    replyToCommentId
    replyToUserName
    replyCount
    createdAt
    updatedAt
    deletedAt
    deletedBy
  }
}
    `;
export const CreateLessonDocument = gql`
    mutation CreateLesson($input: CreateLessonInput!) {
  createLesson(input: $input) {
    id
    type
    title
    abstractText
    thumbnailUrl
    isPublic
    allowComment
    contentMarkdown
    level
    estimateTimeInMinutes
    createdAt
    viewsCount
    voteScore
    commentCount
    currentUserVote
    owner {
      id
    }
    category {
      id
      name
    }
  }
}
    `;
export const CreateQuestionDocument = gql`
    mutation CreateQuestion($input: CreateQuestionInput!) {
  createQuestion(input: $input) {
    id
    type
    title
    abstractText
    thumbnailUrl
    isPublic
    allowComment
    content
    isResolved
    acceptedAnswerId
    createdAt
    viewsCount
    voteScore
    commentCount
    currentUserVote
    owner {
      id
    }
    category {
      id
      name
    }
  }
}
    `;
export const GetMyKnowledgeListDocument = gql`
    query GetMyKnowledgeList($input: MyKnowledgeFilterInput) {
  myKnowledgeList(input: $input) {
    totalElements
    totalPages
    number
    size
    content {
      id
      type
      title
      abstractText
      thumbnailUrl
      isPublic
      viewsCount
      voteScore
      commentCount
      createdAt
      ... on Lesson {
        level
        estimateTimeInMinutes
      }
      ... on Question {
        isResolved
        content
      }
    }
  }
}
    `;
export const GetKnowledgeDocument = gql`
    query GetKnowledge($id: ID!) {
  knowledge(id: $id) {
    id
    type
    title
    abstractText
    thumbnailUrl
    isPublic
    allowComment
    category {
      id
    }
    ... on Lesson {
      contentMarkdown
      level
      estimateTimeInMinutes
    }
    ... on Question {
      content
      isResolved
    }
  }
}
    `;
export const UpdateQuestionDocument = gql`
    mutation UpdateQuestion($input: UpdateQuestionInput!) {
  updateQuestion(input: $input) {
    id
    title
    abstractText
    thumbnailUrl
    isPublic
    allowComment
    content
    isResolved
    category {
      id
    }
  }
}
    `;
export const UpdateLessonDocument = gql`
    mutation UpdateLesson($input: UpdateLessonInput!) {
  updateLesson(input: $input) {
    id
    title
    abstractText
    thumbnailUrl
    isPublic
    allowComment
    contentMarkdown
    level
    estimateTimeInMinutes
    category {
      id
    }
  }
}
    `;
export const GetReferenceDataDocument = gql`
    query GetReferenceData {
  lessonLevels {
    code
    displayName
  }
  categories {
    id
    name
  }
}
    `;

export type SdkFunctionWrapper = <T>(action: (requestHeaders?:Record<string, string>) => Promise<T>, operationName: string, operationType?: string, variables?: any) => Promise<T>;


const defaultWrapper: SdkFunctionWrapper = (action, _operationName, _operationType, _variables) => action();

export function getSdk(client: GraphQLClient, withWrapper: SdkFunctionWrapper = defaultWrapper) {
  return {
    GetFeed(variables?: Types.GetFeedQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.GetFeedQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.GetFeedQuery>({ document: GetFeedDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'GetFeed', 'query', variables);
    },
    searchFeed(variables: Types.SearchFeedQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.SearchFeedQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.SearchFeedQuery>({ document: SearchFeedDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'searchFeed', 'query', variables);
    },
    GetKnowledgeDetail(variables: Types.GetKnowledgeDetailQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.GetKnowledgeDetailQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.GetKnowledgeDetailQuery>({ document: GetKnowledgeDetailDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'GetKnowledgeDetail', 'query', variables);
    },
    ListRootComments(variables: Types.ListRootCommentsQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.ListRootCommentsQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.ListRootCommentsQuery>({ document: ListRootCommentsDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'ListRootComments', 'query', variables);
    },
    listCommentReplies(variables: Types.ListCommentRepliesQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.ListCommentRepliesQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.ListCommentRepliesQuery>({ document: ListCommentRepliesDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'listCommentReplies', 'query', variables);
    },
    CreateLesson(variables: Types.CreateLessonMutationVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.CreateLessonMutation> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.CreateLessonMutation>({ document: CreateLessonDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'CreateLesson', 'mutation', variables);
    },
    CreateQuestion(variables: Types.CreateQuestionMutationVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.CreateQuestionMutation> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.CreateQuestionMutation>({ document: CreateQuestionDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'CreateQuestion', 'mutation', variables);
    },
    GetMyKnowledgeList(variables?: Types.GetMyKnowledgeListQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.GetMyKnowledgeListQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.GetMyKnowledgeListQuery>({ document: GetMyKnowledgeListDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'GetMyKnowledgeList', 'query', variables);
    },
    GetKnowledge(variables: Types.GetKnowledgeQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.GetKnowledgeQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.GetKnowledgeQuery>({ document: GetKnowledgeDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'GetKnowledge', 'query', variables);
    },
    UpdateQuestion(variables: Types.UpdateQuestionMutationVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.UpdateQuestionMutation> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.UpdateQuestionMutation>({ document: UpdateQuestionDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'UpdateQuestion', 'mutation', variables);
    },
    UpdateLesson(variables: Types.UpdateLessonMutationVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.UpdateLessonMutation> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.UpdateLessonMutation>({ document: UpdateLessonDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'UpdateLesson', 'mutation', variables);
    },
    GetReferenceData(variables?: Types.GetReferenceDataQueryVariables, requestHeaders?: GraphQLClientRequestHeaders, signal?: RequestInit['signal']): Promise<Types.GetReferenceDataQuery> {
      return withWrapper((wrappedRequestHeaders) => client.request<Types.GetReferenceDataQuery>({ document: GetReferenceDataDocument, variables, requestHeaders: { ...requestHeaders, ...wrappedRequestHeaders }, signal }), 'GetReferenceData', 'query', variables);
    }
  };
}
export type Sdk = ReturnType<typeof getSdk>;