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
          <el-button type="primary" link @click="templateDialogVisible = true">查看导入模板格式</el-button>
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

    <!-- 模板格式说明对话框 -->
    <el-dialog v-model="templateDialogVisible" title="导入模板格式说明" width="780px">
      <el-alert type="info" :closable="false" style="margin-bottom: 16px">
        按以下格式准备Excel文件（.xlsx / .xls），第1行为表头，第2行起为数据。
      </el-alert>
      <el-table :data="templateHeaders" border size="small" style="margin-bottom: 16px">
        <el-table-column prop="col" label="列" width="50" align="center" />
        <el-table-column prop="field" label="字段名" width="100" />
        <el-table-column prop="required" label="必填" width="60" align="center">
          <template #default="{ row }">
            <el-tag :type="row.required ? 'danger' : 'info'" size="small">{{ row.required ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="说明" />
      </el-table>

      <h4 style="margin: 12px 0 8px">样例数据</h4>
      <el-table :data="templateExamples" border size="small" max-height="320">
        <el-table-column prop="type" label="题型" width="70" />
        <el-table-column prop="title" label="题干" min-width="180" show-overflow-tooltip />
        <el-table-column prop="optA" label="选项A" width="70" />
        <el-table-column prop="optB" label="选项B" width="70" />
        <el-table-column prop="optC" label="选项C" width="70" />
        <el-table-column prop="optD" label="选项D" width="70" />
        <el-table-column prop="answer" label="正确答案" width="80" />
        <el-table-column prop="score" label="分值" width="50" align="center" />
        <el-table-column prop="difficulty" label="难度" width="60" align="center" />
      </el-table>
      <template #footer><el-button type="primary" @click="templateDialogVisible = false">知道了</el-button></template>
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
const form = reactive({ id: null, questionType: 1, title: "", difficulty: 3, score: 0, correctAnswer: "", answerAnalysis: "", options: [], status: 1 });
const typeMap = { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" };
const diffMap = { 1: "简单", 2: "较易", 3: "中等", 4: "较难", 5: "困难" };

// ─── 批量导入 ───
const importDialogVisible = ref(false);
const templateDialogVisible = ref(false);
const importLoading = ref(false);
const importFile = ref(null);
const importResult = ref(null);
const uploadRef = ref(null);

const templateHeaders = [
  { col: "A", field: "题型", required: true, desc: "单选题 / 多选题 / 判断题 / 填空题 / 简答题" },
  { col: "B", field: "题干", required: true, desc: "题目内容" },
  { col: "C", field: "选项A", required: false, desc: "单选/多选必填，判断/填空/简答留空" },
  { col: "D", field: "选项B", required: false, desc: "单选/多选必填" },
  { col: "E", field: "选项C", required: false, desc: "可选" },
  { col: "F", field: "选项D", required: false, desc: "可选" },
  { col: "G", field: "正确答案", required: true, desc: "单选填字母(A)，多选用逗号分隔(A,B)，判断填T/F，填空/简答填文本" },
  { col: "H", field: "分值", required: true, desc: "大于0的数字" },
  { col: "I", field: "难度", required: false, desc: "简单 / 较易 / 中等 / 较难 / 困难，默认中等" },
  { col: "J", field: "解析", required: false, desc: "答案解析说明" }
];

const templateExamples = [
  { type: "单选题", title: "MySQL中用于创建数据库的SQL语句是？", optA: "CREATE TABLE", optB: "CREATE DATABASE", optC: "ALTER DATABASE", optD: "DROP DATABASE", answer: "B", score: 2, difficulty: "简单" },
  { type: "多选题", title: "以下哪些是Java的基本数据类型？", optA: "int", optB: "String", optC: "boolean", optD: "List", answer: "A,C", score: 3, difficulty: "中等" },
  { type: "判断题", title: "Java支持多继承", optA: "", optB: "", optC: "", optD: "", answer: "F", score: 2, difficulty: "简单" },
  { type: "填空题", title: "Java中所有类的父类是____", optA: "", optB: "", optC: "", optD: "", answer: "Object", score: 2, difficulty: "中等" },
  { type: "简答题", title: "请简述Spring IOC的概念", optA: "", optB: "", optC: "", optD: "", answer: "IOC是控制反转...", score: 5, difficulty: "困难" }
];

function onFileChange(file) {
  importFile.value = file.raw;
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
  const { id, ...data } = form;
  // Convert isCorrect to integer
  (data.options || []).forEach(o => o.isCorrect = o.isCorrect ? 1 : 0);
  try {
    if (id) { await updateQuestion(id, data); }
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
