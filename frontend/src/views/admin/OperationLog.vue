<template>
  <div class="page-panel">
    <h2>系统日志</h2>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索账号/姓名/操作/URI" clearable style="width:280px" @clear="fetch" @keyup.enter="fetch" />
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width:120px" @change="fetch">
        <el-option label="成功" :value="1" />
        <el-option label="失败" :value="0" />
      </el-select>
      <el-button @click="fetch">搜索</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="realName" label="操作人" width="100">
        <template #default="{ row }">{{ row.realName || row.username || '-' }}</template>
      </el-table-column>
      <el-table-column prop="operation" label="操作" min-width="180" show-overflow-tooltip />
      <el-table-column prop="method" label="方法" width="80" />
      <el-table-column prop="uri" label="请求路径" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="costTime" label="耗时" width="80">
        <template #default="{ row }">{{ row.costTime }}ms</template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170">
        <template #default="{ row }">{{ row.createTime?.replace('T', ' ') }}</template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="20" layout="total,prev,pager,next" @current-change="fetch" style="margin-top:16px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getLogs } from "@/api/system";

const list = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const total = ref(0);
const keyword = ref("");
const filterStatus = ref(null);

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize: 20 };
    if (keyword.value) params.keyword = keyword.value;
    if (filterStatus.value !== null && filterStatus.value !== "") params.status = filterStatus.value;
    const res = await getLogs(params);
    list.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } finally { loading.value = false; }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
