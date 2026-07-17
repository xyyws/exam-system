<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的成绩</h2>
      <span class="total-count">共 {{ list.length }} 条记录</span>
    </div>

    <div v-if="list.length" class="score-grid">
      <div v-for="s in list" :key="s.examId" class="score-card" :class="{ passed: s.passFlag }">
        <div class="score-card__header">
          <h4>{{ s.examName }}</h4>
          <el-link :type="s.passFlag ? 'success' : 'danger'" @click="showDetail(s)">
            <el-tag :type="s.passFlag ? 'success' : 'danger'" size="small" round effect="dark">
              {{ s.passFlag ? '通过' : '未通过' }}
            </el-tag>
          </el-link>
        </div>
        <div class="score-card__body">
          <div class="score-display">
            <span class="score-value">{{ s.totalScore }}</span>
            <span class="score-unit">分</span>
          </div>
        </div>
        <div class="score-card__footer">
          <span class="submit-time">{{ s.submitTime }}</span>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-icon :size="64" color="#ddd"><Document /></el-icon>
      <p>暂无成绩记录</p>
    </div>

    <!-- 试卷详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="selectedRecord?.examName + ' - 答题详情'" width="800px" class="detail-dialog">
      <div v-if="paperDetail" class="paper-summary">
        <div class="summary-item">
          <span class="label">得分</span>
          <span class="value highlight">{{ paperDetail.totalScore ?? '-' }}分</span>
        </div>
        <div class="summary-item">
          <span class="label">状态</span>
          <el-tag :type="selectedRecord?.passFlag ? 'success' : 'danger'" size="small">
            {{ selectedRecord?.passFlag ? '通过' : '未通过' }}
          </el-tag>
        </div>
        <div class="summary-item">
          <span class="label">提交时间</span>
          <span class="value">{{ selectedRecord?.submitTime }}</span>
        </div>
      </div>

      <el-divider />

      <div class="answers-list">
        <div v-for="(a, idx) in answers" :key="a.id" class="answer-item" :class="{ correct: a.isCorrect === 1, wrong: a.isCorrect === 0 }">
          <div class="answer-header">
            <span class="q-index">第{{ idx + 1 }}题</span>
            <el-tag :type="typeTag(a.questionType)" size="small">{{ typeText(a.questionType) }}</el-tag>
            <span class="q-score">{{ a.score }}/{{ a.maxScore }}分</span>
            <el-tag v-if="a.isCorrect === 1" type="success" size="small">正确</el-tag>
            <el-tag v-else-if="a.isCorrect === 0" type="danger" size="small">错误</el-tag>
            <el-tag v-else type="info" size="small">待批改</el-tag>
          </div>
          <div class="answer-body">
            <div class="q-title" v-html="getTitle(a)"></div>
            <div class="a-row">
              <span class="a-label">你的答案：</span>
              <span :class="{ 'text-success': a.isCorrect === 1, 'text-danger': a.isCorrect === 0 }">{{ a.studentAnswer || '(未作答)' }}</span>
            </div>
            <div v-if="a.correctAnswer" class="a-row">
              <span class="a-label">正确答案：</span>
              <span class="text-success">{{ a.correctAnswer }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!answers.length" class="dialog-empty">暂无答题记录</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Document } from "@element-plus/icons-vue";
import request from "@/api/request";

const list = ref([]);
const dialogVisible = ref(false);
const selectedRecord = ref(null);
const paperDetail = ref(null);
const answers = ref([]);

onMounted(async () => {
  try {
    list.value = (await request.get("/student/analytics/scores")).data || [];
  } catch {
    list.value = [];
  }
});

async function showDetail(record) {
  selectedRecord.value = record;
  dialogVisible.value = true;
  paperDetail.value = null;
  answers.value = [];
  try {
    const res = await request.get(`/student/exams/records/${record.examPaperId}`);
    paperDetail.value = res.data?.paper || {};
    answers.value = res.data?.answers || [];
  } catch {
    paperDetail.value = {};
    answers.value = [];
  }
}

function typeText(t) { return { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" }[t] || "未知"; }
function typeTag(t) { return { 1: "primary", 2: "warning", 3: "info", 4: "", 5: "danger" }[t] || "info"; }

function getTitle(a) {
  try {
    const snap = JSON.parse(a.questionSnapshot);
    return snap.title || '';
  } catch { return ''; }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 { margin: 0; font-size: 24px; font-weight: 700; color: #333; }
.total-count { font-size: 14px; color: #999; }

.score-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.score-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-left: 4px solid #f5576c;
}

.score-card.passed { border-left-color: #43e97b; }
.score-card:hover { transform: translateY(-4px); box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15); }

.score-card__header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.score-card__header h4 { margin: 0; font-size: 16px; font-weight: 600; color: #333; flex: 1; margin-right: 12px; }

.score-card__body { margin-bottom: 16px; }
.score-display { display: flex; align-items: baseline; gap: 4px; }
.score-value { font-size: 48px; font-weight: 700; color: #667eea; line-height: 1; }
.score-unit { font-size: 16px; color: #999; }

.score-card__footer { padding-top: 16px; border-top: 1px solid #f0f0f0; }
.submit-time { font-size: 13px; color: #999; }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 40px; color: #ccc; }
.empty-state p { margin-top: 16px; font-size: 16px; }

/* 试卷详情 */
.paper-summary {
  display: flex;
  gap: 32px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
}

.summary-item { display: flex; flex-direction: column; gap: 4px; }
.summary-item .label { font-size: 13px; color: #999; }
.summary-item .value { font-size: 15px; color: #333; font-weight: 600; }
.summary-item .value.highlight { font-size: 24px; color: #667eea; }

.answers-list { display: flex; flex-direction: column; gap: 16px; }

.answer-item {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
}

.answer-item.correct { border-color: #c8e6c9; }
.answer-item.wrong { border-color: #ffcdd2; }

.answer-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fafafa;
}

.q-index { font-weight: 600; color: #333; }
.q-score { margin-left: auto; font-size: 13px; color: #666; }

.answer-body { padding: 16px; }
.q-title { margin-bottom: 12px; line-height: 1.6; color: #333; }
.a-row { margin-bottom: 8px; font-size: 14px; }
.a-label { color: #999; }
.text-success { color: #43e97b; font-weight: 600; }
.text-danger { color: #f5576c; font-weight: 600; }

.dialog-empty { text-align: center; padding: 40px; color: #999; }

@media (max-width: 768px) {
  .score-grid { grid-template-columns: 1fr; }
  .paper-summary { flex-direction: column; gap: 12px; }
}
</style>
