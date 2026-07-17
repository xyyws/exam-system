<template>
  <div class="page-container">
    <div class="page-header">
      <h2>考试记录</h2>
      <span class="total-count">共 {{ list.length }} 条记录</span>
    </div>

    <div v-if="list.length" class="records-list">
      <div v-for="r in list" :key="r.examPaperId" class="record-card">
        <div class="record-card__left">
          <div class="record-card__icon" :class="statusClass(r.answerStatus)">
            <el-icon :size="24"><component :is="statusIcon(r.answerStatus)" /></el-icon>
          </div>
          <div class="record-card__info">
            <h4>{{ r.examName }}</h4>
            <div class="record-card__meta">
              <el-tag :type="statusType(r.answerStatus)" size="small">
                {{ statusText(r.answerStatus) }}
              </el-tag>
              <span class="submit-time">{{ r.submitTime || '未提交' }}</span>
            </div>
          </div>
        </div>
        <div class="record-card__right">
          <div v-if="r.totalScore !== null && r.totalScore !== undefined" class="score-display">
            <span class="score-value">{{ r.totalScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <span v-else class="no-score">--</span>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-icon :size="64" color="#ddd"><Memo /></el-icon>
      <p>暂无考试记录</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Memo, Timer, CircleCheck, Document } from "@element-plus/icons-vue";
import request from "@/api/request";

const list = ref([]);

onMounted(async () => {
  try {
    list.value = (await request.get("/student/exams/records")).data?.records || [];
  } catch {
    list.value = [];
  }
});

function statusText(s) {
  return { 0: "未开始", 1: "进行中", 2: "已交卷", 3: "已判分" }[s] || "未知";
}

function statusType(s) {
  return { 0: "info", 1: "warning", 2: "primary", 3: "success" }[s] || "info";
}

function statusClass(s) {
  return { 0: "status--info", 1: "status--warning", 2: "status--primary", 3: "status--success" }[s] || "";
}

function statusIcon(s) {
  return { 0: Timer, 1: Timer, 2: Document, 3: CircleCheck }[s] || Timer;
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

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.total-count {
  font-size: 14px;
  color: #999;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.record-card__left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.record-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status--info { background: #f0f0f0; color: #999; }
.status--warning { background: #fff3e0; color: #e6a23c; }
.status--primary { background: #e8f0fe; color: #667eea; }
.status--success { background: #e8f5e9; color: #43e97b; }

.record-card__info h4 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.record-card__meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submit-time {
  font-size: 13px;
  color: #999;
}

.record-card__right {
  text-align: right;
}

.score-display {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.score-unit {
  font-size: 14px;
  color: #999;
}

.no-score {
  font-size: 24px;
  color: #ddd;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  color: #ccc;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .record-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .record-card__right {
    align-self: flex-end;
  }
}
</style>
