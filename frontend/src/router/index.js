import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import AppShell from "@/layouts/AppShell.vue";
import LoginView from "@/views/LoginView.vue";
import AdminDashboardView from "@/views/admin/AdminDashboardView.vue";
import TeacherDashboardView from "@/views/teacher/TeacherDashboardView.vue";
import StudentDashboardView from "@/views/student/StudentDashboardView.vue";

// Admin
import StudentManage from "@/views/admin/StudentManage.vue";
import TeacherManage from "@/views/admin/TeacherManage.vue";
import ClassManage from "@/views/admin/ClassManage.vue";
import OperationLog from "@/views/admin/OperationLog.vue";

// Teacher
import QuestionBank from "@/views/teacher/QuestionBank.vue";
import CategoryList from "@/views/teacher/CategoryList.vue";
import PaperManage from "@/views/teacher/PaperManage.vue";
import AutoPaperWizard from "@/views/teacher/AutoPaperWizard.vue";
import ExamManage from "@/views/teacher/ExamManage.vue";
import ExamMonitor from "@/views/teacher/ExamMonitor.vue";
import MarkingCenter from "@/views/teacher/MarkingCenter.vue";
import Analytics from "@/views/teacher/Analytics.vue";
import TeacherProfile from "@/views/teacher/Profile.vue";

// Student
import ExamList from "@/views/student/ExamList.vue";
import ExamRoom from "@/views/student/ExamRoom.vue";
import ExamRecords from "@/views/student/ExamRecords.vue";
import ScoreList from "@/views/student/ScoreList.vue";
import ScoreAnalysis from "@/views/student/ScoreAnalysis.vue";
import WrongBook from "@/views/student/WrongBook.vue";
import StudentProfile from "@/views/student/Profile.vue";

const adminRoutes = [
  { path: "dashboard", name: "admin-dashboard", component: AdminDashboardView, meta: { title: "数据统计", role: "ADMIN" } },
  { path: "students", name: "admin-students", component: StudentManage, meta: { title: "学生管理", role: "ADMIN" } },
  { path: "teachers", name: "admin-teachers", component: TeacherManage, meta: { title: "教师管理", role: "ADMIN" } },
  { path: "classes", name: "admin-classes", component: ClassManage, meta: { title: "班级管理", role: "ADMIN" } },
  { path: "logs", name: "admin-logs", component: OperationLog, meta: { title: "系统日志", role: "ADMIN" } }
];

const teacherRoutes = [
  { path: "dashboard", name: "teacher-dashboard", component: TeacherDashboardView, meta: { title: "首页", role: "TEACHER" } },
  { path: "questions", name: "teacher-questions", component: QuestionBank, meta: { title: "题库考里", role: "TEACHER" } },
  { path: "categories", name: "teacher-categories", component: CategoryList, meta: { title: "题目分类", role: "TEACHER" } },
  { path: "papers", name: "teacher-papers", component: PaperManage, meta: { title: "试卷列表", role: "TEACHER" } },
  { path: "paper-auto", name: "teacher-paper-auto", component: AutoPaperWizard, meta: { title: "自动组卷", role: "TEACHER" } },
  { path: "paper-manual", name: "teacher-paper-manual", component: AutoPaperWizard, meta: { title: "手动组卷", role: "TEACHER" } },
  { path: "exams", name: "teacher-exams", component: ExamManage, meta: { title: "考试列表", role: "TEACHER" } },
  { path: "monitor", name: "teacher-monitor", component: ExamMonitor, meta: { title: "监控中心", role: "TEACHER" } },
  { path: "marking", name: "teacher-marking", component: MarkingCenter, meta: { title: "阅卷任务", role: "TEACHER" } },
  { path: "scores", name: "teacher-scores", component: Analytics, meta: { title: "成绩列表", role: "TEACHER" } },
  { path: "analytics", name: "teacher-analytics", component: Analytics, meta: { title: "统计分析", role: "TEACHER" } },
  { path: "profile", name: "teacher-profile", component: TeacherProfile, meta: { title: "个人中心", role: "TEACHER" } }
];

const studentRoutes = [
  { path: "dashboard", name: "student-dashboard", component: ExamList, meta: { title: "我的考试", role: "STUDENT" } },
  { path: "exam/:id", name: "student-exam", component: ExamRoom, meta: { title: "在线答题", role: "STUDENT" } },
  { path: "records", name: "student-records", component: ExamRecords, meta: { title: "考试记录", role: "STUDENT" } },
  { path: "scores", name: "student-scores", component: ScoreList, meta: { title: "我的成绩", role: "STUDENT" } },
  { path: "analysis", name: "student-analysis", component: ScoreAnalysis, meta: { title: "成绩分析", role: "STUDENT" } },
  { path: "wrong-book", name: "student-wrong-book", component: WrongBook, meta: { title: "错题本", role: "STUDENT" } },
  { path: "profile", name: "student-profile", component: StudentProfile, meta: { title: "个人中心", role: "STUDENT" } }
];

const roleDefaultRoute = { ADMIN: "/admin/dashboard", TEACHER: "/teacher/dashboard", STUDENT: "/student/dashboard" };

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", name: "login", component: LoginView, meta: { public: true } },
  { path: "/admin", component: AppShell, meta: { role: "ADMIN" }, children: adminRoutes },
  { path: "/teacher", component: AppShell, meta: { role: "TEACHER" }, children: teacherRoutes },
  { path: "/student", component: AppShell, meta: { role: "STUDENT" }, children: studentRoutes }
];

const router = createRouter({ history: createWebHistory(), routes });

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.public) {
    if (authStore.isLoggedIn && to.path === "/login") {
      if (!authStore.userInfo) {
        try { await authStore.fetchUserInfo(); } catch { authStore.reset(); return next(); }
      }
      const effectiveRole = authStore.effectiveRole;
      const defaultPath = roleDefaultRoute[effectiveRole];
      if (defaultPath) return next(defaultPath);
    }
    return next();
  }
  if (!authStore.isLoggedIn) return next({ path: "/login", query: { redirect: to.fullPath } });
  if (!authStore.userInfo) {
    try { await authStore.fetchUserInfo(); }
    catch { authStore.reset(); return next("/login"); }
  }
  const effectiveRole = authStore.effectiveRole;
  const requiredRole = to.meta.role;
  if (requiredRole && requiredRole !== effectiveRole) {
    return next(roleDefaultRoute[effectiveRole] || "/login");
  }
  next();
});

export default router;
