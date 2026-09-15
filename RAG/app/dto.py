from datetime import datetime
from enum import Enum
from typing import Annotated, List, Literal, Optional, Union

from pydantic import BaseModel, BeforeValidator, Field


def parse_java_timestamp(value):
    if value is None or isinstance(value, (str, datetime)):
        return value
    y, mo, d, h, mi, s, *rest = value
    micro = (rest[0] // 1000) if rest else 0
    return datetime(y, mo, d, h, mi, s, micro)


JavaDateTime = Annotated[datetime, BeforeValidator(parse_java_timestamp)]


class EmbedRequest(BaseModel):
    texts: List[str]
    normalize: bool = True

class EmbedResponse(BaseModel):
    model: str
    dim: int
    embeddings: List[List[float]]

class ChatRequest(BaseModel):
    query: str
    userId: Optional[int] = None
    visibleOwnerIds: Optional[List[int]] = None
    model: Optional[str] = None
    stream: bool = False

class ChatSource(BaseModel):
    knowledgeId: int
    title: str
    type: str
    similarity: float

class ChatResponse(BaseModel):
    model: str
    answer: str
    sources: List[ChatSource]
    prompt: str


class KnowledgeType(str, Enum):
    LESSON = "LESSON"
    QUESTION = "QUESTION"


class LessonLevel(str, Enum):
    BEGINNER = "BEGINNER"
    INTERMEDIATE = "INTERMEDIATE"
    ADVANCED = "ADVANCED"


class KnowledgeCreatedEvent(BaseModel):
    eventType: Literal["LESSON_CREATED", "QUESTION_CREATED"]
    knowledgeId: int
    type: KnowledgeType
    ownerId: int
    ownerName: str
    ownerAvatarUrl: Optional[str] = None
    isFamous: bool
    title: str
    abstractText: Optional[str] = None
    thumbnailUrl: Optional[str] = None
    isPublic: bool
    categoryId: Optional[int] = None
    categoryName: Optional[str] = None
    createdAt: JavaDateTime


class LessonCreatedEvent(KnowledgeCreatedEvent):
    eventType: Literal["LESSON_CREATED"]
    level: LessonLevel
    estimateTimeInMinutes: Optional[int] = None
    contentMarkdown: str


class QuestionCreatedEvent(KnowledgeCreatedEvent):
    eventType: Literal["QUESTION_CREATED"]
    isResolved: bool
    acceptedAnswerId: Optional[int] = None
    content: str


CreatedEvent = Annotated[
    Union[LessonCreatedEvent, QuestionCreatedEvent],
    Field(discriminator="eventType"),
]


class KnowledgeUpdatedEvent(BaseModel):
    eventType: Literal["LESSON_UPDATED", "QUESTION_UPDATED"]
    knowledgeId: int
    title: str
    abstractText: Optional[str] = None
    thumbnailUrl: Optional[str] = None
    allowComment: bool
    isPublic: bool
    updatedAt: JavaDateTime
    categoryId: Optional[int] = None
    categoryName: Optional[str] = None
    type: KnowledgeType
    ownerId: int


class LessonUpdatedEvent(KnowledgeUpdatedEvent):
    eventType: Literal["LESSON_UPDATED"]
    contentMarkdown: str
    level: LessonLevel
    estimateTimeInMinutes: Optional[int] = None


class QuestionUpdatedEvent(KnowledgeUpdatedEvent):
    eventType: Literal["QUESTION_UPDATED"]
    isResolved: bool
    acceptedAnswerId: Optional[int] = None
    content: str


UpdatedEvent = Annotated[
    Union[LessonUpdatedEvent, QuestionUpdatedEvent],
    Field(discriminator="eventType"),
]


class KnowledgeDeletedEvent(BaseModel):
    knowledgeId: int
    type: KnowledgeType
    ownerId: int
    deletedBy: int
