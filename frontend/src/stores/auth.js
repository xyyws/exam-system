import { defineStore } from "pinia";
import { login as apiLogin, logout as apiLogout, getCurrentUser } from "@/api/auth";

const userTypeRoleMap = { 1: "ADMIN", 2: "TEACHER", 3: "STUDENT" };

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("accessToken") || "",
    refreshToken: localStorage.getItem("refreshToken") || "",
    userInfo: null,
    roles: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userType: (state) => state.userInfo?.userType,
    // 优先用 roles，为空时从 userType 推断
    effectiveRole: (state) => {
      if (state.roles.length > 0) return state.roles[0];
      return userTypeRoleMap[state.userInfo?.userType] || null;
    },
    isAdmin: (state) => {
      if (state.roles.length > 0) return state.roles.includes("ADMIN");
      return state.userInfo?.userType === 1;
    },
    isTeacher: (state) => {
      if (state.roles.length > 0) return state.roles.includes("TEACHER");
      return state.userInfo?.userType === 2;
    },
    isStudent: (state) => {
      if (state.roles.length > 0) return state.roles.includes("STUDENT");
      return state.userInfo?.userType === 3;
    }
  },

  actions: {
    async login(credentials) {
      const res = await apiLogin(credentials);
      const { accessToken, refreshToken, user } = res.data;
      this.token = accessToken;
      this.refreshToken = refreshToken;
      this.userInfo = user;
      this.roles = user.roles || [];
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      return user;
    },

    async fetchUserInfo() {
      const res = await getCurrentUser();
      this.userInfo = res.data;
      this.roles = res.data.roles || [];
    },

    async logout() {
      try {
        await apiLogout();
      } finally {
        this.reset();
      }
    },

    reset() {
      this.token = "";
      this.refreshToken = "";
      this.userInfo = null;
      this.roles = [];
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    }
  }
});
