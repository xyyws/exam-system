<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getAvailableExams } from "@/api/runtime";
import { getOngoingExam } from "@/api/runtime";
import { getScoreTrend, getTypeBreakdown, getWrongBookSummary } from "@/api/analytics";
import PanelCard from "@/components/PanelCard.vue";
import EChart from "@/components/charts/EChart.vue";

const router = useRouter();

const upcomingExams = ref([]);
const ongoingExam = ref(null);
const wrongBook = ref({ total: 0, items: [] });
const scoreTrend = ref([]);
const radarData = ref({ types: [], correctRates: [] });

onMounted(async () => {
  const [availRes, ongoingRes, wrongRes, trendRes, breakdownRes] = await Promise.allSettled([
    getAvailableExams(),
    getOngoingExam(),
    getWrongBookSummary(),
    getScoreTrend(),
    getTypeBreakdown()
  ]);
  if (availRes.status === "fulfilled") upcomingExams.value = availRes.value.data?.exams || [];
  if (ongoingRes.status === "fulfilled" && ongoingRes.value.data?.hasOngoing) ongoingExam.value = ongoingRes.value.data;
  if (wrongRes.status === "fulfilled") wrongBook.value = wrongRes.value.data || { total: 0, items: [] };
  if (trendRes.status === "fulfilled") scoreTrend.value = trendRes.value.data || [];
  if (breakdownRes.status === "fulfilled") radarData.value = breakdownRes.value.data || { types: [], correctRates: [] };
});

function formatRemain(seconds) {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
}

function enterExam(examId) {
  router.push({ name: "student-exam", params: { id: examId } });
}

const scoreTrendOption = computed(() => ({
  tooltip: { trigger: "axis" },
  grid: { left: 16, right: 16, top: 18, bottom: 20, containLabel: true },
  xAxis: {
    type: "category",
    boundaryGap: false,
    data: scoreTrend.value.map((item) => item.examName || item.examTime?.slice(5, 10)),
    axisLine: { lineStyle: { color: "#d7deea" } },
    axisTick: { show: false }
  },
  yAxis: {
    type: "value",
    min: 0,
    max: 100,
    splitLine: { lineStyle: { color: "#eef2f7" } }
  },
  series: [
    {
      type: "line",
      smooth: true,
      symbolSize: 10,
      lineStyle: { width: 3, color: "#2f6cf5" },
      itemStyle: { color: "#2f6cf5" },
      data: scoreTrend.value.map((item) => item.percentage ?? 0)
    }
  ]
}));

const radarOption = computed(() => ({
  radar: {
    radius: "64%",
    indicator: radarData.value.types.map((name) => ({ name, max: 100 })),
    splitArea: { areaStyle: { color: ["#f9fbff", "#f4f8ff"] } }
  },
  series: [
    {
      type: "radar",
      data: [
        {
          value: radarData.value.correctRates,
          areaStyle: { color: "rgba(47,108,245,0.22)" },
          lineStyle: { color: "#2f6cf5", width: 3 },
          itemStyle: { color: "#2f6cf5" }
        }
      ]
    }
  ]
}));
</script>

<template>
  <div class="dashboard-stack">
    <PanelCard v-if="ongoingExam" title="在线考试">
      <div class="ongoing-card">
        <div>
          <h4>{{ ongoingExam.examName }}</h4>
          <div class="countdown-row">
            <span>剩余时间</span>
            <strong>{{ formatRemain(ongoingExam.remainSeconds) }}</strong>
            <span>已答 {{ ongoingExam.answeredCount }}/{{ ongoingExam.totalCount }}</span>
          </div>
        </div>
        <div class="ongoing-actions">
          <span class="solid-pill">正在考试</span>
          <button class="outline-button" type="button" @click="enterExam(ongoingExam.examId)">继续考试</button>
        </div>
      </div>
    </PanelCard>

    <PanelCard title="待考考试列表">
      <div class="table-shell">
        <table class="simple-table">
          <thead>
            <tr>
              <th>考试名称</th>
              <th>考试时间</th>
              <th>时长</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in upcomingExams" :key="item.examId">
              <td>{{ item.examName }}</td>
              <td>{{ item.startTime }}</td>
              <td>{{ item.durationMinutes }}分钟</td>
              <td><button class="outline-button" type="button" @click="enterExam(item.examId)">去考试</button></td>
            </tr>
            <tr v-if="upcomingExams.length === 0">
              <td colspan="4" style="text-align:center;color:#909399;">暂无待考考试</td>
            </tr>
          </tbody>
        </table>
      </div>
    </PanelCard>

    <section class="split-grid split-grid--student">
      <PanelCard :title="`错题本（共 ${wrongBook.total} 题）`">
        <div class="wrong-list">
          <div v-for="item in wrongBook.items" :key="item.label" class="wrong-row">
            <span>{{ item.label }}</span>
            <span>{{ item.count }}</span>
          </div>
          <div v-if="wrongBook.items.length === 0" style="text-align:center;color:#909399;padding:12px;">暂无错题</div>
        </div>
        <div class="panel-card__footer-link" @click="router.push('/student/wrong-book')">查看错题本</div>
      </PanelCard>

      <PanelCard title="成绩分析（最近一次）" extra="查看详情">
        <EChart :option="scoreTrendOption" height="240px" />
      </PanelCard>
    </section>

    <PanelCard title="知识点掌握雷达图（最近一次）">
      <EChart :option="radarOption" height="250px" />
    </PanelCard>
  </div>
</template>
