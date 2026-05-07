<template>
  <div class="score-analysis">
    <div class="page-header">
      <h2>成绩分析</h2>
      <span class="subtitle">多维度学习数据分析</span>
    </div>

    <div v-if="loading" class="state-box">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <div v-else-if="isEmpty" class="state-box">
      <el-empty description="暂无考试数据">
        <el-button type="primary" @click="$router.push('/student/dashboard')">去参加考试</el-button>
      </el-empty>
    </div>

    <template v-else>
      <div class="summary-row">
        <div class="summary-card">
          <span class="summary-num">{{ trendData.length }}</span>
          <span class="summary-label">参与考试</span>
        </div>
        <div class="summary-card">
          <span class="summary-num">{{ overallRate }}%</span>
          <span class="summary-label">总正确率</span>
        </div>
        <div class="summary-card">
          <span class="summary-num">{{ latestScore }}</span>
          <span class="summary-label">最近成绩</span>
        </div>
      </div>

      <div class="chart-grid">
        <div class="chart-card">
          <h4>成绩趋势</h4>
          <div ref="trendRef" class="chart-box"></div>
        </div>
        <div class="chart-card">
          <h4>得分分布</h4>
          <div ref="distRef" class="chart-box"></div>
        </div>
        <div class="chart-card">
          <h4>各题型正确率</h4>
          <div ref="radarRef" class="chart-box"></div>
        </div>
        <div class="chart-card">
          <h4>答题正确率</h4>
          <div ref="pieRef" class="chart-box"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { Loading } from '@element-plus/icons-vue'
import { getScoreTrend, getTypeBreakdown } from '@/api/analytics'

const loading = ref(true)
const trendData = ref([])
const breakdown = ref(null)

const trendRef = ref(null)
const distRef = ref(null)
const radarRef = ref(null)
const pieRef = ref(null)

let charts = []

const isEmpty = computed(() => !loading.value && trendData.value.length === 0)

const overallRate = computed(() => {
  if (!breakdown.value) return '0'
  return breakdown.value.overallRate
})

const latestScore = computed(() => {
  if (trendData.value.length === 0) return '--'
  const latest = trendData.value[0]
  return latest.totalScore + '/' + latest.totalMax
})

function createChart(domRef, option) {
  if (!domRef) return null
  const instance = echarts.init(domRef)
  instance.setOption(option)
  return instance
}

function buildTrendOption(data) {
  const reversed = [...data].reverse()
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 10, right: 20, top: 10, bottom: 20 },
    xAxis: {
      type: 'category',
      data: reversed.map(d => d.examName || '考试'),
      axisLabel: { fontSize: 11, color: '#909399', rotate: data.length > 6 ? 30 : 0 }
    },
    yAxis: {
      type: 'value',
      name: '得分率(%)',
      axisLabel: { fontSize: 11, color: '#909399' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      type: 'line',
      data: reversed.map(d => d.percentage),
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: '#409EFF' },
      itemStyle: { color: '#409EFF' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(64,158,255,0.25)' },
        { offset: 1, color: 'rgba(64,158,255,0.02)' }
      ])}
    }]
  }
}

function buildDistOption(data) {
  const reversed = [...data].reverse()
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 10, right: 20, top: 10, bottom: 20 },
    xAxis: {
      type: 'category',
      data: reversed.map(d => d.examName || '考试'),
      axisLabel: { fontSize: 11, color: '#909399', rotate: data.length > 6 ? 30 : 0 }
    },
    yAxis: {
      type: 'value',
      name: '得分',
      axisLabel: { fontSize: 11, color: '#909399' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [
      {
        name: '得分',
        type: 'bar',
        data: reversed.map(d => d.totalScore),
        itemStyle: { color: '#67C23A', borderRadius: [6, 6, 0, 0] },
        barWidth: '40%'
      },
      {
        name: '满分',
        type: 'bar',
        data: reversed.map(d => d.totalMax),
        itemStyle: { color: '#E4E7ED', borderRadius: [6, 6, 0, 0] },
        barWidth: '40%'
      }
    ]
  }
}

function buildRadarOption(data) {
  return {
    tooltip: {},
    radar: {
      center: ['50%', '55%'],
      radius: '65%',
      indicator: data.types.map(t => ({ name: t, max: 100 })),
      axisName: { fontSize: 12, color: '#606266' }
    },
    series: [{
      type: 'radar',
      data: [{ value: data.correctRates, name: '正确率(%)', areaStyle: { color: 'rgba(64,158,255,0.2)' } }],
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' }
    }]
  }
}

function buildPieOption(data) {
  const cc = data.totalCorrect
  const tc = data.totalCount
  const ic = tc - cc
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}题 ({d}%)' },
    legend: { bottom: 0, textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['55%', '78%'],
      center: ['50%', '48%'],
      avoidLabelOverlap: false,
      label: { show: true, position: 'center', formatter: `{label|${data.overallRate}%}\n{labelSmall|正确率}`, rich: { label: { fontSize: 28, fontWeight: 700, color: '#303133' }, labelSmall: { fontSize: 12, color: '#909399', padding: [8, 0, 0, 0] } } },
      emphasis: { label: { fontSize: 32 } },
      data: [
        { value: cc, name: '正确', itemStyle: { color: '#67C23A' } },
        { value: ic, name: '错误', itemStyle: { color: '#F56C6C' } }
      ]
    }]
  }
}

async function fetchData() {
  loading.value = true
  try {
    const [trendRes, breakdownRes] = await Promise.all([
      getScoreTrend(),
      getTypeBreakdown()
    ])
    trendData.value = trendRes.data || []
    breakdown.value = breakdownRes.data
  } catch {
    trendData.value = []
    breakdown.value = null
  } finally {
    loading.value = false
    await nextTick()
    renderCharts()
  }
}

function renderCharts() {
  disposeCharts()
  if (isEmpty.value) return

  charts = [
    createChart(trendRef.value, buildTrendOption(trendData.value)),
    createChart(distRef.value, buildDistOption(trendData.value)),
    breakdown.value ? createChart(radarRef.value, buildRadarOption(breakdown.value)) : null,
    breakdown.value ? createChart(pieRef.value, buildPieOption(breakdown.value)) : null
  ].filter(Boolean)
}

function disposeCharts() {
  charts.forEach(c => c.dispose())
  charts = []
}

function handleResize() {
  charts.forEach(c => c.resize())
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})
</script>

<style scoped>
.score-analysis { max-width: 960px; margin: 0 auto; padding: 24px }

.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a2e }
.subtitle { font-size: 13px; color: #909399; margin-top: 4px; display: inline-block }

.state-box { display: flex; flex-direction: column; align-items: center; padding: 80px 0; color: #b0b7c6 }

.summary-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 16px }
.summary-card { padding: 18px 16px; background: #fff; border: 1px solid #eef1f6; border-radius: 12px; text-align: center }
.summary-num { display: block; font-size: 24px; font-weight: 700; color: #1a1a2e }
.summary-label { font-size: 12px; color: #909399; margin-top: 4px }

.chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px }
.chart-card { padding: 20px; background: #fff; border: 1px solid #eef1f6; border-radius: 14px }
.chart-card h4 { margin: 0 0 12px; font-size: 14px; font-weight: 600; color: #303133 }
.chart-box { width: 100%; height: 260px }

@media (max-width: 720px) {
  .chart-grid { grid-template-columns: 1fr }
  .summary-row { grid-template-columns: 1fr }
  .chart-box { height: 220px }
}
</style>
