<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-eyebrow">Online Exam System</div>
      <h1>在线考试系统</h1>
      <p>支持管理员、教师、学生三种角色登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" style="margin-top: 28px">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <div class="forgot-password">
          <el-link type="primary" underline="never" @click="showForgotPassword">忘记密码？</el-link>
        </div>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%; height: 48px; font-weight: 700; border-radius: 14px"
            @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const loading = ref(false);
const formRef = ref(null);

const form = reactive({
  username: "",
  password: ""
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

function showForgotPassword() {
  ElMessageBox.alert(
    '<p>请联系系统管理员重置您的密码。</p><p style="margin-top:8px;color:#909399;font-size:13px;">管理员联系方式请咨询您的教师或教务处。</p>',
    "忘记密码",
    {
      confirmButtonText: "我知道了",
      type: "info",
      dangerouslyUseHTMLString: true
    }
  );
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    const user = await authStore.login(form);
    ElMessage.success(`欢迎，${user.realName}`);
    const rolePath = { ADMIN: "/admin/dashboard", TEACHER: "/teacher/dashboard", STUDENT: "/student/dashboard" };
    const targetRole = authStore.effectiveRole;
    const redirect = route.query.redirect || (targetRole ? rolePath[targetRole] : "/login");
    router.push(redirect);
  } catch {
    ElMessage.error("用户名或密码错误");
  } finally {
    loading.value = false;
  }
}
</script>
