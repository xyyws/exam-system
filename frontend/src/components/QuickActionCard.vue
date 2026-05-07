<script setup>
import { computed } from "vue";
import { ElIcon } from "element-plus";
import { quickActionIcons } from "@/data/navigation";

const props = defineProps({
  title: String,
  icon: String,
  accent: String
});

const iconComponent = computed(() => quickActionIcons[props.icon]);
</script>

<template>
  <button class="quick-card" type="button">
    <div class="quick-card__icon" :class="`accent-${accent}`">
      <el-icon><component :is="iconComponent" /></el-icon>
    </div>
    <div class="quick-card__title">{{ title }}</div>
  </button>
</template>
