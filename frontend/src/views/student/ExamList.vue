<template>
  <div class="student-page">
    <div class="page-top">
      <h2>我的考试</h2>
      <el-radio-group v-model="filter" size="small">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="ongoing">进行中</el-radio-button>
      </el-radio-group>
    </div>
    <div v-if="exams.length" class="exam-cards">
      <div v-for="e in exams" :key="e.examId" class="exam-card">
        <div class="card-body">
          <h4>{{ e.examName }}</h4>
          <div class="card-info">
            <span>时长 {{ e.durationMinutes }} 分钟</span>
            <span>总分 {{ e.totalScore }} 分</span>
            <span>及格 {{ e.passScore }} 分</span>
          </div>
          <div class="card-time">{{ e.startTime?.substring(0,16) }} ~ {{ e.endTime?.substring(11,16) }}</div>
        </div>
        <div class="card-action">
          <el-button type="primary" round @click="confirmEnter(e)">进入考试</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无可用考试" />

    <!-- 考试确认对话框 -->
    <el-dialog v-model="confirmVisible" title="考试须知" width="520px" class="confirm-dialog">
      <div class="confirm-body">
        <h3>{{ confirmExam?.examName }}</h3>
        <div class="confirm-info">
          <p><b>答题时长：</b>{{ confirmExam?.durationMinutes }} 分钟</p>
          <p><b>题量说明：</b>本试卷共包含 <b>{{ confirmExam?.questionCount || '—' }}</b> 道题目</p>
        </div>
        <div class="confirm-rules">
          <p><b>考试说明：</b></p>
          <ol>
            <li>请不要中途离开考试界面</li>
            <li>请将手机调至免打扰模式，避免来电中断</li>
            <li>确保设备有充足电量或接入电源</li>
            <li>保持座位桌面干净，不要有与考试无关的物品</li>
          </ol>
        </div>
        <div class="confirm-warning">
          <b>系统声明：</b>本次考试会进行后台防作弊识别监控，请自觉遵守考试纪律。
        </div>
      </div>
      <template #footer>
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="doEnter">确认进入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getAvailableExams } from "@/api/runtime";

const router = useRouter();
const exams = ref([]);
const filter = ref("all");
const confirmVisible = ref(false);
const confirmExam = ref(null);

onMounted(async () => { try { const res = await getAvailableExams(); exams.value = res.data?.exams || []; } catch { exams.value = []; } });

function confirmEnter(exam) {
  confirmExam.value = exam;
  confirmVisible.value = true;
}

function doEnter() {
  confirmVisible.value = false;
  router.push(`/student/exam/${confirmExam.value.examId}`);
}
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
.page-top{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-top h2{margin:0;font-size:22px;font-weight:700}
.exam-cards{display:flex;flex-direction:column;gap:16px}
.exam-card{display:flex;align-items:center;justify-content:space-between;padding:20px 24px;background:#fff;border:1px solid #eef1f6;border-radius:16px;transition:box-shadow .2s}
.exam-card:hover{box-shadow:0 8px 24px rgba(0,0,0,.06)}
.card-body h4{margin:0 0 8px;font-size:17px;font-weight:600;color:#1a1a2e}
.card-info{display:flex;gap:20px;font-size:13px;color:#8b92a8;margin-bottom:4px}
.card-time{font-size:12px;color:#b0b7c6}
.card-action{flex-shrink:0}
.confirm-body h3{margin:0 0 16px;font-size:20px;color:#1a1a2e}
.confirm-info p{margin:6px 0;font-size:14px;color:#333}
.confirm-rules{margin:16px 0;padding:12px 16px;background:#f8f9fb;border-radius:10px}
.confirm-rules p{margin:0 0 8px;font-size:14px;color:#333}
.confirm-rules ol{margin:0;padding-left:20px;font-size:13px;color:#666;line-height:2}
.confirm-warning{margin-top:12px;padding:10px 14px;background:#fff7e6;border-radius:8px;font-size:13px;color:#e6a23c}
</style>
