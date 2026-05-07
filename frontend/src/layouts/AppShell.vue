<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import SidebarNav from "@/components/SidebarNav.vue";
import TopBar from "@/components/TopBar.vue";

const route = useRoute();
const role = computed(() => (route.meta.role || "admin").toLowerCase());
</script>

<template>
  <div class="app-shell">
    <SidebarNav :role="role" />
    <div class="app-shell__main">
      <TopBar :role="role" />
      <main class="app-shell__content">
        <router-view />
      </main>
    </div>
  </div>
</template>
