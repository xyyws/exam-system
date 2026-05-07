<template>
  <div class="page-panel">
    <h2>考试监控</h2>

    <div class="selector-row">
      <el-select v-model="selectedExamId" placeholder="选择考试" @change="load" style="width:300px" clearable>
        <el-option v-for="e in exams" :key="e.id" :label="e.examName" :value="e.id" />
      </el-select>
    </div>

    <div v-if="selectedExamId && list.length" class="stats-row">
      <div class="mini-stat"><span class="ms-label">总人数</span><span class="ms-value">{{ list.length }}</span></div>
      <div class="mini-stat"><span class="ms-label">进行中</span><span class="ms-value accent">{{ list.filter(r => r.answer_status === 1).length }}</span></div>
      <div class="mini-stat"><span class="ms-label">已交卷</span><span class="ms-value">{{ list.filter(r => r.answer_status >= 2).length }}</span></div>
      <div class="mini-stat"><span class="ms-label">违规</span><span class="ms-value danger">{{ list.filter(r => r.violation_count > 0).length }}</span></div>
    </div>

    <el-table v-if="selectedExamId" :data="list" stripe v-loading="loading">
      <el-table-column prop="student_name" label="学生" width="100" />
      <el-table-column prop="student_no" label="学号" width="110" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType(row.answer_status)" size="small">{{ statusLabel(row.answer_status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="used_minutes" label="用时(分)" width="80" />
      <el-table-column prop="cut_screen_count" label="切屏" width="60" />
      <el-table-column prop="violation_count" label="违规" width="60">
        <template #default="{ row }">
          <span :class="{ danger: row.violation_count > 0 }">{{ row.violation_count }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="total_score" label="得分" width="70" />
    </el-table>

    <el-empty v-if="selectedExamId && !list.length && !loading" description="暂无考试数据" />
    <el-empty v-if="!selectedExamId" description="请选择一场考试查看监控" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getExams } from "@/api/exam";
import request from "@/api/request";

const exams = ref([]);
const selectedExamId = ref(null);
const list = ref([]);
const loading = ref(false);

onMounted(async () => {
  try {
    const res = await getExams({ pageSize: 100 });
    exams.value = res.data.records || [];
  } catch { /* silent */ }
});

async function load() {
  if (!selectedExamId.value) { list.value = []; return; }
  loading.value = true;
  try {
    const res = await request.get(`/teacher/exams/${selectedExamId.value}/monitor`);
    list.value = res.data?.records || [];
  } finally { loading.value = false; }
}

function statusLabel(s) { return { 0: '未开始', 1: '进行中', 2: '已交卷', 3: '已判分' }[s] || '未知'; }
function statusType(s) { return { 0: 'info', 1: 'success', 2: 'warning', 3: '' }[s] || 'info'; }
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
.selector-row { margin-bottom: 16px; }
.stats-row { display: flex; gap: 16px; margin-bottom: 16px; }
.mini-stat { padding: 12px 20px; border: 1px solid #ebeef5; border-radius: 10px; text-align: center; }
.ms-label { font-size: 12px; color: #909399; display: block; }
.ms-value { font-size: 22px; font-weight: 700; color: #303133; }
.ms-value.accent { color: #67c23a; }
.ms-value.danger { color: #f56c6c; }
.danger { color: #f56c6c; font-weight: 600; }
</style>
