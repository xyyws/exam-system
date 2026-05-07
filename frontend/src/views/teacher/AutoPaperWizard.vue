<template>
  <div class="page-panel">
    <h2>自动组卷</h2>
    <el-steps :active="step" finish-status="success" align-center style="margin: 20px 0 30px">
      <el-step title="基础信息" /><el-step title="抽题规则" /><el-step title="预览确认" />
    </el-steps>

    <!-- Step 1 -->
    <div v-if="step === 0" style="max-width: 500px">
      <el-form :model="meta" label-width="100px">
        <el-form-item label="试卷名称"><el-input v-model="meta.paperName" /></el-form-item>
        <el-form-item label="考试时长"><el-input-number v-model="meta.durationMinutes" :min="10" /> 分钟</el-form-item>
        <el-form-item label="备注"><el-input v-model="meta.remark" /></el-form-item>
      </el-form>
      <el-button type="primary" @click="step = 1">下一步</el-button>
    </div>

    <!-- Step 2 -->
    <div v-if="step === 1">
      <div v-for="(rule, i) in rules" :key="i" class="rule-row">
        <el-select v-model="rule.questionType" placeholder="题型" style="width:100px">
          <el-option label="单选" :value="1" /><el-option label="多选" :value="2" />
          <el-option label="判断" :value="3" /><el-option label="填空" :value="4" />
        </el-select>
        <el-select v-model="rule.difficulty" placeholder="难度" style="width:100px">
          <el-option label="简单" :value="1" /><el-option label="中等" :value="3" /><el-option label="困难" :value="5" />
        </el-select>
        <span>抽</span>
        <el-input-number v-model="rule.questionCount" :min="1" :max="50" style="width:100px" />
        <span>道，每题</span>
        <el-input-number v-model="rule.scorePerQuestion" :min="0" :step="0.5" style="width:100px" />
        <span>分</span>
        <el-button link type="danger" @click="rules.splice(i, 1)">删除</el-button>
      </div>
      <el-button @click="rules.push({ questionType: 1, difficulty: 3, questionCount: 5, scorePerQuestion: 2 })">+ 添加规则</el-button>
      <div style="margin-top:16px">
        <el-button @click="step = 0">上一步</el-button>
        <el-button type="primary" :loading="previewLoading" @click="doPreview">预览试卷</el-button>
      </div>
    </div>

    <!-- Step 3 -->
    <div v-if="step === 2 && previewData">
      <div style="margin-bottom:12px; font-weight:700">总分: {{ previewData.totalScore }} | 题数: {{ previewData.questionCount }}</div>
      <div v-for="q in previewData.questions" :key="q.questionId" class="pq-item">
        <span class="pq-type">{{ ['','单选','多选','判断','填空'][q.questionType] }}</span>
        <span class="pq-title" v-html="q.title"></span>
        <span class="pq-score">{{ q.score }}分</span>
      </div>
      <div style="margin-top:20px">
        <el-button @click="step = 1">返回修改</el-button>
        <el-button type="success" :loading="genLoading" @click="doGenerate">确认生成</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { autoGeneratePaper } from "@/api/paper";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
const step = ref(0);
const meta = reactive({ paperName: "", durationMinutes: 60, remark: "" });
const rules = ref([{ questionType: 1, difficulty: 3, questionCount: 5, scorePerQuestion: 2 }]);
const previewData = ref(null);
const previewLoading = ref(false);
const genLoading = ref(false);

async function doPreview() {
  previewLoading.value = true;
  try {
    const res = await autoGeneratePaper({ paperName: meta.paperName, rules: rules.value, preview: true });
    previewData.value = res.data;
    step.value = 2;
  } catch { ElMessage.error("预览失败，检查题库库存"); }
  finally { previewLoading.value = false; }
}

async function doGenerate() {
  genLoading.value = true;
  try {
    await autoGeneratePaper({ paperName: meta.paperName, durationMinutes: meta.durationMinutes, remark: meta.remark, rules: rules.value, preview: false });
    ElMessage.success("试卷生成成功");
    router.push("/teacher/papers");
  } catch { ElMessage.error("生成失败"); }
  finally { genLoading.value = false; }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.rule-row { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.pq-item { display: flex; align-items: baseline; gap: 10px; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
.pq-type { font-size: 12px; padding: 2px 6px; border-radius: 4px; background: #ecf5ff; color: #409eff; white-space: nowrap; }
.pq-title { flex: 1; }
.pq-score { color: #909399; font-size: 13px; white-space: nowrap; }
</style>
