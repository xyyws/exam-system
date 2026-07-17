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
  icon: String,
  clickable: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["click"]);
const iconComponent = computed(() => statIcons[props.icon]);

function handleClick() {
  if (props.clickable) {
    emit("click");
  }
}
</script>

<template>
  <article class="stat-card" :class="{ clickable }" @click="handleClick">
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

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.2s, transform 0.2s;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.accent-blue { background: rgba(47, 108, 245, 0.1); color: #2f6cf5; }
.accent-green { background: rgba(30, 179, 107, 0.1); color: #1eb36b; }
.accent-orange { background: rgba(255, 139, 42, 0.1); color: #ff8b2a; }
.accent-violet { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }

.stat-trend {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
