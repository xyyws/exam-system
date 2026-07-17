<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getAvailableExams } from "@/api/runtime";
import { getOngoingExam } from "@/api/runtime";
import { getScoreTrend, getTypeBreakdown, getWrongBookSummary } from "@/api/analytics";
import { Timer, Document, Reading, DataAnalysis } from "@element-plus/icons-vue";
import { ElIcon } from "element-plus";
import PanelCard from "@/components/PanelCard.vue";
import EChart from "@/components/charts/EChart.vue";

const router = useRouter();

const upcomingExams = ref([]);
const ongoingExam = ref(null);
const wrongBook = ref({ total: 0, items: [] });
const scoreTrend = ref([]);
const radarData = ref({ types: [], correctRates: [] });

onMounted(async () => {
  const [availRes, ongoingRes, wrongRes, trendRes, breakdownRes] = await Promise.allSettled([
    getAvailableExams(),
    getOngoingExam(),
    getWrongBookSummary(),
    getScoreTrend(),
    getTypeBreakdown()
  ]);
  if (availRes.status === "fulfilled") upcomingExams.value = availRes.value.data?.exams || [];
  if (ongoingRes.status === "fulfilled" && ongoingRes.value.data?.hasOngoing) ongoingExam.value = ongoingRes.value.data;
  if (wrongRes.status === "fulfilled") wrongBook.value = wrongRes.value.data || { total: 0, items: [] };
  if (trendRes.status === "fulfilled") scoreTrend.value = trendRes.value.data || [];
  if (breakdownRes.status === "fulfilled") radarData.value = breakdownRes.value.data || { types: [], correctRates: [] };
});

// 统计卡片数据
const statCards = computed(() => [
  { title: "待考考试", value: upcomingExams.value.length, icon: Timer, gradient: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" },
  { title: "错题数量", value: wrongBook.value.total, icon: Document, gradient: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
  { title: "已考次数", value: scoreTrend.value.length, icon: Reading, gradient: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" },
  { title: "平均得分", value: avgScore.value, icon: DataAnalysis, gradient: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)" }
]);

const avgScore = computed(() => {
  if (scoreTrend.value.length === 0) return "-";
  const sum = scoreTrend.value.reduce((acc, item) => acc + (item.percentage ?? 0), 0);
  return Math.round(sum / scoreTrend.value.length) + "%";
});

function formatRemain(seconds) {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
}

function enterExam(examId) {
  router.push({ name: "student-exam", params: { id: examId } });
}

const scoreTrendOption = computed(() => ({
  tooltip: {
    trigger: "axis",
    backgroundColor: "rgba(255,255,255,0.95)",
    borderColor: "#eee",
    textStyle: { color: "#333" }
  },
  grid: { left: "3%", right: "4%", bottom: "8%", top: "12%", containLabel: true },
  xAxis: {
    type: "category",
    boundaryGap: false,
    data: scoreTrend.value.map((item) => item.examName || item.examTime?.slice(5, 10)),
    axisLine: { lineStyle: { color: "#e0e0e0" } },
    axisTick: { show: false },
    axisLabel: { color: "#666", fontSize: 11 }
  },
  yAxis: {
    type: "value",
    min: 0,
    max: 100,
    axisLine: { show: false },
    axisTick: { show: false },
    splitLine: { lineStyle: { color: "#f0f0f0", type: "dashed" } },
    axisLabel: { color: "#999" }
  },
  series: [
    {
      type: "line",
      smooth: true,
      symbolSize: 8,
      lineStyle: { width: 3, color: "#667eea" },
      itemStyle: { color: "#667eea" },
      areaStyle: {
        color: {
          type: "linear",
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: "rgba(102,126,234,0.3)" },
            { offset: 1, color: "rgba(102,126,234,0)" }
          ]
        }
      },
      data: scoreTrend.value.map((item) => item.percentage ?? 0)
    }
  ]
}));

const radarOption = computed(() => ({
  radar: {
    radius: "65%",
    indicator: radarData.value.types.map((name) => ({ name, max: 100 })),
    axisName: { color: "#666", fontSize: 12 },
    splitArea: { areaStyle: { color: ["#f9fbff", "#f4f8ff", "#eef3ff", "#e8eeff"] } },
    splitLine: { lineStyle: { color: "#e0e0e0" } }
  },
  series: [
    {
      type: "radar",
      data: [
        {
          value: radarData.value.correctRates,
          areaStyle: { color: "rgba(102,126,234,0.25)" },
          lineStyle: { color: "#667eea", width: 2 },
          itemStyle: { color: "#667eea" }
        }
      ]
    }
  ]
}));
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <section class="stats-row">
      <div v-for="(stat, idx) in statCards" :key="idx" class="stat-card" :style="{ background: stat.gradient }">
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

    <!-- 进行中的考试 -->
    <section v-if="ongoingExam" class="ongoing-section">
      <div class="ongoing-card">
        <div class="ongoing-card__left">
          <div class="ongoing-badge">考试中</div>
          <h3>{{ ongoingExam.examName }}</h3>
          <div class="ongoing-info">
            <span>已答 {{ ongoingExam.answeredCount }}/{{ ongoingExam.totalCount }} 题</span>
            <span class="countdown">
              <el-icon><Timer /></el-icon>
              {{ formatRemain(ongoingExam.remainSeconds) }}
            </span>
          </div>
        </div>
        <button class="btn-primary" @click="enterExam(ongoingExam.examId)">继续考试</button>
      </div>
    </section>

    <!-- 图表区域 -->
    <section class="charts-row">
      <PanelCard title="成绩趋势" class="chart-panel">
        <template #header-extra>
          <span class="chart-subtitle">最近考试得分率变化</span>
        </template>
        <div v-if="scoreTrend.length" class="chart-wrap">
          <EChart :option="scoreTrendOption" height="300px" />
        </div>
        <div v-else class="empty-state">
          <el-icon :size="48" color="#ddd"><DataAnalysis /></el-icon>
          <p>暂无考试记录</p>
        </div>
      </PanelCard>

      <PanelCard title="知识点掌握" class="chart-panel">
        <template #header-extra>
          <span class="chart-subtitle">各题型正确率</span>
        </template>
        <div v-if="radarData.types.length" class="chart-wrap">
          <EChart :option="radarOption" height="300px" />
        </div>
        <div v-else class="empty-state">
          <el-icon :size="48" color="#ddd"><DataAnalysis /></el-icon>
          <p>暂无数据</p>
        </div>
      </PanelCard>
    </section>

    <!-- 待考列表 & 错题本 -->
    <section class="bottom-row">
      <PanelCard title="待考考试" class="list-panel">
        <template #header-extra>
          <span class="chart-subtitle">{{ upcomingExams.length }} 场待考</span>
        </template>
        <div class="exam-list">
          <div v-for="item in upcomingExams" :key="item.examId" class="exam-item">
            <div class="exam-item__info">
              <h4>{{ item.examName }}</h4>
              <span class="exam-item__time">{{ item.startTime }} · {{ item.durationMinutes }}分钟</span>
            </div>
            <button class="btn-outline" @click="enterExam(item.examId)">去考试</button>
          </div>
          <div v-if="upcomingExams.length === 0" class="empty-state">
            <p>暂无待考考试</p>
          </div>
        </div>
      </PanelCard>

      <PanelCard title="错题本" class="list-panel">
        <template #header-extra>
          <span class="clickable" @click="router.push('/student/wrong-book')">查看全部 ></span>
        </template>
        <div class="wrong-list">
          <div v-for="item in wrongBook.items" :key="item.label" class="wrong-item">
            <span class="wrong-item__label">{{ item.label }}</span>
            <span class="wrong-item__count">{{ item.count }} 题</span>
          </div>
          <div v-if="wrongBook.items.length === 0" class="empty-state">
            <p>暂无错题</p>
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

/* 进行中考试 */
.ongoing-section {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.95; }
}

.ongoing-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.ongoing-card h3 {
  margin: 8px 0 12px;
  font-size: 20px;
}

.ongoing-badge {
  display: inline-block;
  background: rgba(255,255,255,0.2);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.ongoing-info {
  display: flex;
  gap: 24px;
  font-size: 14px;
  opacity: 0.9;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}

.btn-primary {
  background: #fff;
  color: #667eea;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

/* 图表区域 */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.chart-wrap {
  padding: 16px 8px;
}

.chart-subtitle {
  font-size: 13px;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #ccc;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

/* 底部列表 */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.list-panel {
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
  max-height: 300px;
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

.wrong-list {
  max-height: 300px;
  overflow-y: auto;
}

.wrong-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.wrong-item:last-child {
  border-bottom: none;
}

.wrong-item__label {
  font-size: 14px;
  color: #333;
}

.wrong-item__count {
  font-size: 13px;
  color: #f5576c;
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row, .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .dashboard {
    padding: 16px;
    gap: 16px;
  }
  .ongoing-card {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  .ongoing-info {
    justify-content: center;
  }
}
</style>
