import argparse
import asyncio
import os
import sys
from pathlib import Path
from typing import List, Optional

sys.stdout.reconfigure(encoding="utf-8")

from dotenv import load_dotenv

load_dotenv()

from app import db
from app.chunking import chunk_lesson, chunk_question
from app.embedding import aembed_texts

SEED_FILES = ["seed_user.sql", "seed_category.sql", "seed_knowledge_feed.sql"]

DEFAULT_SEED_DIR = Path(
    r"D:\Workspace\Coding\SpringBoot\edu-share\edushare-backend\src\main\resources\db"
)


async def run_sql_file(conn, path: Path) -> None:
    sql = path.read_text(encoding="utf-8")
    await conn.execute(sql)
    print(f"  [ok] {path.name}")


async def seed_database(seed_dir: Path, force: bool = False) -> None:
    pool = await db.init_pool()
    async with pool.acquire() as conn:
        knowledge_count = await conn.fetchval("SELECT COUNT(*) FROM knowledge")

        if knowledge_count and not force:
            print(f"[seed] knowledge đã có {knowledge_count} dòng, bỏ qua seed (dùng --force-seed để chạy lại)")
            return

        for fname in SEED_FILES:
            fpath = seed_dir / fname
            if not fpath.exists():
                print(f"  [skip] không tìm thấy {fpath}")
                continue
            print(f"[seed] đang chạy {fname} ...")
            await run_sql_file(conn, fpath)


async def load_knowledge() -> List[dict]:
    pool = await db.init_pool()
    rows = await pool.fetch(
        """
        SELECT
            k.id,
            k.type,
            k.title,
            k.abstract,
            k.owner_id,
            k.is_public,
            l.content_markdown,
            q.content      AS question_content,
            q.is_resolved,
            q.accepted_answer_id
        FROM knowledge k
        LEFT JOIN lesson l   ON l.knowledge_id = k.id
        LEFT JOIN question q ON q.knowledge_id = k.id
        WHERE k.deleted_at IS NULL
        ORDER BY k.id
        """
    )
    return [dict(r) for r in rows]


async def embed_knowledge(knowledge_rows: List[dict]) -> dict:
    processed = {"lessons": 0, "questions": 0, "skipped": 0}
    total_chunks = 0

    for row in knowledge_rows:
        k_id = row["id"]
        k_type = row["type"]

        content_markdown = (row["content_markdown"] or "").strip()
        is_resolved = row["is_resolved"]
        accepted_answer_id = row["accepted_answer_id"]

        if k_type == "LESSON":
            chunks = chunk_lesson(content_markdown)
            processed["lessons"] += 1
            label = f"LESSON   {k_id}"
        elif k_type == "QUESTION":
            if not is_resolved or accepted_answer_id is None:
                print(f"[embed] bỏ qua QUESTION {k_id} (chưa resolved hoặc chưa có câu trả lời chấp nhận)")
                processed["questions"] += 1
                processed["skipped"] += 1
                continue
            accepted_answer = await db.get_accepted_comment_content(k_id, accepted_answer_id)
            chunks = chunk_question(
                title=row["title"],
                abstract=row["abstract"] or "",
                content=row["question_content"] or "",
                accepted_answer=accepted_answer,
            )
            processed["questions"] += 1
            label = f"QUESTION  {k_id}"
        else:
            print(f"[embed] bỏ qua type không hỗ trợ: {k_type} (id={k_id})")
            processed["skipped"] += 1
            continue

        if not chunks:
            print(f"[embed] {label} không có nội dung để chunk, bỏ qua")
            processed["skipped"] += 1
            continue

        embeddings = await aembed_texts(chunks)
        await db.insert_chunks(
            knowledge_id=k_id,
            knowledge_type=k_type,
            owner_id=row["owner_id"],
            is_public=row["is_public"],
            title=row["title"],
            chunks=chunks,
            embeddings=embeddings,
        )
        total_chunks += len(chunks)
        print(f"[embed] {label} -> {len(chunks)} chunk")

    print(f"[embed] XONG: {processed} | tổng chunk = {total_chunks}")
    return processed


async def main() -> None:
    parser = argparse.ArgumentParser(description="Seed knowledge_feed + generate embeddings cho knowledge_chunk")
    parser.add_argument(
        "--seed-dir",
        type=Path,
        default=os.getenv("SEED_SQL_DIR", DEFAULT_SEED_DIR),
        help="Thư mục chứa seed_user.sql, seed_category.sql, seed_knowledge_feed.sql",
    )
    parser.add_argument(
        "--skip-seed",
        action="store_true",
        help="Không chạy seed SQL, chỉ embed dữ liệu đã có",
    )
    parser.add_argument(
        "--force-seed",
        action="store_true",
        help="Chạy lại seed SQL kể cả khi knowledge đã có dữ liệu",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Chỉ đọc và in số lượng, không gọi embedding/ghi DB",
    )
    args = parser.parse_args()

    if not args.skip_seed:
        await seed_database(args.seed_dir, force=args.force_seed)
    else:
        print("[seed] bỏ qua seed (--skip-seed)")

    rows = await load_knowledge()
    print(f"[embed] đọc được {len(rows)} knowledge từ DB")

    lessons = sum(1 for r in rows if r["type"] == "LESSON")
    questions = sum(1 for r in rows if r["type"] == "QUESTION")
    resolved = sum(
        1
        for r in rows
        if r["type"] == "QUESTION" and r["is_resolved"] and r["accepted_answer_id"] is not None
    )
    print(f"[embed] trong đó: {lessons} LESSON, {questions} QUESTION (resolved có đáp án: {resolved})")

    if args.dry_run:
        print("[dry-run] dừng tại đây, không embed")
        return

    await embed_knowledge(rows)


if __name__ == "__main__":
    asyncio.run(main())