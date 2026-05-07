<template>
  <div class="student-page">
    <div class="page-top"><h2>错题本</h2><span class="count">共 {{ list.length }} 题</span></div>
    <div v-if="list.length" class="wrong-cards">
      <div v-for="w in list" :key="w.questionId" class="wrong-card" :class="{ mastered: w.masteredStatus === 1 }">
        <div class="wc-header">
          <span class="wc-id">#{{ w.questionId }}</span>
          <span class="wc-count">错 {{ w.wrongCount }} 次</span>
          <el-switch :model-value="w.masteredStatus === 1" @change="toggleMastered(w)" active-text="已掌握" size="small" />
        </div>
        <div class="wc-title">{{ w.questionTitle || '(题目已删除)' }}</div>
        <div class="wc-time">{{ w.lastWrongTime }}</div>
      </div>
    </div>
    <el-empty v-else description="暂无错题，继续加油！" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"; import request from "@/api/request"; import { ElMessage } from "element-plus";
const list = ref([]);
onMounted(fetch);
async function fetch() { try { const r = await request.get("/student/analytics/wrong-questions"); list.value = r.data?.records || []; } catch { list.value = []; } }
async function toggleMastered(row) { try { await request.put(`/student/analytics/wrong-questions/${row.questionId}/mastered`, { masteredStatus: row.masteredStatus === 1 ? 0 : 1 }); fetch(); } catch { ElMessage.error("更新失败"); } }
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
.page-top{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-top h2{margin:0;font-size:22px;font-weight:700}
.count{font-size:14px;color:#8b92a8}
.wrong-cards{display:flex;flex-direction:column;gap:12px}
.wrong-card{padding:16px 20px;background:#fff;border:1px solid #eef1f6;border-radius:14px}
.wrong-card.mastered{opacity:.6;background:#f8fafb}
.wc-header{display:flex;align-items:center;gap:12px;margin-bottom:8px}
.wc-id{font-size:12px;color:#b0b7c6}
.wc-count{font-size:12px;color:#e6a23c;font-weight:600}
.wc-title{font-size:15px;color:#1a1a2e;margin-bottom:4px}
.wc-time{font-size:11px;color:#c0c4cc}
</style>
