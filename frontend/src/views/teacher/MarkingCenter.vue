<template>
  <div class="page-panel">
    <h2>阅卷中心</h2>

    <!-- Exam list for marking -->
    <div v-if="!viewingPaper">
      <div class="info-bar">
        <span>选择考试查看待阅答卷</span>
      </div>
      <el-select v-model="selectedExamId" placeholder="选择考试" @change="loadPapers" style="width:300px;margin-bottom:16px" clearable>
        <el-option v-for="e in exams" :key="e.id" :label="e.examName" :value="e.id" />
      </el-select>

      <el-table v-if="selectedExamId" :data="papers" stripe v-loading="loading">
        <el-table-column prop="examPaperId" label="答卷ID" width="80" />
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="studentNo" label="学号" width="110" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.answerStatus === 3 ? 'success' : 'warning'" size="small">
              {{ row.answerStatus === 3 ? '已判分' : '待阅卷' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="得分" width="70" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="openMarking(row.examPaperId)">阅卷</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="selectedExamId && !papers.length && !loading" description="暂无答卷" />
      <el-empty v-if="!selectedExamId" description="请选择考试" />
    </div>

    <!-- Marking detail view -->
    <div v-else>
      <el-button @click="viewingPaper = false; answers = []; current = null" style="margin-bottom:16px">返回列表</el-button>
      <span style="margin-left:12px;font-weight:600">答卷 #{{ viewingPaper }}</span>

      <div v-if="answers.length" class="marking-layout">
        <div class="answer-list">
          <div v-for="a in answers" :key="a.id" class="answer-card" :class="{ active: currentId === a.id }" @click="select(a)">
            <span class="a-type">{{ typeMap[a.questionType] || a.questionType }}</span>
            <span class="a-status">{{ a.markStatus === 1 ? '已阅' : '待阅' }}</span>
            <span v-if="a.score > 0">{{ a.score }}分</span>
          </div>
        </div>
        <div class="marking-main" v-if="current">
          <div class="question-show">
            <strong>题目 ({{ current.maxScore }}分)</strong>
            <div v-html="current.questionSnapshot ? JSON.parse(current.questionSnapshot).title : ''" style="margin-top:8px"></div>
          </div>
          <div class="answer-show">
            <strong>学生答案</strong>
            <div style="margin-top:8px; padding:12px; background:#fafafa; border-radius:6px">{{ current.studentAnswer || '(未作答)' }}</div>
          </div>
          <div class="score-panel">
            <el-input-number v-model="score" :min="0" :max="current.maxScore" :step="0.5" />
            <el-input v-model="comment" placeholder="评语" style="width:200px; margin-left:10px" />
            <el-button type="primary" @click="submitScore" style="margin-left:10px">提交评分</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="无主观题需要阅卷" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getExams } from "@/api/exam";
import { getExamPaperDetail, scoreAnswer } from "@/api/marking";
import request from "@/api/request";
import { ElMessage } from "element-plus";

const exams = ref([]);
const selectedExamId = ref(null);
const papers = ref([]);
const loading = ref(false);
const viewingPaper = ref(null);

const answers = ref([]);
const current = ref(null);
const currentId = ref(null);
const score = ref(0);
const comment = ref("");
const typeMap = { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" };

onMounted(async () => {
  try {
    const res = await getExams({ pageSize: 100 });
    exams.value = res.data.records || [];
  } catch { /* silent */ }
});

async function loadPapers() {
  if (!selectedExamId.value) { papers.value = []; return; }
  loading.value = true;
  try {
    const res = await request.get(`/teacher/exams/${selectedExamId.value}/monitor`);
    papers.value = res.data?.records || [];
  } finally { loading.value = false; }
}

async function openMarking(examPaperId) {
  viewingPaper.value = examPaperId;
  try {
    const res = await getExamPaperDetail(examPaperId);
    answers.value = res.data.answers || [];
    if (answers.value.length) select(answers.value[0]);
  } catch { ElMessage.error("加载失败"); }
}

function select(a) {
  current.value = a;
  currentId.value = a.id;
  score.value = a.score || 0;
  comment.value = a.markComment || "";
}

async function submitScore() {
  if (!current.value) return;
  try {
    await scoreAnswer(current.value.id, { score: score.value, markComment: comment.value });
    current.value.score = score.value;
    current.value.markStatus = 1;
    ElMessage.success("评分已提交");
  } catch (e) { ElMessage.error(e.message || "提交失败"); }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
.info-bar { margin-bottom: 12px; font-size: 14px; color: #909399; }
.marking-layout { display: flex; gap: 16px; margin-top: 16px; }
.answer-list { width: 200px; border: 1px solid #ebeef5; border-radius: 8px; overflow-y: auto; max-height: 500px; }
.answer-card { padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #f0f0f0; display: flex; justify-content: space-between; font-size: 13px; }
.answer-card.active { background: #ecf5ff; }
.answer-card:hover { background: #f5f7fa; }
.a-type { font-weight: 600; }
.a-status { color: #909399; }
.marking-main { flex: 1; }
.question-show, .answer-show { margin-bottom: 16px; padding: 12px; border: 1px solid #ebeef5; border-radius: 8px; }
.score-panel { display: flex; align-items: center; padding-top: 12px; border-top: 1px solid #ebeef5; }
</style>
