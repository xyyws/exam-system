<template>
  <div class="page-panel">
    <h2>统计分析</h2>

    <el-select v-model="examId" placeholder="选择考试" @change="load" style="width:300px;margin-bottom:16px" clearable>
      <el-option v-for="e in exams" :key="e.id" :label="e.examName" :value="e.id" />
    </el-select>

    <div v-if="summary" class="stats-grid">
      <div class="stat-card"><div class="stat-label">参加人数</div><div class="stat-value">{{ summary.participantCount }}</div></div>
      <div class="stat-card"><div class="stat-label">交卷人数</div><div class="stat-value">{{ summary.submitCount }}</div></div>
      <div class="stat-card"><div class="stat-label">平均分</div><div class="stat-value">{{ summary.avgScore }}</div></div>
      <div class="stat-card"><div class="stat-label">最高分</div><div class="stat-value">{{ summary.maxScore }}</div></div>
      <div class="stat-card"><div class="stat-label">最低分</div><div class="stat-value">{{ summary.minScore }}</div></div>
      <div class="stat-card"><div class="stat-label">通过率</div><div class="stat-value">{{ summary.passRate }}%</div></div>
    </div>

    <div v-if="rankings.length" style="margin-top:20px">
      <h3>成绩排名</h3>
      <el-table :data="rankings" stripe>
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="student_name" label="学生" />
        <el-table-column prop="student_no" label="学号" width="110" />
        <el-table-column prop="total_score" label="总分" width="100" />
      </el-table>
    </div>

    <el-empty v-if="!examId" description="选择考试查看统计分析" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getExams } from "@/api/exam";
import request from "@/api/request";
import { ElMessage } from "element-plus";

const exams = ref([]);
const examId = ref(null);
const summary = ref(null);
const rankings = ref([]);

onMounted(async () => {
  try {
    const res = await getExams({ pageSize: 100 });
    exams.value = res.data.records || [];
  } catch { /* silent */ }
});

async function load() {
  if (!examId.value) { summary.value = null; rankings.value = []; return; }
  try {
    const [s, r] = await Promise.all([
      request.get(`/teacher/analytics/exams/${examId.value}/summary`),
      request.get(`/teacher/analytics/exams/${examId.value}/rankings`, { params: { pageNum: 1, pageSize: 50 } })
    ]);
    summary.value = s.data;
    rankings.value = r.data?.records || [];
  } catch { ElMessage.error("查询失败"); }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.stat-card { padding: 20px; border: 1px solid #ebeef5; border-radius: 12px; text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: 700; color: #303133; }
</style>
