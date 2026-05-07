<template>
  <div class="exam-room" @contextmenu.prevent>
    <!-- Top bar -->
    <div class="exam-topbar">
      <span class="exam-name">{{ examName }}</span>
      <div class="exam-info">
        <span class="countdown" :class="{ danger: remainSeconds < 300 }">
          {{ formatTime(remainSeconds) }}
        </span>
        <span class="progress">{{ currentIdx + 1 }} / {{ questions.length }}</span>
        <el-button type="danger" size="small" round @click="handleSubmit">交卷</el-button>
      </div>
    </div>

    <!-- Main area -->
    <div class="exam-body">
      <!-- Question nav -->
      <div class="exam-nav">
        <div v-for="(q, i) in questions" :key="q.questionId"
          class="nav-dot" :class="{ done: answeredSet.has(q.questionId), active: i === currentIdx }"
          @click="currentIdx = i">
          {{ i + 1 }}
        </div>
      </div>

      <!-- Question content -->
      <div class="exam-content" v-if="currentQ">
        <div class="question-stem">
          <span class="q-type">{{ typeLabel(currentQ.questionType) }}</span>
          <span class="q-score">({{ currentQ.score }}分)</span>
          <div class="q-title" v-html="currentQ.title"></div>
        </div>

        <!-- Single / Judgment -->
        <el-radio-group v-if="currentQ.questionType === 1 || currentQ.questionType === 3"
          v-model="currentAnswer" class="option-list" @change="autoSave">
          <el-radio v-for="opt in (currentQ.options || [])" :key="opt.optionLabel"
            :value="opt.optionLabel" class="option-item">
            {{ opt.optionLabel }}. {{ opt.optionContent }}
          </el-radio>
        </el-radio-group>

        <!-- Multi choice -->
        <el-checkbox-group v-if="currentQ.questionType === 2"
          v-model="multiAnswer" class="option-list" @change="autoSave">
          <el-checkbox v-for="opt in (currentQ.options || [])" :key="opt.optionLabel"
            :label="opt.optionLabel" class="option-item">
            {{ opt.optionLabel }}. {{ opt.optionContent }}
          </el-checkbox>
        </el-checkbox-group>

        <!-- Fill blank / Short answer -->
        <el-input v-if="currentQ.questionType >= 4" v-model="currentAnswer" type="textarea"
          :rows="4" placeholder="请输入答案" @change="autoSave" />

        <div class="nav-buttons">
          <el-button :disabled="currentIdx === 0" @click="currentIdx--">上一题</el-button>
          <el-button type="primary" :disabled="currentIdx >= questions.length - 1" @click="currentIdx++">下一题</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { enterExam, saveAnswer, reportCutScreen, submitExam } from "@/api/runtime";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();
const examId = ref(Number(route.params.id));
const examName = ref("");
const examPaperId = ref(null);
const questions = ref([]);
const answers = ref([]);
const currentIdx = ref(0);
const currentAnswer = ref("");
const multiAnswer = ref([]);
const answeredSet = ref(new Set());
const remainSeconds = ref(0);
const cutCount = ref(0);
let timer = null;
let autoSaveTimer = null;

const currentQ = computed(() => questions.value[currentIdx.value] || null);

onMounted(async () => {
  try {
    const res = await enterExam(examId.value, navigator.userAgent);
    const d = res.data;
    examPaperId.value = d.examPaperId;
    examName.value = d.examName;
    questions.value = d.questions || [];
    remainSeconds.value = d.remainSeconds || 0;
    (d.answers || []).forEach(a => {
      if (a.studentAnswer) {
        answeredSet.value.add(a.questionId);
      }
    });
    startCountdown();
    startAutoSave();
    document.addEventListener("visibilitychange", onVisibilityChange);
  } catch { ElMessage.error("进入考试失败"); router.back(); }
});

onUnmounted(() => {
  clearInterval(timer);
  clearInterval(autoSaveTimer);
  document.removeEventListener("visibilitychange", onVisibilityChange);
});

watch(currentIdx, () => {
  if (!currentQ.value) return;
  if (currentQ.value.questionType === 2) {
    multiAnswer.value = currentAnswer.value ? currentAnswer.value.split(",") : [];
  } else {
    currentAnswer.value = "";
  }
  // Load existing answer
  const a = answers.value.find(x => x.questionId === currentQ.value.questionId);
  if (a) {
    if (currentQ.value.questionType === 2) {
      multiAnswer.value = a.studentAnswer ? a.studentAnswer.split(",") : [];
    } else {
      currentAnswer.value = a.studentAnswer || "";
    }
  }
});

function startCountdown() {
  timer = setInterval(() => {
    if (remainSeconds.value <= 0) {
      clearInterval(timer);
      handleSubmit(true);
      return;
    }
    remainSeconds.value--;
  }, 1000);
}

function startAutoSave() {
  autoSaveTimer = setInterval(() => doSave(), 30000);
}

async function autoSave() {
  if (!currentQ.value) return;
  const val = currentQ.value.questionType === 2 ? multiAnswer.value.join(",") : currentAnswer.value;
  if (!val) return;
  answeredSet.value.add(currentQ.value.questionId);
  await doSave();
}

async function doSave() {
  if (!examPaperId.value || !currentQ.value) return;
  const val = currentQ.value.questionType === 2 ? multiAnswer.value.join(",") : currentAnswer.value;
  try {
    await saveAnswer(examId.value, {
      examPaperId: examPaperId.value,
      questionId: currentQ.value.questionId,
      studentAnswer: val
    });
  } catch { /* silent */ }
}

function onVisibilityChange() {
  if (document.hidden) {
    cutCount.value++;
    reportCutScreen(examId.value, { examPaperId: examPaperId.value }).then(res => {
      if (res.data?.forceSubmit) handleSubmit(true);
      if (res.data?.cutScreenCount) cutCount.value = res.data.cutScreenCount;
    });
    if (cutCount.value >= 3) ElMessage.warning("已切屏 " + cutCount.value + " 次");
  }
}

async function handleSubmit(auto = false) {
  try {
    await ElMessageBox.confirm("确认交卷？交卷后不可修改", "提示", { confirmButtonText: "确认", cancelButtonText: "取消", type: "warning" });
  } catch { return; }
  try {
    await submitExam(examId.value, { examPaperId: examPaperId.value, autoSubmit: auto });
    ElMessage.success("交卷成功");
    router.push("/student/dashboard");
  } catch { ElMessage.error("交卷失败"); }
}

function formatTime(s) {
  const m = Math.floor(s / 60), sec = s % 60;
  return `${String(m).padStart(2, "0")}:${String(sec).padStart(2, "0")}`;
}

function typeLabel(t) {
  return { 1: "单选", 2: "多选", 3: "判断", 4: "填空", 5: "简答" }[t] || "";
}
</script>

<style scoped>
.exam-room{display:flex;flex-direction:column;height:calc(100vh - 80px);background:#f0f2f5}
.exam-topbar{display:flex;align-items:center;justify-content:space-between;padding:12px 24px;background:#fff;box-shadow:0 2px 8px rgba(0,0,0,.06);z-index:10}
.exam-name{font-size:17px;font-weight:700;color:#1a1a2e}
.exam-info{display:flex;align-items:center;gap:20px}
.countdown{font-size:26px;font-weight:700;color:#1a1a2e;font-variant-numeric:tabular-nums}
.countdown.danger{color:#f56c6c;animation:pulse 1s infinite}
@keyframes pulse{50%{opacity:.4}}
.progress{font-size:13px;color:#8b92a8}
.exam-body{display:flex;flex:1;overflow:hidden}
.exam-nav{width:56px;padding:12px 6px;background:#fff;overflow-y:auto;border-right:1px solid #eef1f6;display:flex;flex-direction:column;gap:6px}
.nav-dot{width:34px;height:34px;border-radius:50%;display:grid;place-items:center;font-size:12px;background:#f0f2f5;cursor:pointer;border:2px solid transparent;margin:0 auto;color:#8b92a8;transition:all .2s}
.nav-dot:hover{background:#e8ecf2}
.nav-dot.done{background:#e6f7e9;color:#2e79ff;border-color:#2e79ff}
.nav-dot.active{background:#2e79ff;color:#fff;border-color:#2e79ff}
.exam-content{flex:1;padding:32px 40px;overflow-y:auto;background:#fff}
.question-stem{margin-bottom:24px}
.q-type{display:inline-block;padding:3px 10px;border-radius:6px;background:#e8f0fe;color:#2e79ff;font-size:12px;font-weight:600;margin-right:10px}
.q-score{color:#8b92a8;font-size:13px}
.q-title{margin-top:14px;font-size:17px;line-height:1.8;color:#1a1a2e}
.option-list{display:flex;flex-direction:column;gap:10px}
.option-item{padding:14px 16px;border-radius:10px;background:#f8fafc;border:1px solid #eef1f6;margin:0;transition:all .15s}
.option-item:hover{background:#e8f0fe;border-color:#2e79ff}
.nav-buttons{display:flex;justify-content:space-between;margin-top:36px;padding-top:18px;border-top:1px solid #eef1f6}
</style>
