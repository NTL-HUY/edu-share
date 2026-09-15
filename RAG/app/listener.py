import asyncio
import json
import os
from aiokafka import AIOKafkaConsumer
from pydantic import TypeAdapter, ValidationError

from app.config import config
from app.dto import (
    UpdatedEvent,
    CreatedEvent,
    KnowledgeDeletedEvent,
)
from app.rag import (
    handle_knowledge_create_event,
    handle_knowledge_update_event,
    handle_knowledge_delete_event,
)

KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "localhost:9092")

consumer_task = None
created_adapter = TypeAdapter(CreatedEvent)
updated_adapter = TypeAdapter(UpdatedEvent)

async def consume_knowledge_events():
    consumer = AIOKafkaConsumer(
        config.KNOWLEDGE_CREATED_TOPIC,
        config.KNOWLEDGE_UPDATED_TOPIC,
        config.KNOWLEDGE_DELETED_TOPIC,
        bootstrap_servers=config.KAFKA_BOOTSTRAP,
        group_id="embedding-service",
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        auto_offset_reset="earliest",
    )
    await consumer.start()
    try:
        async for msg in consumer:
            payload = msg.value  # payload đã decode thành dict JSON

            if msg.topic == config.KNOWLEDGE_CREATED_TOPIC:
                try:
                    event = created_adapter.validate_python(payload)
                except ValidationError as e:
                    print("Lỗi schema CreatedEvent:", e)
                    continue
                await handle_knowledge_create_event(event)

            elif msg.topic == config.KNOWLEDGE_UPDATED_TOPIC:
                try:
                    event = updated_adapter.validate_python(payload)
                except ValidationError as e:
                    print("Lỗi schema UpdatedEvent:", e)
                    continue
                await handle_knowledge_update_event(event)

            elif msg.topic == config.KNOWLEDGE_DELETED_TOPIC:
                try:
                    event = KnowledgeDeletedEvent.model_validate(payload)
                except ValidationError as e:
                    print("Lỗi schema DeletedEvent:", e)
                    continue
                await handle_knowledge_delete_event(event)
    finally:
        await consumer.stop()


def start_consumer_task():
    global consumer_task
    consumer_task = asyncio.create_task(consume_knowledge_events())


def stop_consumer_task():
    global consumer_task
    if consumer_task:
        consumer_task.cancel()