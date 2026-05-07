<template>
  <div class="student-page">
    <h2>考试记录</h2>
    <div v-if="list.length" class="record-cards">
      <div v-for="r in list" :key="r.examPaperId" class="record-card">
        <div class="rc-left">
          <h4>{{ r.examName }}</h4>
          <span class="rc-status">{{ {0:'未开始',1:'进行中',2:'已交卷',3:'已判分'}[r.answerStatus] }}</span>
        </div>
        <div class="rc-right">
          <span class="rc-score" v-if="r.totalScore">{{ r.totalScore }}分</span>
          <span class="rc-time">{{ r.submitTime }}</span>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无考试记录" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"; import request from "@/api/request";
const list = ref([]);
onMounted(async () => { try { list.value = (await request.get("/student/exams/records")).data?.records || []; } catch { list.value = []; } });
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
h2{margin:0 0 20px;font-size:22px;font-weight:700}
.record-cards{display:flex;flex-direction:column;gap:12px}
.record-card{display:flex;justify-content:space-between;align-items:center;padding:16px 22px;background:#fff;border:1px solid #eef1f6;border-radius:14px}
.rc-left h4{margin:0 0 4px;font-size:16px;font-weight:600;color:#1a1a2e}
.rc-status{font-size:12px;color:#8b92a8}
.rc-right{text-align:right}
.rc-score{display:block;font-size:22px;font-weight:700;color:#2e79ff}
.rc-time{font-size:11px;color:#c0c4cc}
</style>
