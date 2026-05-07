<template>
  <div class="student-page">
    <div class="page-top">
      <h2>我的考试</h2>
      <el-radio-group v-model="filter" size="small">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="ongoing">进行中</el-radio-button>
      </el-radio-group>
    </div>
    <div v-if="exams.length" class="exam-cards">
      <div v-for="e in exams" :key="e.examId" class="exam-card">
        <div class="card-body">
          <h4>{{ e.examName }}</h4>
          <div class="card-info">
            <span>时长 {{ e.durationMinutes }} 分钟</span>
            <span>总分 {{ e.totalScore }} 分</span>
            <span>及格 {{ e.passScore }} 分</span>
          </div>
          <div class="card-time">{{ e.startTime?.substring(0,16) }} ~ {{ e.endTime?.substring(11,16) }}</div>
        </div>
        <div class="card-action">
          <el-button type="primary" round @click="$router.push(`/student/exam/${e.examId}`)">进入考试</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无可用考试" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"; import { getAvailableExams } from "@/api/runtime";
const exams = ref([]); const filter = ref("all");
onMounted(async () => { try { const res = await getAvailableExams(); exams.value = res.data?.exams || []; } catch { exams.value = []; } });
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
.page-top{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-top h2{margin:0;font-size:22px;font-weight:700}
.exam-cards{display:flex;flex-direction:column;gap:16px}
.exam-card{display:flex;align-items:center;justify-content:space-between;padding:20px 24px;background:#fff;border:1px solid #eef1f6;border-radius:16px;transition:box-shadow .2s}
.exam-card:hover{box-shadow:0 8px 24px rgba(0,0,0,.06)}
.card-body h4{margin:0 0 8px;font-size:17px;font-weight:600;color:#1a1a2e}
.card-info{display:flex;gap:20px;font-size:13px;color:#8b92a8;margin-bottom:4px}
.card-time{font-size:12px;color:#b0b7c6}
.card-action{flex-shrink:0}
</style>
