<script setup>
import { computed } from "vue";
import { ElIcon } from "element-plus";
import { statIcons } from "@/data/navigation";

const props = defineProps({
  title: String,
  value: String,
  trend: String,
  accent: {
    type: String,
    default: "blue"
  },
  icon: String
});

const iconComponent = computed(() => statIcons[props.icon]);
</script>

<template>
  <article class="stat-card">
    <div class="stat-title">{{ title }}</div>
    <div class="stat-main">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-icon" :class="`accent-${accent}`">
        <el-icon><component :is="iconComponent" /></el-icon>
      </div>
    </div>
    <div class="stat-trend">{{ trend }}</div>
  </article>
</template>
