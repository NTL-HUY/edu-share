#!/bin/bash
# Script tạo cấu trúc thư mục minh họa cho SvelteKit + TypeScript
# Chạy: bash init-structure.sh (đặt file này ở thư mục gốc project, sau khi đã "npx sv create")

set -e

# ===== lib/components =====
mkdir -p src/lib/components/candidate
mkdir -p src/lib/components/common
mkdir -p src/lib/components/dashboard
mkdir -p src/lib/components/jobs
mkdir -p src/lib/components/notifications
mkdir -p src/lib/components/statistics

touch src/lib/components/candidate/EducationCard.svelte
touch src/lib/components/candidate/ExperienceCard.svelte
touch src/lib/components/common/Header.svelte
touch src/lib/components/common/Footer.svelte
touch src/lib/components/common/Toast.svelte
touch src/lib/components/dashboard/Sidebar.svelte
touch src/lib/components/jobs/JobCard.svelte
touch src/lib/components/notifications/NotificationBell.svelte
touch src/lib/components/statistics/MetricCard.svelte

# ===== lib/configs =====
mkdir -p src/lib/configs
touch src/lib/configs/api.ts

# ===== lib/constants =====
mkdir -p src/lib/constants
touch src/lib/constants/auth.ts
touch src/lib/constants/navItems.ts

# ===== lib/stores (thay contexts/) =====
mkdir -p src/lib/stores
touch src/lib/stores/authStore.ts
touch src/lib/stores/toastStore.ts
touch src/lib/stores/jobCompareStore.ts
touch src/lib/stores/websocketStore.ts

# ===== lib/hooks (giữ tên quen thuộc, nội dung là hàm .ts thường) =====
mkdir -p src/lib/hooks/auth
mkdir -p src/lib/hooks/candidate
mkdir -p src/lib/hooks/jobs
mkdir -p src/lib/hooks/conversation

touch src/lib/hooks/auth/useLoginForm.ts
touch src/lib/hooks/candidate/useApplyJob.ts
touch src/lib/hooks/jobs/useSearchJob.ts
touch src/lib/hooks/conversation/useConversation.ts

# ===== lib/services =====
mkdir -p src/lib/services
touch src/lib/services/auth.service.ts
touch src/lib/services/job.service.ts
touch src/lib/services/graphql.client.ts

# ===== lib/utils =====
mkdir -p src/lib/utils
touch src/lib/utils/formatString.ts
touch src/lib/utils/parseApiError.ts

# ===== lib/types (mới, vì dùng TS) =====
mkdir -p src/lib/types
touch src/lib/types/job.ts
touch src/lib/types/user.ts

# ===== routes =====
mkdir -p "src/routes/(candidate)/profile"
mkdir -p "src/routes/(candidate)/applications"
mkdir -p "src/routes/(employer)/dashboard"
mkdir -p "src/routes/(employer)/job-management"
mkdir -p "src/routes/jobs/[id]"
mkdir -p src/routes/login
mkdir -p src/routes/register
mkdir -p src/routes/chat

touch src/routes/+layout.svelte
touch src/routes/+page.svelte
touch "src/routes/(candidate)/+layout.svelte"
touch "src/routes/(candidate)/profile/+page.svelte"
touch "src/routes/(candidate)/applications/+page.svelte"
touch "src/routes/(employer)/+layout.svelte"
touch "src/routes/(employer)/dashboard/+page.svelte"
touch "src/routes/(employer)/job-management/+page.svelte"
touch src/routes/jobs/+page.svelte
touch "src/routes/jobs/[id]/+page.svelte"
touch src/routes/login/+page.svelte
touch src/routes/register/+page.svelte
touch src/routes/chat/+page.svelte
touch src/routes/+error.svelte

# ===== root files =====
touch src/hooks.server.ts
touch src/app.css
touch src/app.d.ts

echo "Đã tạo xong cấu trúc thư mục minh họa."
