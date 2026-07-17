<template>
  <div class="page-panel">
    <h2>手动组卷</h2>
    <el-steps :active="step" finish-status="success" align-center style="margin: 20px 0 30px">
      <el-step title="基础信息" /><el-step title="挑选题目" /><el-step title="确认" />
    </el-steps>

    <!-- Step 1: 基础信息 -->
    <div v-if="step === 0" style="max-width: 500px">
      <el-form :model="meta" label-width="100px">
        <el-form-item label="试卷名称"><el-input v-model="meta.paperName" /></el-form-item>
        <el-form-item label="考试时长"><el-input-number v-model="meta.durationMinutes" :min="10" /> 分钟</el-form-item>
        <el-form-item label="备注"><el-input v-model="meta.remark" /></el-form-item>
      </el-form>
      <el-button type="primary" @click="step = 1">下一步</el-button>
    </div>

    <!-- Step 2: 挑选题目 -->
    <div v-if="step === 1">
      <div class="toolbar">
        <el-input v-model="searchKw" placeholder="搜索题干" clearable style="width:200px" @keyup.enter="loadBank" />
        <el-select v-model="searchType" placeholder="题型" clearable style="width:120px" @change="loadBank">
          <el-option label="单选" :value="1" /><el-option label="多选" :value="2" />
          <el-option label="判断" :value="3" /><el-option label="填空" :value="4" /><el-option label="简答" :value="5" />
        </el-select>
        <el-button @click="loadBank">搜索</el-button>
        <span class="picked-count">已选 {{ picked.length }} 题，共 {{ totalScore }} 分</span>
      </div>

      <el-table :data="bank" stripe v-loading="bankLoading" size="small" style="margin-bottom: 12px">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox :model-value="isPicked(row.id)" @change="togglePick(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="题干" show-overflow-tooltip />
        <el-table-column label="题型" width="70">
          <template #default="{ row }">{{ typeMap[row.questionType] }}</template>
        </el-table-column>
        <el-table-column prop="score" label="默认分值" width="80" />
      </el-table>
      <el-pagination v-model:current-page="bankPage" :total="bankTotal" :page-size="10" layout="prev,pager,next" @current-change="loadBank" />

      <div v-if="picked.length" style="margin-top: 20px">
        <h4>已选题目</h4>
        <el-table :data="picked" stripe size="small">
          <el-table-column type="index" label="#" width="40" />
          <el-table-column prop="title" label="题干" show-overflow-tooltip />
          <el-table-column label="题型" width="70">
            <template #default="{ row }">{{ typeMap[row.questionType] }}</template>
          </el-table-column>
          <el-table-column label="分值" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row._score" :min="0" :step="0.5" size="small" style="width:90px" />
            </template>
          </el-table-column>
          <el-table-column width="60">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="removePick(row.id)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div style="margin-top: 16px">
        <el-button @click="step = 0">上一步</el-button>
        <el-button type="primary" :disabled="!picked.length" @click="step = 2">下一步</el-button>
      </div>
    </div>

    <!-- Step 3: 确认 -->
    <div v-if="step === 2" style="max-width: 600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="试卷名称">{{ meta.paperName }}</el-descriptions-item>
        <el-descriptions-item label="题目数量">{{ picked.length }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ totalScore }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ meta.durationMinutes }} 分钟</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 20px">
        <el-button @click="step = 1">返回修改</el-button>
        <el-button type="success" :loading="submitting" @click="submit">确认生成试卷</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getQuestions } from "@/api/question";
import { createManualPaper } from "@/api/paper";
import { ElMessage } from "element-plus";

const router = useRouter();
const step = ref(0);
const meta = reactive({ paperName: "", durationMinutes: 60, remark: "" });
const typeMap = { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" };

// 题库浏览
const bank = ref([]);
const bankLoading = ref(false);
const bankPage = ref(1);
const bankTotal = ref(0);
const searchKw = ref("");
const searchType = ref(null);

// 已选题目
const picked = ref([]);
const submitting = ref(false);

const totalScore = computed(() => picked.value.reduce((s, q) => s + (q._score || 0), 0));

onMounted(loadBank);

async function loadBank() {
  bankLoading.value = true;
  try {
    const params = { pageNum: bankPage.value, pageSize: 10 };
    if (searchKw.value) params.keyword = searchKw.value;
    if (searchType.value) params.questionType = searchType.value;
    const res = await getQuestions(params);
    bank.value = res.data?.records || [];
    bankTotal.value = res.data?.total || 0;
  } finally { bankLoading.value = false; }
}

function isPicked(id) { return picked.value.some(q => q.id === id); }

function togglePick(row) {
  if (isPicked(row.id)) { removePick(row.id); }
  else { picked.value.push({ ...row, _score: row.score || 5 }); }
}

function removePick(id) { picked.value = picked.value.filter(q => q.id !== id); }

async function submit() {
  if (!meta.paperName) { ElMessage.warning("请输入试卷名称"); step.value = 0; return; }
  submitting.value = true;
  try {
    const questionItems = picked.value.map((q, i) => ({
      questionId: q.id, score: q._score, questionOrder: i + 1
    }));
    await createManualPaper({ paperName: meta.paperName, durationMinutes: meta.durationMinutes, remark: meta.remark, questionItems });
    ElMessage.success("试卷创建成功");
    router.push("/teacher/papers");
  } catch { ElMessage.error("创建失败"); }
  finally { submitting.value = false; }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
.toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.picked-count { margin-left: auto; font-size: 14px; color: #409eff; font-weight: 600; }
</style>
