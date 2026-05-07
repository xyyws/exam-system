<template>
  <div class="page-panel">
    <h2>个人中心</h2>
    <el-card style="max-width:500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ user.realName }}</el-descriptions-item>
        <el-descriptions-item label="工号">{{ user.teacherNo || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ user.deptName || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ user.phone || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email || '未设置' }}</el-descriptions-item>
      </el-descriptions>
      <el-button type="primary" style="margin-top:16px" @click="dialogVisible = true">修改密码</el-button>
    </el-card>

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
  } catch {
    // error message handled by request interceptor
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-panel h2 { margin: 0 0 16px; }
</style>
