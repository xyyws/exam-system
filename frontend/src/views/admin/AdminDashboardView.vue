<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { UserFilled, OfficeBuilding, Calendar } from "@element-plus/icons-vue";
import { ElIcon } from "element-plus";
import PanelCard from "@/components/PanelCard.vue";
import EChart from "@/components/charts/EChart.vue";
import request from "@/api/request";

const router = useRouter();
const dialogVisible = ref(false);
const examDialogVisible = ref(false);
const selectedTeacher = ref(null);
const teacherExams = ref([]);

const stats = ref([
  { title: "总用户数", value: "0", icon: UserFilled, route: "/admin/students", gradient: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" },
  { title: "班级数量", value: "0", icon: OfficeBuilding, route: "/admin/classes", gradient: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
  { title: "已发布考试", value: "0", icon: Calendar, gradient: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)", action: () => dialogVisible.value = true }
]);

const userDistribution = ref([
  { name: "管理员", value: 0 },
  { name: "教师", value: 0 },
  { name: "学生", value: 0 }
]);

const teacherExamStats = ref([]);

onMounted(async () => {
  try {
    const res = await request.get("/admin/stats");
    const d = res.data || {};
    stats.value[0].value = String(d.totalUsers ?? 0);
    stats.value[1].value = String(d.classCount ?? 0);
    stats.value[2].value = String(d.examCount ?? 0);

    const adminCount = Math.max(0, (d.totalUsers || 0) - (d.teacherCount || 0) - (d.studentCount || 0));
    userDistribution.value = [
      { name: "管理员", value: adminCount },
      { name: "教师", value: d.teacherCount || 0 },
      { name: "学生", value: d.studentCount || 0 }
    ];

    teacherExamStats.value = d.teacherExamStats || [];
  } catch {}
});

const pieOption = computed(() => ({
  tooltip: {
    trigger: "item",
    formatter: "{b}: {c}人 ({d}%)",
    backgroundColor: "rgba(255,255,255,0.95)",
    borderColor: "#eee",
    borderWidth: 1,
    textStyle: { color: "#333" }
  },
  legend: {
    orient: "vertical",
    right: "5%",
    top: "center",
    itemGap: 16,
    textStyle: { fontSize: 13, color: "#666" }
  },
  color: ["#667eea", "#4facfe", "#f5576c"],
  series: [
    {
      type: "pie",
      radius: ["50%", "75%"],
      center: ["35%", "50%"],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: "#fff",
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: "bold" },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,0.2)" }
      },
      data: userDistribution.value
    }
  ]
}));

const barOption = computed(() => {
  const names = teacherExamStats.value.map(t => t.teacherName);
  const counts = teacherExamStats.value.map(t => t.examCount);
  return {
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      backgroundColor: "rgba(255,255,255,0.95)",
      borderColor: "#eee",
      borderWidth: 1,
      textStyle: { color: "#333" }
    },
    grid: { left: "3%", right: "4%", bottom: "8%", top: "12%", containLabel: true },
    xAxis: {
      type: "category",
      data: names,
      axisLine: { lineStyle: { color: "#e0e0e0" } },
      axisLabel: {
        rotate: names.length > 4 ? 30 : 0,
        fontSize: 12,
        color: "#666"
      },
      axisTick: { show: false }
    },
    yAxis: {
      type: "value",
      name: "发卷数",
      nameTextStyle: { color: "#999", fontSize: 12 },
      minInterval: 1,
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: "#f0f0f0", type: "dashed" } }
    },
    series: [
      {
        name: "发卷数",
        type: "bar",
        data: counts,
        barWidth: "35%",
        itemStyle: {
          color: {
            type: "linear",
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: "#667eea" },
              { offset: 1, color: "#764ba2" }
            ]
          },
          borderRadius: [6, 6, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: {
              type: "linear",
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: "#764ba2" },
                { offset: 1, color: "#667eea" }
              ]
            }
          }
        }
      }
    ]
  };
});

function navigateTo(route) { router.push(route); }
function handleStatClick(stat) {
  if (stat.action) stat.action();
  else if (stat.route) navigateTo(stat.route);
}

async function showTeacherExams(teacher) {
  selectedTeacher.value = teacher;
  examDialogVisible.value = true;
  try {
    const res = await request.get("/admin/stats/teacher-exams", { params: { creatorId: teacher.creatorId } });
    teacherExams.value = res.data || [];
  } catch { teacherExams.value = []; }
}

function statusText(s) { return { 0: "草稿", 1: "已发布", 2: "已结束", 3: "已归档" }[s] || "未知"; }
function statusType(s) { return { 0: "info", 1: "success", 2: "warning", 3: "info" }[s] || "info"; }
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <section class="stats-row">
      <div
        v-for="(stat, idx) in stats"
        :key="idx"
        class="stat-card"
        :class="{ clickable: stat.route || stat.action }"
        :style="{ background: stat.gradient }"
        @click="handleStatClick(stat)"
      >
        <div class="stat-card__content">
          <div class="stat-card__info">
            <span class="stat-card__title">{{ stat.title }}</span>
            <span class="stat-card__value">{{ stat.value }}</span>
          </div>
          <div class="stat-card__icon">
            <el-icon :size="48"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
        <div class="stat-card__footer" v-if="stat.route || stat.action">
          <span>查看详情</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- 已发布考试详情对话框 -->
    <el-dialog v-model="dialogVisible" title="教师已发布考试统计" width="600px">
      <el-table :data="teacherExamStats" stripe>
        <el-table-column type="index" label="排名" width="70" />
        <el-table-column prop="teacherName" label="教师姓名" />
        <el-table-column prop="teacherNo" label="工号" />
        <el-table-column prop="examCount" label="已发布考试数" width="140">
          <template #default="{ row }">
            <el-link type="primary" @click="showTeacherExams(row)">{{ row.examCount }} 场</el-link>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!teacherExamStats.length" class="dialog-empty">暂无数据</div>
    </el-dialog>

    <!-- 教师考试列表对话框 -->
    <el-dialog v-model="examDialogVisible" :title="selectedTeacher?.teacherName + ' - 考试列表'" width="700px">
      <el-table :data="teacherExams" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="examName" label="考试名称" min-width="150" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.publishStatus)" size="small">{{ statusText(row.publishStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="时长" width="70">
          <template #default="{ row }">{{ row.durationMinutes }}分</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="60" />
        <el-table-column label="开始时间" width="160">
          <template #default="{ row }">{{ row.startTime?.replace('T', ' ').substring(0, 16) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!teacherExams.length" class="dialog-empty">暂无考试</div>
    </el-dialog>

    <!-- 图表区域 -->
    <section class="charts-row">
      <PanelCard title="教师已发布考试统计" class="chart-panel bar-panel">
        <template #header-extra>
          <span class="chart-subtitle">每位教师已发布的考试数量</span>
        </template>
        <div v-if="teacherExamStats.length" class="chart-wrap">
          <EChart :option="barOption" height="350px" />
        </div>
        <div v-else class="empty-state">
          <el-icon :size="48" color="#ddd"><Calendar /></el-icon>
          <p>暂无考试数据</p>
        </div>
      </PanelCard>

      <PanelCard title="用户角色分布" class="chart-panel pie-panel">
        <template #header-extra>
          <span class="chart-subtitle">系统用户构成</span>
        </template>
        <div class="chart-wrap">
          <EChart :option="pieOption" height="350px" />
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
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  border-radius: 16px;
  padding: 24px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 100%);
  pointer-events: none;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
}

.stat-card__content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.stat-card__info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-card__title {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
}

.stat-card__value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-card__icon {
  opacity: 0.3;
  transform: rotate(-15deg);
}

.stat-card__footer {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 13px;
  opacity: 0.8;
  position: relative;
  z-index: 1;
}

/* 图表区域 */
.charts-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 20px;
}

.chart-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.chart-wrap {
  padding: 16px 8px;
}

.chart-subtitle {
  font-size: 13px;
  color: #999;
  font-weight: normal;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 350px;
  color: #ccc;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

.dialog-empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 响应式 */
@media (max-width: 1200px) {
  .charts-row {
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
}
</style>
