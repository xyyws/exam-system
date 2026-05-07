<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import StatCard from "@/components/StatCard.vue";
import PanelCard from "@/components/PanelCard.vue";
import QuickActionCard from "@/components/QuickActionCard.vue";
import EChart from "@/components/charts/EChart.vue";
import request from "@/api/request";

const router = useRouter();

const stats = ref([
  { title: "总用户数", value: "-", trend: "", accent: "blue", icon: "users" },
  { title: "考试场次", value: "-", trend: "", accent: "green", icon: "exams" },
  { title: "参与人次", value: "-", trend: "", accent: "orange", icon: "visits" },
  { title: "班级数量", value: "-", trend: "", accent: "violet", icon: "classes" }
]);

const quickActions = [
  { title: "学生管理", icon: "userManage", accent: "blue", route: "/admin/students" },
  { title: "教师管理", icon: "roleManage", accent: "green", route: "/admin/teachers" },
  { title: "班级管理", icon: "classManage", accent: "orange", route: "/admin/classes" },
  { title: "系统日志", icon: "examManage", accent: "violet", route: "/admin/logs" }
];

const userDistribution = ref([
  { name: "管理员", value: 0 },
  { name: "教师", value: 0 },
  { name: "学生", value: 0 }
]);

onMounted(async () => {
  try {
    const res = await request.get("/admin/stats");
    const d = res.data || {};
    stats.value[0].value = String(d.totalUsers ?? 0);
    stats.value[1].value = String(d.examCount ?? 0);
    stats.value[2].value = String(d.participantCount ?? 0);
    stats.value[3].value = String(d.classCount ?? 0);

    const adminCount = Math.max(0, (d.totalUsers || 0) - (d.teacherCount || 0) - (d.studentCount || 0));
    userDistribution.value = [
      { name: "管理员", value: adminCount },
      { name: "教师", value: d.teacherCount || 0 },
      { name: "学生", value: d.studentCount || 0 }
    ];
  } catch {}
});

const pieOption = computed(() => ({
  tooltip: { trigger: "item", formatter: "{b}: {c} ({d}%)" },
  legend: { orient: "vertical", right: 0, top: "middle", itemGap: 18 },
  color: ["#2f6cf5", "#1eb36b", "#ff8b2a"],
  series: [
    {
      type: "pie",
      radius: ["55%", "78%"],
      center: ["35%", "50%"],
      label: { show: false },
      data: userDistribution.value
    }
  ]
}));

function navigateTo(route) { router.push(route); }
</script>

<template>
  <div class="dashboard-stack">
    <section class="stats-grid">
      <StatCard
        v-for="stat in stats"
        :key="stat.title"
        :title="stat.title"
        :value="stat.value"
        :trend="stat.trend"
        :accent="stat.accent"
        :icon="stat.icon"
      />
    </section>

    <section class="action-grid">
      <QuickActionCard
        v-for="action in quickActions"
        :key="action.title"
        :title="action.title"
        :icon="action.icon"
        :accent="action.accent"
        @click="navigateTo(action.route)"
      />
    </section>

    <PanelCard title="用户角色分布">
      <EChart :option="pieOption" height="286px" />
    </PanelCard>
  </div>
</template>

<style scoped>
.dashboard-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
</style>
