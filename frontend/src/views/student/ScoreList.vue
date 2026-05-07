<template>
  <div class="student-page">
    <h2>我的成绩</h2>
    <div v-if="list.length" class="score-cards">
      <div v-for="s in list" :key="s.examId" class="score-card">
        <div class="sc-left">
          <h4>{{ s.examName }}</h4>
          <span class="sc-time">{{ s.submitTime }}</span>
        </div>
        <div class="sc-right">
          <span class="sc-score">{{ s.totalScore }}</span>
          <el-tag :type="s.passFlag ? 'success' : 'danger'" size="small" round>{{ s.passFlag ? '通过' : '未通过' }}</el-tag>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无成绩" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"; import request from "@/api/request";
const list = ref([]);
onMounted(async () => { try { list.value = (await request.get("/student/analytics/scores")).data || []; } catch { list.value = []; } });
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
h2{margin:0 0 20px;font-size:22px;font-weight:700}
.score-cards{display:flex;flex-direction:column;gap:14px}
.score-card{display:flex;justify-content:space-between;align-items:center;padding:18px 22px;background:#fff;border:1px solid #eef1f6;border-radius:14px}
.sc-left h4{margin:0 0 4px;font-size:16px;font-weight:600;color:#1a1a2e}
.sc-time{font-size:12px;color:#b0b7c6}
.sc-right{display:flex;align-items:center;gap:14px}
.sc-score{font-size:28px;font-weight:700;color:#2e79ff}
</style>
