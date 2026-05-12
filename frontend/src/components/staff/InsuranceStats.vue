<template>
  <el-card>
    <div id="chart" style="width: 600px; height: 400px; margin: 0 auto;"></div>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const initChart = async () => {
  const chartDom = document.getElementById('chart')
  const myChart = echarts.init(chartDom)
  
  const res = await axios.get('/api/insurance/stats')
  if (res.data.code === 200) {
    const data = res.data.data
    const option = {
      title: {
        text: '学生参保比例',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '参保状态',
          type: 'pie',
          radius: '50%',
          data: [
            { value: data.insured, name: '已参保' },
            { value: data.uninsured, name: '未参保' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    myChart.setOption(option)
  }
}

onMounted(() => {
  initChart()
})
</script>
