<template>
  <div class="log-page">
    <div class="page-header">
      <h2>系统日志</h2>
      <div class="header-stats">
        <div class="stat-chip success">
          <el-icon><CircleCheck /></el-icon>
          <span>成功 {{ successCount }}</span>
        </div>
        <div class="stat-chip danger">
          <el-icon><CircleClose /></el-icon>
          <span>失败 {{ failCount }}</span>
        </div>
        <div class="stat-chip info">
          <el-icon><Timer /></el-icon>
          <span>平均 {{ avgCostTime }}ms</span>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索操作人 / 操作 / URI" clearable
        style="width:280px" @clear="fetch" @keyup.enter="fetch" prefix-icon="Search" />
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width:120px" @change="fetch">
        <el-option label="成功" :value="1" /><el-option label="失败" :value="0" />
      </el-select>
      <el-select v-model="filterMethod" placeholder="请求方法" clearable style="width:120px" @change="fetch">
        <el-option label="GET" value="GET" /><el-option label="POST" value="POST" />
        <el-option label="PUT" value="PUT" /><el-option label="DELETE" value="DELETE" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="~"
        start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
        style="width:260px" @change="fetch" clearable />
      <el-button @click="resetFilters">重置</el-button>
    </div>

    <el-table :data="list" stripe v-loading="loading" highlight-current-row
      :row-class-name="rowClassName" @row-click="showDetail" style="cursor:pointer">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="操作人" width="110">
        <template #default="{ row }">
          <div class="user-cell">
            <span class="user-avatar">{{ (row.realName || row.username || '?')[0] }}</span>
            <span>{{ row.realName || row.username || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="operation" label="操作" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span class="op-text">{{ formatOperation(row.operation) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请求" min-width="120">
        <template #default="{ row }">
          <el-tag :type="methodTag(row.method)" size="small" effect="plain">{{ row.method }}</el-tag>
          <span class="uri-text">{{ simplifyUri(row.uri) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="dark" round>
            {{ row.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时" width="90" align="right" sortable :sort-method="sortCost">
        <template #default="{ row }">
          <span :class="['cost-text', costClass(row.costTime)]">{{ row.costTime }}ms</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" sortable>
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="20"
      layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50, 100]"
      @current-change="fetch" @size-change="handleSizeChange" style="margin-top:16px" />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="580px" destroy-on-close>
      <template v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detail.realName || detail.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">
            <el-tag :type="methodTag(detail.method)" size="small">{{ detail.method }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detail.status === 1 ? 'success' : 'danger'" size="small">
              {{ detail.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作" :span="2">{{ detail.operation }}</el-descriptions-item>
          <el-descriptions-item label="请求路径" :span="2">
            <code class="uri-code">{{ detail.uri }}</code>
          </el-descriptions-item>
          <el-descriptions-item label="IP">{{ detail.ip }}</el-descriptions-item>
          <el-descriptions-item label="耗时">
            <span :class="['cost-text', costClass(detail.costTime)]">{{ detail.costTime }}ms</span>
          </el-descriptions-item>
          <el-descriptions-item label="时间" :span="2">{{ formatTime(detail.createTime) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.errorMsg" class="error-box">
          <div class="error-title">错误信息</div>
          <pre class="error-msg">{{ detail.errorMsg }}</pre>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { getLogs } from "@/api/system";
import { CircleCheck, CircleClose, Timer } from "@element-plus/icons-vue";

const list = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(20);
const total = ref(0);
const keyword = ref("");
const filterStatus = ref(null);
const filterMethod = ref(null);
const dateRange = ref(null);
const detailVisible = ref(false);
const detail = ref(null);

const successCount = computed(() => list.value.filter(r => r.status === 1).length);
const failCount = computed(() => list.value.filter(r => r.status === 0).length);
const avgCostTime = computed(() => {
  if (!list.value.length) return 0;
  const sum = list.value.reduce((s, r) => s + (r.costTime || 0), 0);
  return Math.round(sum / list.value.length);
});

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value };
    if (keyword.value) params.keyword = keyword.value;
    if (filterStatus.value !== null && filterStatus.value !== "") params.status = filterStatus.value;
    const res = await getLogs(params);
    list.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } finally { loading.value = false; }
}

function resetFilters() {
  keyword.value = "";
  filterStatus.value = null;
  filterMethod.value = null;
  dateRange.value = null;
  pageNum.value = 1;
  fetch();
}

function handleSizeChange(val) { pageSize.value = val; pageNum.value = 1; fetch(); }

function showDetail(row) { detail.value = row; detailVisible.value = true; }

function rowClassName({ row }) { return row.status === 0 ? "row-fail" : ""; }

function formatTime(t) { return t ? t.replace("T", " ").substring(0, 19) : "-"; }

function formatOperation(op) {
  if (!op) return "-";
  // "UserController.create" -> "UserController - create"
  const parts = op.split(".");
  if (parts.length === 2) {
    const classMap = { UserController: "用户管理", RoleController: "角色管理", ClassController: "班级管理",
      AdminStatsController: "数据统计", AdminLogController: "系统日志", AuthController: "认证" };
    return (classMap[parts[0]] || parts[0]) + " - " + parts[1];
  }
  return op;
}

function simplifyUri(uri) {
  if (!uri) return "";
  return uri.replace("/api/admin/", "").replace("/api/", "");
}

function methodTag(m) { return { GET: "info", POST: "success", PUT: "warning", DELETE: "danger" }[m] || "info"; }

function costClass(ms) {
  if (ms > 1000) return "cost-slow";
  if (ms > 500) return "cost-warn";
  return "cost-fast";
}

function sortCost(a, b) { return (a.costTime || 0) - (b.costTime || 0); }
</script>

<style scoped>
.log-page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 22px; font-weight: 700; }
.header-stats { display: flex; gap: 12px; }
.stat-chip { display: flex; align-items: center; gap: 6px; padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 600; }
.stat-chip.success { background: #f0f9eb; color: #67c23a; }
.stat-chip.danger { background: #fef0f0; color: #f56c6c; }
.stat-chip.info { background: #f4f4f5; color: #909399; }

.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }

.user-cell { display: flex; align-items: center; gap: 8px; }
.user-avatar { width: 28px; height: 28px; border-radius: 50%; background: #e8ecf1; color: #606266; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; flex-shrink: 0; }

.op-text { font-weight: 500; }
.uri-text { font-size: 12px; color: #909399; margin-left: 6px; }

.cost-text { font-variant-numeric: tabular-nums; font-weight: 600; font-size: 13px; }
.cost-fast { color: #67c23a; }
.cost-warn { color: #e6a23c; }
.cost-slow { color: #f56c6c; }

:deep(.row-fail) { background: #fff8f8 !important; }
:deep(.row-fail:hover td) { background: #fff0f0 !important; }

.uri-code { font-size: 12px; background: #f5f7fa; padding: 2px 8px; border-radius: 4px; color: #409eff; }
.error-box { margin-top: 16px; border: 1px solid #fde2e2; border-radius: 8px; overflow: hidden; }
.error-title { background: #fef0f0; padding: 8px 14px; font-size: 13px; font-weight: 600; color: #f56c6c; }
.error-msg { margin: 0; padding: 12px 14px; font-size: 12px; color: #606266; background: #fafafa; max-height: 200px; overflow-y: auto; white-space: pre-wrap; word-break: break-all; }
</style>
