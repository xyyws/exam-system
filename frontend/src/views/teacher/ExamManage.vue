<template>
  <div class="page-panel">
    <div class="page-header">
      <h2>考试管理</h2>
      <el-button type="primary" @click="dialogVisible = true">创建考试</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="50" />
      <el-table-column prop="examName" label="考试名称" />
      <el-table-column prop="durationMinutes" label="时长(分)" width="70" />
      <el-table-column prop="totalScore" label="总分" width="60" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">{{ {0:'草稿',1:'已发布',2:'已结束',3:'已归档'}[row.publishStatus] }}</template>
      </el-table-column>
      <el-table-column label="时间" width="200">
        <template #default="{ row }">{{ row.startTime?.substring(0,16) }} ~ {{ row.endTime?.substring(11,16) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="row.publishStatus===0" link type="success" @click="publish(row.id)">发布</el-button>
          <el-button v-if="row.publishStatus===1" link type="warning" @click="closeExamFn(row.id)">结束</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="创建考试" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="考试名称"><el-input v-model="form.examName" /></el-form-item>
        <el-form-item label="试卷ID"><el-input-number v-model="form.paperId" :min="1" /></el-form-item>
        <el-form-item label="开始时间"><el-input v-model="form.startTime" placeholder="2026-05-01T09:00:00" /></el-form-item>
        <el-form-item label="结束时间"><el-input v-model="form.endTime" placeholder="2026-05-01T11:00:00" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="form.durationMinutes" :min="10" /></el-form-item>
        <el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="1" /></el-form-item>
        <el-form-item label="及格分"><el-input-number v-model="form.passScore" :min="0" /></el-form-item>
        <el-form-item label="防作弊">
          <el-switch v-model="antiCheat" @change="form.antiCheatSwitch = antiCheat ? 1 : 0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getExams, createExam, publishExam, closeExam as apiCloseExam } from "@/api/exam";
import { ElMessage } from "element-plus";

const list = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const antiCheat = ref(false);
const form = reactive({
  examName: "", paperId: null, startTime: "", endTime: "", durationMinutes: 60,
  totalScore: 100, passScore: 60, antiCheatSwitch: 0, cutScreenLimit: 3,
  autoSubmitSwitch: 1, allowRepeatEnter: 1
});

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const res = await getExams();
    list.value = res.data.records || [];
  } finally { loading.value = false; }
}

async function create() {
  try {
    await createExam(form);
    dialogVisible.value = false;
    fetch();
    ElMessage.success("创建成功");
  } catch { ElMessage.error("创建失败"); }
}

async function publish(id) { await publishExam(id); fetch(); ElMessage.success("已发布"); }
async function closeExamFn(id) { await apiCloseExam(id); fetch(); ElMessage.success("已结束"); }
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
</style>
