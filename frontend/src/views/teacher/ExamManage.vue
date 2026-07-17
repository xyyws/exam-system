<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <h2>考试管理</h2>
        <span class="total-count">共 {{ list.length }} 场考试</span>
      </div>
      <el-button type="primary" class="btn-create" @click="dialogVisible = true">
        <el-icon><Plus /></el-icon>
        创建考试
      </el-button>
    </div>

    <div class="exam-grid">
      <div v-for="exam in list" :key="exam.id" class="exam-card">
        <div class="exam-card__header">
          <h3>{{ exam.examName }}</h3>
          <el-tag :type="statusType(exam.publishStatus)" size="small">
            {{ statusText(exam.publishStatus) }}
          </el-tag>
        </div>
        <div class="exam-card__body">
          <div class="exam-info">
            <div class="info-item">
              <el-icon><Timer /></el-icon>
              <span>{{ exam.durationMinutes }}分钟</span>
            </div>
            <div class="info-item">
              <el-icon><Document /></el-icon>
              <span>{{ exam.totalScore }}分</span>
            </div>
            <div class="info-item">
              <el-icon><Aim /></el-icon>
              <span>及格{{ exam.passScore }}分</span>
            </div>
            <el-tag v-if="exam.examMode === 2" type="warning" size="small" style="margin-left:8px">题序乱</el-tag>
            <el-tag v-else-if="exam.examMode === 3" type="warning" size="small" style="margin-left:8px">选项乱</el-tag>
          </div>
          <div class="exam-time">
            <span>{{ exam.startTime?.substring(0, 16) }}</span>
            <span class="time-sep">~</span>
            <span>{{ exam.endTime?.substring(0, 16) }}</span>
          </div>
        </div>
        <div class="exam-card__footer">
          <el-button link type="primary" @click="openAssign(exam)">分配学生</el-button>
          <el-button v-if="exam.publishStatus <= 2" link @click="openEdit(exam)">编辑</el-button>
          <el-button v-if="exam.publishStatus === 0" link type="success" @click="publish(exam.id)">发布</el-button>
          <el-button v-if="exam.publishStatus === 1" link type="warning" @click="closeExamFn(exam.id)">结束</el-button>
          <el-button v-if="exam.publishStatus >= 2" link type="success" @click="openEdit(exam)">重新发布</el-button>
        </div>
      </div>
    </div>

    <div v-if="!loading && list.length === 0" class="empty-state">
      <el-icon :size="64" color="#ddd"><Calendar /></el-icon>
      <p>暂无考试数据</p>
    </div>

    <!-- 创建考试对话框 -->
    <el-dialog v-model="dialogVisible" title="创建考试" width="550px" class="custom-dialog">
      <el-form :model="form" label-width="100px">
        <el-form-item label="考试名称"><el-input v-model="form.examName" placeholder="请输入考试名称" /></el-form-item>
        <el-form-item label="试卷ID"><el-input-number v-model="form.paperId" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width:100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width:100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="form.durationMinutes" :min="10" style="width: 100%" /></el-form-item>
        <el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="及格分"><el-input-number v-model="form.passScore" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="试卷模式">
          <el-radio-group v-model="form.examMode">
            <el-radio :value="1">固定试卷</el-radio>
            <el-radio :value="2">题目乱序</el-radio>
            <el-radio :value="3">选项乱序</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="防作弊">
          <el-switch v-model="antiCheat" @change="form.antiCheatSwitch = antiCheat ? 1 : 0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>

    <!-- 编辑考试对话框 -->
    <el-dialog v-model="editVisible" title="编辑考试" width="550px" class="custom-dialog">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="考试名称"><el-input v-model="editForm.examName" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="editForm.startTime" type="datetime" style="width:100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="editForm.endTime" type="datetime" style="width:100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="editForm.durationMinutes" :min="10" style="width:100%" /></el-form-item>
        <el-form-item label="总分"><el-input-number v-model="editForm.totalScore" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="及格分"><el-input-number v-model="editForm.passScore" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="试卷模式">
          <el-radio-group v-model="editForm.examMode">
            <el-radio :value="1">固定试卷</el-radio>
            <el-radio :value="2">题目乱序</el-radio>
            <el-radio :value="3">选项乱序</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit(false)">保存</el-button>
        <el-button v-if="editingStatus >= 2" type="success" :loading="editLoading" @click="saveEdit(true)">保存并发布</el-button>
      </template>
    </el-dialog>

    <!-- 分配学生对话框 -->
    <el-dialog v-model="assignVisible" title="分配学生" width="700px" class="custom-dialog">
      <div class="assign-header">
        <el-select v-model="selectedClassId" placeholder="选择班级" clearable style="width:220px" @change="onClassChange">
          <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
        </el-select>
        <el-button type="primary" size="small" :disabled="!studentList.length" @click="selectAll">全选本班</el-button>
        <el-button size="small" @click="selectedStudents = []">清空</el-button>
        <span class="selected-count">已选 {{ selectedStudents.length }} 名学生</span>
      </div>
      <el-table ref="assignTableRef" :data="studentList" stripe size="small" max-height="400" @selection-change="handleSelectionChange" v-loading="studentLoading">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="140" />
        <el-table-column prop="className" label="班级" />
      </el-table>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="doAssign">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Plus, Timer, Document, Aim, Calendar } from "@element-plus/icons-vue";
import { getExams, createExam, updateExam, publishExam, closeExam as apiCloseExam, assignStudents, getAssignedStudents, getExamStudents, getTeacherClasses } from "@/api/exam";
import { ElMessage } from "element-plus";

const route = useRoute();
const list = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const antiCheat = ref(false);
const form = reactive({
  examName: "", paperId: null, startTime: "", endTime: "", durationMinutes: 60,
  totalScore: 100, passScore: 60, examMode: 1, antiCheatSwitch: 0, cutScreenLimit: 3,
  autoSubmitSwitch: 1, allowRepeatEnter: 1
});

const editVisible = ref(false);
const editLoading = ref(false);
const editingId = ref(null);
const editingStatus = ref(0);
const editForm = reactive({
  examName: "", startTime: "", endTime: "", durationMinutes: 60,
  totalScore: 100, passScore: 60, examMode: 1
});

const assignVisible = ref(false);
const assignLoading = ref(false);
const currentExamId = ref(null);
const studentList = ref([]);
const selectedStudents = ref([]);
const classList = ref([]);
const selectedClassId = ref(null);
const studentLoading = ref(false);
const assignTableRef = ref(null);

onMounted(() => {
  fetch();
  const paperId = route.query.paperId;
  if (paperId) {
    form.paperId = Number(paperId);
    dialogVisible.value = true;
  }
});

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

function openEdit(exam) {
  editingId.value = exam.id;
  editingStatus.value = exam.publishStatus;
  editForm.examName = exam.examName;
  editForm.startTime = exam.startTime;
  editForm.endTime = exam.endTime;
  editForm.durationMinutes = exam.durationMinutes;
  editForm.totalScore = exam.totalScore;
  editForm.passScore = exam.passScore;
  editForm.examMode = exam.examMode || 1;
  editVisible.value = true;
}

async function saveEdit(andPublish = false) {
  editLoading.value = true;
  try {
    await updateExam(editingId.value, { ...editForm });
    if (andPublish) {
      await publish(editingId.value);
      ElMessage.success("保存并发布成功");
    } else {
      ElMessage.success("保存成功");
    }
    editVisible.value = false;
    fetch();
  } catch { ElMessage.error("保存失败"); }
  finally { editLoading.value = false; }
}

async function openAssign(exam) {
  currentExamId.value = exam.id;
  selectedStudents.value = [];
  selectedClassId.value = null;
  studentList.value = [];
  assignVisible.value = true;
  try {
    const classRes = await getTeacherClasses();
    classList.value = classRes.data?.records || [];
  } catch {
    ElMessage.error("加载班级列表失败");
  }
}

async function onClassChange(classId) {
  if (!classId) { studentList.value = []; return; }
  studentLoading.value = true;
  try {
    const res = await getExamStudents({ classId });
    studentList.value = res.data?.records || [];
  } catch {
    ElMessage.error("加载学生列表失败");
  } finally {
    studentLoading.value = false;
  }
}

function handleSelectionChange(rows) { selectedStudents.value = rows; }
function selectAll() {
  if (!assignTableRef.value) return;
  studentList.value.forEach(row => assignTableRef.value.toggleRowSelection(row, true));
}

async function doAssign() {
  if (!selectedStudents.value.length) {
    ElMessage.warning("请至少选择一名学生");
    return;
  }
  assignLoading.value = true;
  try {
    const studentIds = selectedStudents.value.map(s => s.id);
    const res = await assignStudents(currentExamId.value, studentIds);
    const { added, skipped } = res.data;
    ElMessage.success(`分配完成：新增 ${added} 人，跳过 ${skipped} 人`);
    assignVisible.value = false;
  } catch {
    ElMessage.error("分配失败");
  } finally {
    assignLoading.value = false;
  }
}

function statusText(s) { return { 0: "草稿", 1: "已发布", 2: "已结束", 3: "已归档" }[s] || "未知"; }
function statusType(s) { return { 0: "info", 1: "success", 2: "warning", 3: "info" }[s] || "info"; }
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

.header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
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

.btn-create {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.exam-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.exam-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.exam-card__header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
  margin-right: 12px;
}

.exam-card__body {
  margin-bottom: 16px;
}

.exam-info {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.exam-time {
  font-size: 13px;
  color: #999;
}

.time-sep {
  margin: 0 8px;
}

.exam-card__footer {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.assign-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.selected-count {
  margin-left: 12px;
  color: #999;
  font-size: 14px;
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
  .exam-grid {
    grid-template-columns: 1fr;
  }
}
</style>
