export interface VoteResult {
  knowledgeId: string;
  currentValue: number;
  voteScore: number;
}

export interface CreateCommentRequest {
  content: string;
  replyToCommentId: number | null;
}