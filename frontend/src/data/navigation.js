import {
  ArrowDown, Bell, Calendar, Collection, DataAnalysis, EditPen,
  FolderOpened, HomeFilled, List, Lock, Memo, Monitor, Notebook,
  OfficeBuilding, Operation, PieChart, Postcard, Reading, School,
  Setting, TrendCharts, User, UserFilled, Tickets
} from "@element-plus/icons-vue";

export const sharedIcons = { bell: Bell, caret: ArrowDown };

export const quickActionIcons = {
  createQuestion: EditPen, manualPaper: Memo, autoPaper: Notebook, createExam: Postcard,
  userManage: User, roleManage: Setting, classManage: OfficeBuilding, examManage: Calendar
};

export const statIcons = { users: UserFilled, teachers: School, exams: Calendar, visits: User, classes: OfficeBuilding };

export const roleShellMap = {
  admin: {
    brand: "在线考试系统",
    userName: "管理员",
    userRole: "超级管理员",
    notificationCount: 0,
    avatarIcon: UserFilled,
    menu: [
      { key: "admin-dashboard", label: "数据统计", path: "/admin/dashboard", icon: DataAnalysis },
      { key: "admin-students", label: "学生管理", path: "/admin/students", icon: Reading },
      { key: "admin-teachers", label: "教师管理", path: "/admin/teachers", icon: User },
      { key: "admin-classes", label: "班级/院系", path: "/admin/classes", icon: OfficeBuilding },
      { key: "admin-logs", label: "系统日志", path: "/admin/logs", icon: Monitor }
    ]
  },
  teacher: {
    brand: "在线考试系统",
    userName: "李老师",
    userRole: "教师",
    notificationCount: 0,
    avatarIcon: UserFilled,
    menu: [
      { key: "teacher-dashboard", label: "首页", path: "/teacher/dashboard", icon: HomeFilled },
      {
        key: "teacher-question", label: "题库管理", icon: Notebook,
        children: [
          { key: "teacher-question-bank", label: "题库管理", path: "/teacher/questions" },
          { key: "teacher-question-category", label: "题目分类", path: "/teacher/categories" }
        ]
      },
      {
        key: "teacher-paper-group", label: "试卷管理", icon: Memo,
        children: [
          { key: "teacher-papers", label: "试卷列表", path: "/teacher/papers" },
          { key: "teacher-manual-paper", label: "手动组卷", path: "/teacher/paper-manual" },
          { key: "teacher-auto-paper", label: "自动组卷", path: "/teacher/paper-auto" }
        ]
      },
      {
        key: "teacher-exam-group", label: "考试管理", icon: Calendar,
        children: [
          { key: "teacher-exams", label: "考试列表", path: "/teacher/exams" },
          { key: "teacher-monitor", label: "监控中心", path: "/teacher/monitor" }
        ]
      },
      {
        key: "teacher-marking-group", label: "阅卷与成绩", icon: Reading,
        children: [
          { key: "teacher-marking", label: "阅卷任务", path: "/teacher/marking" },
          { key: "teacher-scores", label: "成绩列表", path: "/teacher/scores" }
        ]
      },
      { key: "teacher-analytics", label: "统计分析", path: "/teacher/analytics", icon: TrendCharts },
      { key: "teacher-profile", label: "个人中心", path: "/teacher/profile", icon: User }
    ]
  },
  student: {
    brand: "在线考试系统",
    userName: "张三",
    userRole: "学生",
    notificationCount: 0,
    avatarIcon: UserFilled,
    menu: [
      { key: "student-dashboard", label: "我的考试", path: "/student/dashboard", icon: HomeFilled },
      { key: "student-records", label: "考试记录", path: "/student/records", icon: Memo },
      { key: "student-scores", label: "我的成绩", path: "/student/scores", icon: List },
      { key: "student-analysis", label: "成绩分析", path: "/student/analysis", icon: PieChart },
      { key: "student-wrong-book", label: "错题本", path: "/student/wrong-book", icon: FolderOpened },
      { key: "student-profile", label: "个人中心", path: "/student/profile", icon: User }
    ]
  }
};
