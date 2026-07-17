<template>
  <div class="student-page">
    <div class="page-top"><h2>错题本</h2><span class="count">共 {{ list.length }} 题</span></div>
    <div v-if="list.length" class="wrong-cards">
      <div v-for="w in list" :key="w.question_id || w.questionId" class="wrong-card" :class="{ mastered: (w.mastered_status || w.masteredStatus) === 1 }">
        <div class="wc-header">
          <span class="wc-id">#{{ w.question_id || w.questionId }}</span>
          <span class="wc-count">错 {{ w.wrong_count || w.wrongCount }} 次</span>
          <el-switch :model-value="(w.mastered_status || w.masteredStatus) === 1" @change="toggleMastered(w)" active-text="已掌握" size="small" />
        </div>
        <div class="wc-title">{{ w.question_title || w.questionTitle || '(题目已删除)' }}</div>
        <div class="wc-answers">
          <span class="wc-my-answer">我的答案：<b class="wrong-text">{{ w.latest_answer || w.latestAnswer || '(未作答)' }}</b></span>
          <span class="wc-correct-answer">正确答案：<b class="correct-text">{{ w.correct_answer || w.correctAnswer || '-' }}</b></span>
        </div>
        <div class="wc-analysis" v-if="w.answer_analysis || w.answerAnalysis">
          <span class="analysis-label">解析：</span>{{ w.answer_analysis || w.answerAnalysis }}
        </div>
        <div class="wc-note">
          <template v-if="editingNote === (w.question_id || w.questionId)">
            <el-input v-model="noteDraft" type="textarea" :rows="2" placeholder="写下你的笔记..." size="small" />
            <div class="note-actions">
              <el-button size="small" type="primary" @click="saveNote(w)">保存</el-button>
              <el-button size="small" @click="editingNote = null">取消</el-button>
            </div>
          </template>
          <template v-else>
            <div class="note-display" @click="startEditNote(w)">
              <span class="note-label">备注：</span>
              <span v-if="w.note_text || w.noteText" class="note-content">{{ w.note_text || w.noteText }}</span>
              <span v-else class="note-placeholder">点击添加笔记...</span>
            </div>
          </template>
        </div>
        <div class="wc-time">{{ w.last_wrong_time || w.lastWrongTime }}</div>
      </div>
    </div>
    <el-empty v-else description="暂无错题，继续加油！" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"; import request from "@/api/request"; import { ElMessage } from "element-plus";
const list = ref([]);
const editingNote = ref(null);
const noteDraft = ref("");
onMounted(fetch);
async function fetch() { try { const r = await request.get("/student/analytics/wrong-questions"); list.value = r.data?.records || []; } catch { list.value = []; } }
async function toggleMastered(row) { const qid = row.question_id || row.questionId; const ms = row.mastered_status || row.masteredStatus; try { await request.put(`/student/analytics/wrong-questions/${qid}/mastered`, { masteredStatus: ms === 1 ? 0 : 1 }); fetch(); } catch { ElMessage.error("更新失败"); } }
function startEditNote(row) { editingNote.value = row.question_id || row.questionId; noteDraft.value = row.note_text || row.noteText || ""; }
async function saveNote(row) { const qid = row.question_id || row.questionId; try { await request.put(`/student/analytics/wrong-questions/${qid}/note`, { noteText: noteDraft.value }); editingNote.value = null; fetch(); ElMessage.success("备注已保存"); } catch { ElMessage.error("保存失败"); } }
</script>

<style scoped>
.student-page{max-width:900px;margin:0 auto;padding:24px}
.page-top{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-top h2{margin:0;font-size:22px;font-weight:700}
.count{font-size:14px;color:#8b92a8}
.wrong-cards{display:flex;flex-direction:column;gap:12px}
.wrong-card{padding:16px 20px;background:#fff;border:1px solid #eef1f6;border-radius:14px}
.wrong-card.mastered{opacity:.6;background:#f8fafb}
.wc-header{display:flex;align-items:center;gap:12px;margin-bottom:8px}
.wc-id{font-size:12px;color:#b0b7c6}
.wc-count{font-size:12px;color:#e6a23c;font-weight:600}
.wc-title{font-size:15px;color:#1a1a2e;margin-bottom:8px}
.wc-answers{display:flex;gap:24px;font-size:13px;margin-bottom:6px}
.wrong-text{color:#f56c6c}
.correct-text{color:#67c23a}
.wc-analysis{font-size:12px;color:#909399;background:#f8f9fa;padding:8px 12px;border-radius:6px;margin-bottom:6px;line-height:1.6}
.analysis-label{font-weight:600;color:#606266}
.wc-note{margin-bottom:6px}
.note-display{cursor:pointer;padding:6px 10px;border-radius:6px;font-size:13px;transition:background .2s}
.note-display:hover{background:#f0f2f5}
.note-label{font-weight:600;color:#606266}
.note-content{color:#409eff}
.note-placeholder{color:#c0c4cc;font-style:italic}
.note-actions{display:flex;gap:8px;margin-top:6px}
.wc-time{font-size:11px;color:#c0c4cc}
</style>
