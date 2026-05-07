<template>
  <div class="page-panel">
    <div class="page-header">
      <h2>题库管理</h2>
      <div>
        <el-button @click="importDialogVisible = true">批量导入</el-button>
        <el-button type="primary" @click="openDialog()">新增题目</el-button>
      </div>
    </div>

    <el-form :inline="true" class="search-bar">
      <el-form-item><el-input v-model="params.keyword" placeholder="搜索题干" clearable @change="fetch" /></el-form-item>
      <el-form-item>
        <el-select v-model="params.questionType" placeholder="题型" clearable @change="fetch">
          <el-option label="单选题" :value="1" /><el-option label="多选题" :value="2" />
          <el-option label="判断题" :value="3" /><el-option label="填空题" :value="4" /><el-option label="简答题" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="params.difficulty" placeholder="难度" clearable @change="fetch">
          <el-option label="简单" :value="1" /><el-option label="中等" :value="3" /><el-option label="困难" :value="5" />
        </el-select>
      </el-form-item>
    </el-form>

    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="题干" show-overflow-tooltip />
      <el-table-column prop="questionType" label="题型" width="80">
        <template #default="{ row }">{{ typeMap[row.questionType] }}</template>
      </el-table-column>
      <el-table-column prop="difficulty" label="难度" width="60">
        <template #default="{ row }">{{ diffMap[row.difficulty] }}</template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="60" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="params.pageNum" :total="total" :page-size="10" layout="prev,pager,next" @current-change="fetch" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="650px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="题型">
          <el-select v-model="form.questionType"><el-option label="单选题" :value="1" /><el-option label="多选题" :value="2" /><el-option label="判断题" :value="3" /><el-option label="填空题" :value="4" /><el-option label="简答题" :value="5" /></el-select>
        </el-form-item>
        <el-form-item label="题干"><el-input v-model="form.title" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="难度"><el-select v-model="form.difficulty"><el-option label="简单" :value="1" /><el-option label="中等" :value="3" /><el-option label="困难" :value="5" /></el-select></el-form-item>
        <el-form-item label="分值"><el-input-number v-model="form.score" :min="0" /></el-form-item>
        <el-form-item label="正确答案"><el-input v-model="form.correctAnswer" placeholder="单选A/多选A,B/判断T/填空xxx" /></el-form-item>
        <el-form-item label="解析"><el-input v-model="form.answerAnalysis" type="textarea" :rows="2" /></el-form-item>

        <!-- Options for choice/judgment -->
        <template v-if="form.questionType <= 3">
          <el-form-item v-for="(opt, i) in form.options" :key="i" :label="'选项' + opt.optionLabel">
            <el-input v-model="opt.optionContent" :placeholder="'选项' + opt.optionLabel + '内容'" style="width: 80%" />
            <el-checkbox v-model="opt.isCorrect" style="margin-left: 8px">正确</el-checkbox>
          </el-form-item>
          <el-button v-if="form.questionType < 3" size="small" @click="addOption" style="margin-left: 80px">+ 添加选项</el-button>
        </template>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入题目" width="550px" @close="resetImport">
      <div v-if="!importResult">
        <div style="margin-bottom: 16px;">
          <el-button type="primary" link @click="downloadTemplate">
            <el-icon><Download /></el-icon> 下载导入模板
          </el-button>
          <span style="color: #909399; font-size: 12px; margin-left: 8px">请按照模板格式填写数据后上传</span>
        </div>
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="onFileChange"
          :on-exceed="() => ElMessage.warning('只能上传一个文件')"
        >
          <el-icon style="font-size: 40px; color: #c0c4cc"><UploadFilled /></el-icon>
          <div style="color: #606266">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div style="color: #909399; font-size: 12px">仅支持 .xlsx / .xls 格式文件</div>
          </template>
        </el-upload>
      </div>
      <!-- 导入结果 -->
      <div v-else>
        <el-result
          :icon="importResult.failCount === 0 ? 'success' : 'warning'"
          :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failCount} 条`"
        />
        <div v-if="importResult.errors && importResult.errors.length > 0" style="max-height: 200px; overflow-y: auto; padding: 0 20px">
          <el-alert
            v-for="(err, i) in importResult.errors"
            :key="i"
            :title="err"
            type="error"
            :closable="false"
            style="margin-bottom: 8px"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">{{ importResult ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!importResult" type="primary" :loading="importLoading" :disabled="!importFile" @click="doImport">
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getQuestions, createQuestion, updateQuestion, deleteQuestion, importQuestions } from "@/api/question";
import { ElMessage } from "element-plus";
import { Download, UploadFilled } from "@element-plus/icons-vue";

const list = ref([]);
const loading = ref(false);
const total = ref(0);
const params = reactive({ pageNum: 1, pageSize: 10, keyword: "", questionType: null, difficulty: null });
const dialogVisible = ref(false);
const form = reactive({ id: null, questionType: 1, title: "", difficulty: 3, score: 0, correctAnswer: "", answerAnalysis: "", options: [] });
const typeMap = { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" };
const diffMap = { 1: "简单", 2: "较易", 3: "中等", 4: "较难", 5: "困难" };

// ─── 批量导入 ───
const importDialogVisible = ref(false);
const importLoading = ref(false);
const importFile = ref(null);
const importResult = ref(null);
const uploadRef = ref(null);

function onFileChange(file) {
  importFile.value = file.raw;
}

async function downloadTemplate() {
  try {
    const token = localStorage.getItem("accessToken");
    const res = await fetch("http://localhost:8080/api/teacher/questions/import/template", {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error("下载失败");
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "题目导入模板.xlsx";
    a.click();
    URL.revokeObjectURL(url);
  } catch { ElMessage.error("模板下载失败"); }
}

async function doImport() {
  if (!importFile.value) return;
  importLoading.value = true;
  try {
    const formData = new FormData();
    formData.append("file", importFile.value);
    const res = await importQuestions(formData);
    importResult.value = res.data;
    fetch();
  } catch { ElMessage.error("导入失败"); }
  finally { importLoading.value = false; }
}

function resetImport() {
  importFile.value = null;
  importResult.value = null;
  if (uploadRef.value) uploadRef.value.clearFiles();
}

function initOptions(type) {
  if (type === 1) return buildOptions(4);
  if (type === 2) return buildOptions(4);
  if (type === 3) return buildOptions(2);
  return [];
}

function buildOptions(n) {
  return Array.from({ length: n }, (_, i) => ({
    optionLabel: String.fromCharCode(65 + i),
    optionContent: "",
    isCorrect: false
  }));
}

function addOption() {
  const next = String.fromCharCode(65 + form.options.length);
  form.options.push({ optionLabel: next, optionContent: "", isCorrect: false });
}

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const res = await getQuestions(params);
    list.value = res.data.records;
    total.value = res.data.total;
  } finally { loading.value = false; }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, row);
    form.options = form.options || initOptions(row.questionType);
  } else {
    Object.assign(form, { id: null, questionType: 1, title: "", difficulty: 3, score: 0, correctAnswer: "", answerAnalysis: "" });
    form.options = initOptions(1);
  }
  dialogVisible.value = true;
}

async function save() {
  const data = { ...form };
  // Convert isCorrect to integer
  (data.options || []).forEach(o => o.isCorrect = o.isCorrect ? 1 : 0);
  try {
    if (data.id) { await updateQuestion(data.id, data); }
    else { await createQuestion(data); }
    dialogVisible.value = false;
    fetch();
    ElMessage.success("保存成功");
  } catch { ElMessage.error("保存失败"); }
}

async function handleDelete(id) {
  await deleteQuestion(id);
  fetch();
  ElMessage.success("已删除");
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
.search-bar { margin-bottom: 16px; }
</style>
