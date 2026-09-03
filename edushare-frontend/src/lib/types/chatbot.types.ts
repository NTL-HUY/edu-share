export interface KnowledgeSource {
  knowledgeId: number;
  title: string;
  type: string;
  similarity: number;
}

export interface ModelResponse {
  model: string;
  answer: string;
  sources: KnowledgeSource[];
}