<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Document, Notebook, Calendar, Reading, EditPen, Memo, Postcard } from "@element-plus/icons-vue";
import { ElIcon } from "element-plus";
import PanelCard from "@/components/PanelCard.vue";
import EChart from "@/components/charts/EChart.vue";
import request from "@/api/request";

const router = useRouter();

const stats = ref([
  { title: "题目总数", value: "0", icon: Document, gradient: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" },
  { title: "试卷总数", value: "0", icon: Notebook, gradient: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
  { title: "考试总数", value: "0", icon: Calendar, gradient: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" },
  { title: "进行中考试", value: "0", icon: Reading, gradient: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)" }
]);

const quickActions = [
  { title: "创建题目", desc: "添加单选/多选/判断等题型", icon: EditPen, route: "/teacher/questions", color: "#667eea" },
  { title: "手动组卷", desc: "从题库选择题目组卷", icon: Memo, route: "/teacher/paper-manual", color: "#f5576c" },
  { title: "自动组卷", desc: "按规则随机生成试卷", icon: Notebook, route: "/teacher/paper-auto", color: "#4facfe" },
  { title: "创建考试", desc: "发布考试给学生", icon: Postcard, route: "/teacher/exams", color: "#43e97b" }
];

const recentExams = ref([]);

onMounted(async () => {
  try {
    const [questionsRes, papersRes, examsRes, activeExamsRes] = await Promise.all([
      request.get("/teacher/questions", { params: { pageSize: 1 } }),
      request.get("/teacher/papers", { params: { pageSize: 1 } }),
      request.get("/teacher/exams", { params: { pageSize: 5 } }),
      request.get("/teacher/exams", { params: { publishStatus: 1, pageSize: 1 } })
    ]);

    stats.value[0].value = String(questionsRes.data?.total || 0);
    stats.value[1].value = String(papersRes.data?.total || 0);
    stats.value[2].value = String(examsRes.data?.total || 0);
    stats.value[3].value = String(activeExamsRes.data?.total || 0);

    recentExams.value = examsRes.data?.records || [];
  } catch {}
});

function statusLabel(s) {
  return { 0: "草稿", 1: "进行中", 2: "已结束", 3: "已归档" }[s] || "未知";
}

function statusType(s) {
  return { 0: "info", 1: "success", 2: "warning", 3: "info" }[s] || "info";
}

function navigateTo(route) { router.push(route); }
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <section class="stats-row">
      <div v-for="(stat, idx) in stats" :key="idx" class="stat-card" :style="{ background: stat.gradient }">
        <div class="stat-card__content">
          <div class="stat-card__info">
            <span class="stat-card__title">{{ stat.title }}</span>
            <span class="stat-card__value">{{ stat.value }}</span>
          </div>
          <div class="stat-card__icon">
            <el-icon :size="40"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
      </div>
    </section>

    <!-- 快捷操作 -->
    <section class="actions-section">
      <h3 class="section-title">快捷操作</h3>
      <div class="actions-grid">
        <div v-for="action in quickActions" :key="action.title" class="action-card" @click="navigateTo(action.route)">
          <div class="action-card__icon" :style="{ background: action.color + '20', color: action.color }">
            <el-icon :size="24"><component :is="action.icon" /></el-icon>
          </div>
          <div class="action-card__info">
            <h4>{{ action.title }}</h4>
            <p>{{ action.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 最近考试 -->
    <section class="exams-section">
      <PanelCard title="最近考试" class="full-panel">
        <template #header-extra>
          <span class="clickable" @click="navigateTo('/teacher/exams')">查看全部 ></span>
        </template>
        <div class="exam-list">
          <div v-for="exam in recentExams" :key="exam.id" class="exam-item">
            <div class="exam-item__info">
              <h4>{{ exam.examName }}</h4>
              <span class="exam-item__time">{{ exam.startTime?.substring(0, 16) }}</span>
            </div>
            <div class="exam-item__status">
              <el-tag :type="statusType(exam.publishStatus)" size="small">
                {{ statusLabel(exam.publishStatus) }}
              </el-tag>
            </div>
            <button class="btn-outline" @click="navigateTo('/teacher/exams')">查看</button>
          </div>
          <div v-if="recentExams.length === 0" class="empty-state">
            <p>暂无考试数据</p>
          </div>
        </div>
      </PanelCard>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  border-radius: 16px;
  padding: 24px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.stat-card__content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.stat-card__info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-card__title {
  font-size: 14px;
  opacity: 0.9;
}

.stat-card__value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.stat-card__icon {
  opacity: 0.3;
}

/* 快捷操作 */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.action-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-card__info h4 {
  margin: 0 0 4px;
  font-size: 15px;
  color: #333;
}

.action-card__info p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 考试列表 */
.full-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.clickable {
  cursor: pointer;
  color: #667eea;
  font-size: 13px;
}

.clickable:hover {
  text-decoration: underline;
}

.exam-list {
  max-height: 400px;
  overflow-y: auto;
}

.exam-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.exam-item:hover {
  background: #f9fafb;
}

.exam-item:last-child {
  border-bottom: none;
}

.exam-item__info h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #333;
}

.exam-item__time {
  font-size: 12px;
  color: #999;
}

.btn-outline {
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #667eea;
  color: #fff;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #ccc;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row, .actions-grid {
    grid-template-columns: 1fr;
  }
  .dashboard {
    padding: 16px;
    gap: 16px;
  }
}
</style>
