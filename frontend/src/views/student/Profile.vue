<template>
  <div class="student-page">
    <h2>个人中心</h2>
    <div class="profile-card">
      <div class="avatar-circle">{{ (user.realName||'?')[0] }}</div>
      <div class="profile-info">
        <div class="pi-name">{{ user.realName }}</div>
        <div class="pi-item">用户名：{{ user.username }}</div>
        <div class="pi-item">角色：学生</div>
        <div class="pi-item">班级：{{ user.className || '未设置' }}</div>
      </div>
      <el-button type="primary" style="margin-left:auto" @click="dialogVisible = true">修改密码</el-button>
    </div>

    <el-dialog v-model="dialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="pwdForm" :rules="rules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-32位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import { changePassword } from "@/api/auth";
import { ElMessage } from "element-plus";

const auth = useAuthStore();
const user = computed(() => auth.userInfo || {});
const dialogVisible = ref(false);
const loading = ref(false);
const formRef = ref(null);
const pwdForm = reactive({ oldPassword: "", newPassword: "", confirmPassword: "" });

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) callback(new Error("两次密码不一致"));
  else callback();
};

const rules = {
  oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 32, message: "密码长度为6-32位", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    { validator: validateConfirm, trigger: "blur" }
  ]
};

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword });
    ElMessage.success("密码修改成功，请重新登录");
    dialogVisible.value = false;
    pwdForm.oldPassword = "";
    pwdForm.newPassword = "";
    pwdForm.confirmPassword = "";
  } catch (e) {
    // error message handled by request interceptor
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
h2{margin:0 0 20px;font-size:22px;font-weight:700}
.profile-card{display:flex;align-items:center;gap:24px;padding:32px;background:#fff;border:1px solid #eef1f6;border-radius:16px}
.avatar-circle{width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,#2e79ff,#5b9bff);color:#fff;display:grid;place-items:center;font-size:28px;font-weight:700}
.pi-name{font-size:20px;font-weight:700;color:#1a1a2e;margin-bottom:8px}
.pi-item{font-size:14px;color:#8b92a8;line-height:1.8}
</style>
