<template>
  <div class="page-panel">
    <div class="page-header">
      <h2>班级管理</h2>
      <el-button type="primary" @click="openDialog()">新增班级</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="班级ID" width="80" />
      <el-table-column prop="className" label="班级名称" />
      <el-table-column prop="gradeName" label="年级" />
      <el-table-column prop="deptName" label="院系" />
      <el-table-column prop="teacherName" label="班主任" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="10" layout="prev,pager,next" @current-change="fetch" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班级' : '新增班级'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级名称"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.gradeName" /></el-form-item>
        <el-form-item label="院系"><el-input v-model="form.deptName" /></el-form-item>
        <el-form-item label="班主任ID"><el-input-number v-model="form.teacherId" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getClasses, createClass, updateClass } from "@/api/system";
import { ElMessage } from "element-plus";

const list = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const total = ref(0);
const dialogVisible = ref(false);
const form = reactive({ id: null, className: "", gradeName: "", deptName: "", teacherId: null, remark: "", status: 1 });

onMounted(fetch);

async function fetch() {
  loading.value = true;
  try {
    const res = await getClasses({ pageNum: pageNum.value, pageSize: 10 });
    list.value = res.data.records || [];
    total.value = res.data.total || 0;
  } finally { loading.value = false; }
}

function openDialog(row) {
  Object.assign(form, { id: null, className: "", gradeName: "", deptName: "", teacherId: null, remark: "", status: 1 }, row);
  dialogVisible.value = true;
}

async function save() {
  try {
    form.id ? await updateClass(form.id, form) : await createClass(form);
    dialogVisible.value = false;
    fetch();
    ElMessage.success("保存成功");
  } catch { ElMessage.error("保存失败"); }
}
</script>

<style scoped>
.page-panel { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
</style>
