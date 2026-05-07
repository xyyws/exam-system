<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElIcon } from "element-plus";
import { roleShellMap, sharedIcons } from "@/data/navigation";
import { useAuthStore } from "@/stores/auth";

const props = defineProps({
  role: { type: String, required: true }
});

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const shell = computed(() => roleShellMap[(props.role || "admin").toLowerCase()]);
const breadcrumb = computed(() => route.meta.title || "首页");

function handleLogout() {
  authStore.logout();
  router.push("/login");
}
</script>

<template>
  <header class="topbar">
    <div class="topbar-title">{{ breadcrumb }}</div>
    <div class="topbar-actions">
      <div class="topbar-user">
        <div class="topbar-avatar">
          <el-icon><component :is="shell.avatarIcon" /></el-icon>
        </div>
        <span class="topbar-name">{{ authStore.userInfo?.realName || shell.userName }}</span>
        <el-dropdown trigger="click" @command="handleLogout">
          <el-icon class="caret" style="cursor:pointer"><component :is="sharedIcons.caret" /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>
