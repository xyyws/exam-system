<template>
  <div class="page-panel">
    <div class="page-header"><h2>题目分类</h2><el-button type="primary" @click="openDialog()">新增分类</el-button></div>
    <el-table :data="list" stripe><el-table-column prop="id" label="ID" width="60"/><el-table-column prop="categoryName" label="分类名称"/><el-table-column prop="sortNo" label="排序" width="80"/><el-table-column label="操作" width="120"><template #default="{row}"><el-button link type="primary" @click="openDialog(row)">编辑</el-button></template></el-table-column></el-table>
    <el-dialog v-model="dialogVisible" :title="form.id?'编辑分类':'新增分类'" width="400px"><el-form :model="form" label-width="80px"><el-form-item label="分类名称"><el-input v-model="form.categoryName"/></el-form-item><el-form-item label="父分类ID"><el-input-number v-model="form.parentId" :min="0"/></el-form-item><el-form-item label="排序"><el-input-number v-model="form.sortNo"/></el-form-item></el-form><template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import { ref,reactive,onMounted } from "vue";import { getCategories,createCategory,updateCategory } from "@/api/question";import { ElMessage } from "element-plus";
const list=ref([]);const dialogVisible=ref(false);const form=reactive({id:null,parentId:0,categoryName:"",sortNo:0,status:1});
onMounted(async()=>{list.value=(await getCategories()).data||[]});
function openDialog(row){Object.assign(form,{id:null,parentId:0,categoryName:"",sortNo:0,status:1},row);dialogVisible.value=true}
async function save(){try{const{id,...data}=form;if(id)await updateCategory(id,data);else await createCategory(data);dialogVisible.value=false;list.value=(await getCategories()).data||[];ElMessage.success("保存成功")}catch{ElMessage.error("保存失败")}}
</script>
<style scoped>.page-panel{padding:20px}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}.page-header h2{margin:0}</style>
