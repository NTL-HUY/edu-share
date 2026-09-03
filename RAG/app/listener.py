import asyncio
import json
import os
from aiokafka import AIOKafkaConsumer

from app.rag import handle_knowledge_event

KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "localhost:9092")
TOPIC = "knowledge-created"


consumer_task = None

async def consume_knowledge_events():
    consumer = AIOKafkaConsumer(
        TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP,
        group_id="embedding-service",
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        auto_offset_reset="earliest",
    )
    await consumer.start()
    try:
        async for msg in consumer:
            event = msg.value
            print(f"Nhận event: {event}")
            await handle_knowledge_event(event)
    finally:
        await consumer.stop()


def start_consumer_task():
    global consumer_task
    consumer_task = asyncio.create_task(consume_knowledge_events())


def stop_consumer_task():
    global consumer_task
    if consumer_task:
        consumer_task.cancel()