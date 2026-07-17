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
      <div style="display:flex;justify-content:space-between;align-items:center">
        <h3 style="margin:0">成绩排名</h3>
        <el-button type="success" size="small" @click="exportScores">导出成绩</el-button>
      </div>
      <el-table :data="rankings" stripe style="margin-top:8px">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="studentNo" label="学号" width="110" />
        <el-table-column prop="totalScore" label="总分" width="100" />
      </el-table>
    </div>

    <div v-if="accuracy.length" style="margin-top:24px">
      <h3>题目正确率分析</h3>
      <el-table :data="accuracy" stripe style="margin-top:8px">
        <el-table-column prop="questionId" label="题号" width="70" />
        <el-table-column prop="questionTitle" label="题干" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="80" />
        <el-table-column prop="totalCount" label="作答人数" width="90" />
        <el-table-column prop="correctCount" label="正确人数" width="90" />
        <el-table-column label="正确率" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.accuracy" :color="row.accuracy >= 60 ? '#67c23a' : '#f56c6c'" :stroke-width="14" :text-inside="true" />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-empty v-if="!examId" description="选择考试查看统计分析" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getExams } from "@/api/exam";
import { getQuestionAccuracy } from "@/api/analytics";
import request from "@/api/request";
import { ElMessage } from "element-plus";

const exams = ref([]);
const examId = ref(null);
const summary = ref(null);
const rankings = ref([]);
const accuracy = ref([]);

onMounted(async () => {
  try {
    const res = await getExams({ pageSize: 100 });
    exams.value = res.data.records || [];
  } catch { /* silent */ }
});

async function load() {
  if (!examId.value) { summary.value = null; rankings.value = []; accuracy.value = []; return; }
  try {
    const [s, r, a] = await Promise.all([
      request.get(`/teacher/analytics/exams/${examId.value}/summary`),
      request.get(`/teacher/analytics/exams/${examId.value}/rankings`, { params: { pageNum: 1, pageSize: 50 } }),
      getQuestionAccuracy(examId.value)
    ]);
    summary.value = s.data;
    rankings.value = r.data?.records || [];
    accuracy.value = a.data || [];
  } catch { ElMessage.error("查询失败"); }
}

async function exportScores() {
  if (!examId.value) return;
  try {
    const res = await request.get(`/teacher/analytics/exams/${examId.value}/export`, { responseType: "blob", timeout: 30000 });
    const blob = res.data;
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `scores_exam_${examId.value}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  } catch (e) {
    console.error("导出失败", e);
    ElMessage.error("导出失败");
  }
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
