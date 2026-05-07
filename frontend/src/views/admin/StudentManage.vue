<template>
  <div class="page-panel">
    <div class="page-header">
      <h2>学生管理</h2>
      <el-button type="primary" @click="openDialog()">新增学生</el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索姓名/学号" clearable style="width:220px" @clear="fetch" @keyup.enter="fetch" />
      <el-select v-model="filterClassId" placeholder="按班级筛选" clearable style="width:180px" @change="fetch">
        <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id" />
      </el-select>
      <el-button @click="fetch">搜索</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="username" label="登录账号" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="deptName" label="院系" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="10" layout="total,prev,pager,next" @current-change="fetch" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑学生' : '新增学生'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" placeholder="请选择班级" clearable>
            <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getUsers, createUser, updateUser, updateUserStatus, deleteUser, resetPassword, getClasses } from "@/api/system";
import { ElMessage, ElMessageBox } from "element-plus";

const list = ref([]);
const loading = ref(false);
const saving = ref(false);
const pageNum = ref(1);
const total = ref(0);
const keyword = ref("");
const filterClassId = ref(null);
const classList = ref([]);
const dialogVisible = ref(false);
const formRef = ref(null);
const form = reactive({
  id: null, username: "", password: "", realName: "", studentNo: "",
  userType: 3, phone: "", email: "", deptName: "", classId: null
});

const rules = {
  username: [{ required: true, message: "请输入登录账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  realName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  studentNo: [{ required: true, message: "请输入学号", trigger: "blur" }]
};

onMounted(() => { fetch(); fetchClasses(); });

async function fetch() {
  loading.value = true;
  try {
    const params = { pageNum: pageNum.value, pageSize: 10, userType: 3 };
    if (keyword.value) params.keyword = keyword.value;
    if (filterClassId.value) params.classId = filterClassId.value;
    const res = await getUsers(params);
    list.value = res.data.records || [];
    total.value = res.data.total || 0;
  } finally { loading.value = false; }
}

async function fetchClasses() {
  try { classList.value = (await getClasses({ pageSize: 200 })).data?.records || []; } catch {}
}

function openDialog(row) {
  Object.assign(form, { id: null, username: "", password: "", realName: "", studentNo: "", userType: 3, phone: "", email: "", deptName: "", classId: null }, row);
  dialogVisible.value = true;
}

async function save() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  saving.value = true;
  try {
    if (form.id) await updateUser(form.id, form);
    else await createUser(form);
    dialogVisible.value = false;
    fetch();
    ElMessage.success("保存成功");
  } catch {} finally { saving.value = false; }
}

async function toggleStatus(row) {
  await updateUserStatus(row.id, row.status === 1 ? 0 : 1);
  fetch();
}

async function handleResetPwd(row) {
  await ElMessageBox.confirm(`确认重置「${row.realName}」的密码为 123456？`, "重置密码", { type: "warning" });
  await resetPassword(row.id, "123456");
  ElMessage.success("密码已重置为 123456");
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除学生「${row.realName}」？`, "删除确认", { type: "warning" });
  await deleteUser(row.id);
  fetch();
  ElMessage.success("已删除");
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
