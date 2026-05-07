<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { roleShellMap } from "@/data/navigation";
import { ElIcon, ElMessageBox } from "element-plus";
import { useAuthStore } from "@/stores/auth";

const props = defineProps({
  role: {
    type: String,
    required: true
  }
});

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const shell = computed(() => roleShellMap[(props.role || "admin").toLowerCase()]);

async function handleLogout() {
  try { await ElMessageBox.confirm("确认退出登录？", "提示", { type: "warning" }); } catch { return; }
  authStore.logout();
  router.push("/login");
}

function isActive(item) {
  if (item.children) {
    return item.children.some((child) => route.path === child.path);
  }
  return route.path === item.path;
}

function navigate(item) {
  if (item.path) {
    router.push(item.path);
  }
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">
        <el-icon><component :is="shell.avatarIcon" /></el-icon>
      </div>
      <span>{{ shell.brand }}</span>
    </div>

    <nav class="menu">
      <div
        v-for="item in shell.menu"
        :key="item.key"
        class="menu-group"
      >
        <button
          type="button"
          class="menu-item"
          :class="{ active: isActive(item), grouped: !!item.children }"
          @click="navigate(item)"
        >
          <span class="menu-label">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </span>
        </button>
        <div v-if="item.children" class="submenu">
          <button
            v-for="child in item.children"
            :key="child.key"
            type="button"
            class="submenu-item"
            :class="{ active: route.path === child.path }"
            @click="navigate(child)"
          >
            {{ child.label }}
          </button>
        </div>
      </div>
    </nav>

    <div class="sidebar-user" @click="handleLogout" style="cursor:pointer" title="点击退出登录">
      <div class="avatar">
        <el-icon><component :is="shell.avatarIcon" /></el-icon>
      </div>
      <div class="user-meta">
        <div class="name">{{ authStore.userInfo?.realName || shell.userName }}</div>
        <div class="role">{{ shell.userRole }}</div>
      </div>
    </div>
  </aside>
</template>
