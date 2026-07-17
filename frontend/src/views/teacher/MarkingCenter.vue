<template>
  <div class="page-container">
    <div class="page-header">
      <h2>阅卷中心</h2>
    </div>

    <!-- 考试选择 -->
    <div v-if="!viewingPaper" class="content-card">
      <div class="selector-section">
        <h3>选择考试</h3>
        <el-select v-model="selectedExamId" placeholder="请选择考试" @change="loadPapers" style="width: 100%" clearable>
          <el-option v-for="e in exams" :key="e.id" :label="e.examName" :value="e.id" />
        </el-select>
      </div>

      <div v-if="selectedExamId" class="papers-section">
        <h3>待阅答卷</h3>
        <div v-loading="loading" class="papers-grid">
          <div v-for="paper in papers" :key="paper.examPaperId" class="paper-card" :class="{ marked: paper.answerStatus === 3 }">
            <div class="paper-card__header">
              <div class="student-info">
                <div class="avatar">{{ paper.studentName?.charAt(0) || '?' }}</div>
                <div>
                  <h4>{{ paper.studentName }}</h4>
                  <span class="student-no">{{ paper.studentNo }}</span>
                </div>
              </div>
              <el-tag :type="paper.answerStatus === 3 ? 'success' : 'warning'" size="small">
                {{ paper.answerStatus === 3 ? '已判分' : '待阅卷' }}
              </el-tag>
            </div>
            <div class="paper-card__body">
              <div v-if="paper.totalScore !== null && paper.totalScore !== undefined" class="score-display">
                <span class="score-value">{{ paper.totalScore }}</span>
                <span class="score-unit">分</span>
              </div>
              <span v-else class="no-score">未评分</span>
            </div>
            <div class="paper-card__footer">
              <el-button type="primary" link @click="openMarking(paper.examPaperId)">
                {{ paper.answerStatus === 3 ? '查看' : '阅卷' }}
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="selectedExamId && !papers.length && !loading" description="暂无答卷" />
      </div>

      <div v-if="!selectedExamId" class="empty-state">
        <el-icon :size="64" color="#ddd"><Reading /></el-icon>
        <p>请先选择考试</p>
      </div>
    </div>

    <!-- 阅卷详情 -->
    <div v-else class="content-card">
      <div class="marking-header">
        <el-button @click="viewingPaper = false; answers = []; current = null" class="btn-back">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
        <span class="paper-id">答卷 #{{ viewingPaper }}</span>
      </div>

      <div v-if="answers.length" class="marking-layout">
        <div class="answer-sidebar">
          <div v-for="a in answers" :key="a.id" class="answer-item" :class="{ active: currentId === a.id, marked: a.markStatus === 1 }" @click="select(a)">
            <span class="answer-type">{{ typeMap[a.questionType] || a.questionType }}</span>
            <span class="answer-score">{{ a.markStatus === 1 ? a.score + '分' : '待阅' }}</span>
          </div>
        </div>

        <div class="marking-main" v-if="current">
          <div class="question-panel">
            <div class="panel-header">
              <span class="panel-title">题目</span>
              <span class="panel-score">{{ current.maxScore }}分</span>
            </div>
            <div class="panel-body">{{ current.questionTitle || '题目已删除' }}</div>
          </div>

          <div class="answer-panel">
            <div class="panel-header">
              <span class="panel-title">学生答案</span>
            </div>
            <div class="panel-body answer-text">{{ current.studentAnswer || '(未作答)' }}</div>
          </div>

          <div class="scoring-panel">
            <div class="scoring-row">
              <div class="scoring-item">
                <label>评分</label>
                <el-input-number v-model="score" :min="0" :max="current.maxScore" :step="0.5" />
              </div>
              <div class="scoring-item">
                <label>评语</label>
                <el-input v-model="comment" placeholder="请输入评语" />
              </div>
              <el-button type="primary" class="btn-submit" @click="submitScore">提交评分</el-button>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="无主观题需要阅卷" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ArrowLeft, Reading } from "@element-plus/icons-vue";
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
  } catch {}
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
.page-container {
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.page-header h2 {
  margin: 0 0 24px;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.content-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.selector-section {
  margin-bottom: 32px;
}

.selector-section h3, .papers-section h3 {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.papers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.paper-card {
  background: #f9fafb;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.paper-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.paper-card.marked {
  border-color: #43e97b;
}

.paper-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.student-info h4 {
  margin: 0;
  font-size: 15px;
  color: #333;
}

.student-no {
  font-size: 12px;
  color: #999;
}

.paper-card__body {
  margin-bottom: 12px;
}

.score-display {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.score-value {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
}

.score-unit {
  font-size: 14px;
  color: #999;
}

.no-score {
  font-size: 14px;
  color: #ccc;
}

.paper-card__footer {
  text-align: right;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #ccc;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}

/* 阅卷详情 */
.marking-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.btn-back {
  background: #f5f7fa;
  border: none;
}

.paper-id {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.marking-layout {
  display: flex;
  gap: 24px;
}

.answer-sidebar {
  width: 200px;
  background: #f9fafb;
  border-radius: 12px;
  padding: 12px;
  max-height: 600px;
  overflow-y: auto;
}

.answer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 8px;
}

.answer-item:hover {
  background: #e8f0fe;
}

.answer-item.active {
  background: #667eea;
  color: #fff;
}

.answer-item.marked {
  opacity: 0.7;
}

.answer-type {
  font-size: 13px;
  font-weight: 600;
}

.answer-score {
  font-size: 12px;
}

.marking-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-panel, .answer-panel {
  background: #f9fafb;
  border-radius: 12px;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f0f2f5;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.panel-score {
  font-size: 13px;
  color: #667eea;
  font-weight: 600;
}

.panel-body {
  padding: 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.answer-text {
  white-space: pre-wrap;
}

.scoring-panel {
  background: #f9fafb;
  border-radius: 12px;
  padding: 20px;
}

.scoring-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
}

.scoring-item {
  flex: 1;
}

.scoring-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #666;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  height: 40px;
  padding: 0 24px;
}

@media (max-width: 768px) {
  .marking-layout {
    flex-direction: column;
  }
  .answer-sidebar {
    width: 100%;
    max-height: 200px;
  }
  .scoring-row {
    flex-direction: column;
  }
}
</style>
