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
    query GetKnowledgeDetail($id: ID!, $commentInput: CommentGetInput) {
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
      content {
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
      totalElements
      totalPages
      number
      size
    }
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
    }
  };
}
export type Sdk = ReturnType<typeof getSdk>;