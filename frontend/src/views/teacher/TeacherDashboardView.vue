<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import StatCard from "@/components/StatCard.vue";
import PanelCard from "@/components/PanelCard.vue";
import QuickActionCard from "@/components/QuickActionCard.vue";
import EChart from "@/components/charts/EChart.vue";
import request from "@/api/request";
import { Reading } from "@element-plus/icons-vue";

const router = useRouter();

// ─── Stat cards ───
const stats = ref([
  { title: "题目总数", value: "0", trend: "", accent: "blue", icon: "visits" },
  { title: "试卷总数", value: "0", trend: "", accent: "green", icon: "exams" },
  { title: "考试总数", value: "0", trend: "", accent: "orange", icon: "exams" },
  { title: "进行中考试", value: "0", trend: "", accent: "violet", icon: "exams" }
]);

// ─── Quick actions ───
const quickActions = [
  { title: "创建题目", icon: "createQuestion", accent: "blue", route: "/teacher/questions" },
  { title: "手动组卷", icon: "manualPaper", accent: "green", route: "/teacher/paper-manual" },
  { title: "自动组卷", icon: "autoPaper", accent: "orange", route: "/teacher/paper-auto" },
  { title: "创建考试", icon: "createExam", accent: "violet", route: "/teacher/exams" }
];

// ─── Recent exams ───
const recentExams = ref([]);

// ─── Score histogram (mock) ───
const histogram = ref([
  { label: "0-59", value: 0 },
  { label: "60-69", value: 0 },
  { label: "70-79", value: 0 },
  { label: "80-89", value: 0 },
  { label: "90-100", value: 0 }
]);

// ─── Fetch real data ───
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
  } catch {
    // use defaults
  }
});

// ─── Chart option ───
const barOption = computed(() => ({
  tooltip: { trigger: "axis" },
  grid: { left: 20, right: 16, top: 18, bottom: 20, containLabel: true },
  xAxis: {
    type: "category",
    data: histogram.value.map((i) => i.label),
    axisLine: { lineStyle: { color: "#d7deea" } },
    axisTick: { show: false }
  },
  yAxis: {
    type: "value",
    splitLine: { lineStyle: { color: "#eef2f7" } }
  },
  series: [
    {
      type: "bar",
      barWidth: 28,
      data: histogram.value.map((i) => i.value),
      itemStyle: {
        borderRadius: [10, 10, 0, 0],
        color: "#7ba6ff"
      }
    }
  ]
}));

// ─── Helpers ───
function statusLabel(s) {
  return { 0: "草稿", 1: "进行中", 2: "已结束", 3: "已归档" }[s] || "未知";
}

function statusClass(s) {
  return { 0: "status--muted", 1: "status--green", 2: "status--orange", 3: "status--muted" }[s] || "";
}

function navigateTo(route) {
  router.push(route);
}
</script>

<template>
  <div class="dashboard-stack">
    <!-- Stat Cards -->
    <section class="stats-grid">
      <StatCard
        v-for="stat in stats"
        :key="stat.title"
        :title="stat.title"
        :value="stat.value"
        :trend="stat.trend"
        :accent="stat.accent"
        :icon="stat.icon"
      />
    </section>

    <!-- Quick Actions -->
    <section class="action-grid">
      <QuickActionCard
        v-for="action in quickActions"
        :key="action.title"
        :title="action.title"
        :icon="action.icon"
        :accent="action.accent"
        @click="navigateTo(action.route)"
      />
    </section>

    <!-- Two Column: Recent Exams + Score Distribution -->
    <section class="split-grid">
      <PanelCard title="最近考试" extra="查看全部">
        <div class="table-shell">
          <table class="simple-table">
            <thead>
              <tr>
                <th>考试名称</th>
                <th>状态</th>
                <th>开始时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="exam in recentExams" :key="exam.id">
                <td>{{ exam.examName }}</td>
                <td>
                  <span class="status-text" :class="statusClass(exam.publishStatus)">
                    {{ statusLabel(exam.publishStatus) }}
                  </span>
                </td>
                <td>{{ exam.startTime?.substring(0, 16) }}</td>
                <td class="actions-cell">
                  <button class="link-button" type="button" @click="navigateTo('/teacher/exams')">
                    查看
                  </button>
                </td>
              </tr>
              <tr v-if="recentExams.length === 0">
                <td colspan="4" class="empty-cell">暂无考试数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </PanelCard>

      <PanelCard title="成绩分布（最近一次考试）">
        <div class="score-meta">
          <div><span>平均分</span><strong>--</strong></div>
          <div><span>最高分</span><strong>--</strong></div>
          <div><span>及格率</span><strong>--</strong></div>
        </div>
        <EChart :option="barOption" height="220px" />
      </PanelCard>
    </section>

    <!-- Quick Navigation -->
    <section class="nav-grid">
      <div class="nav-card" @click="navigateTo('/teacher/questions')">
        <div class="nav-icon accent-blue">
          <el-icon><component :is="quickActions[0].icon" /></el-icon>
        </div>
        <div class="nav-info">
          <div class="nav-title">题库管理</div>
          <div class="nav-desc">管理题目、分类</div>
        </div>
      </div>
      <div class="nav-card" @click="navigateTo('/teacher/papers')">
        <div class="nav-icon accent-green">
          <el-icon><component :is="quickActions[1].icon" /></el-icon>
        </div>
        <div class="nav-info">
          <div class="nav-title">试卷管理</div>
          <div class="nav-desc">手动/自动组卷</div>
        </div>
      </div>
      <div class="nav-card" @click="navigateTo('/teacher/exams')">
        <div class="nav-icon accent-orange">
          <el-icon><component :is="quickActions[3].icon" /></el-icon>
        </div>
        <div class="nav-info">
          <div class="nav-title">考试管理</div>
          <div class="nav-desc">创建、发布考试</div>
        </div>
      </div>
      <div class="nav-card" @click="navigateTo('/teacher/marking')">
        <div class="nav-icon accent-violet">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="nav-info">
          <div class="nav-title">阅卷中心</div>
          <div class="nav-desc">批阅主观题</div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.dashboard-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.split-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.table-shell {
  overflow-x: auto;
}

.simple-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.simple-table th,
.simple-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.simple-table th {
  font-weight: 600;
  color: #909399;
  background: #fafafa;
}

.simple-table tbody tr:hover {
  background: #f5f7fa;
}

.status-text {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status--green {
  color: #67c23a;
  background: #f0f9eb;
}

.status--orange {
  color: #e6a23c;
  background: #fdf6ec;
}

.status--muted {
  color: #909399;
  background: #f4f4f5;
}

.actions-cell {
  display: flex;
  gap: 8px;
}

.link-button {
  background: none;
  border: none;
  color: #409eff;
  cursor: pointer;
  padding: 0;
  font-size: 14px;
}

.link-button:hover {
  color: #66b1ff;
}

.empty-cell {
  text-align: center;
  color: #909399;
  padding: 24px;
}

.score-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.score-meta div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.score-meta span {
  font-size: 13px;
  color: #909399;
}

.score-meta strong {
  font-size: 20px;
  color: #303133;
}

/* ─── Nav Grid ─── */
.nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.nav-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.15s;
}

.nav-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.nav-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.accent-blue { background: #2f6cf5; }
.accent-green { background: #1eb36b; }
.accent-orange { background: #ff8b2a; }
.accent-violet { background: #6a5cff; }

.nav-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.nav-desc {
  font-size: 12px;
  color: #909399;
}
</style>
