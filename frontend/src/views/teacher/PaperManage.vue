<template>
  <div class="page-panel">
    <div class="page-header">
      <h2>试卷管理</h2>
      <el-button type="primary" @click="$router.push('/teacher/paper-auto')">自动组卷</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="paperName" label="试卷名称" />
      <el-table-column prop="questionCount" label="题数" width="60" />
      <el-table-column prop="totalScore" label="总分" width="60" />
      <el-table-column prop="durationMinutes" label="时长(分)" width="80" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">{{ row.paperType === 1 ? '手动' : '自动' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/teacher/exams/create?paperId=${row.id}`)">创建考试</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="10" layout="prev,pager,next" @current-change="fetch" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getPapers, deletePaper } from "@/api/paper";
import { ElMessage } from "element-plus";

const list = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const total = ref(0);

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const res = await getPapers({ pageNum: pageNum.value, pageSize: 10 });
    list.value = res.data.records || [];
    total.value = res.data.total || 0;
  } finally { loading.value = false; }
}

async function handleDelete(id) {
  await deletePaper(id);
  fetch();
  ElMessage.success("已删除");
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
</style>
