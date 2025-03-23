const os = require('os');
const v8 = require('v8');
const logger = require('./logger');
const notification = require('./notification');

class PerformanceMonitor {
    constructor() {
        this.metrics = {
            cpu: 0,
            memory: 0,
            heap: 0,
            requests: 0
        };
        this.startMonitoring();
    }

    startMonitoring() {
        setInterval(() => {
            this.updateMetrics();
            this.checkThresholds();
            this.logMetrics();
        }, 60000); // 1분마다 체크
    }

    updateMetrics() {
        // CPU 사용량
        const cpus = os.cpus();
        const totalCPU = cpus.reduce((acc, cpu) => {
            const total = Object.values(cpu.times).reduce((a, b) => a + b);
            const idle = cpu.times.idle;
            return acc + ((total - idle) / total);
        }, 0);
        this.metrics.cpu = (totalCPU / cpus.length) * 100;

        // 메모리 사용량
        const totalMemory = os.totalmem();
        const freeMemory = os.freemem();
        this.metrics.memory = ((totalMemory - freeMemory) / totalMemory) * 100;

        // 힙 메모리
        const heapStats = v8.getHeapStatistics();
        this.metrics.heap = (heapStats.used_heap_size / heapStats.heap_size_limit) * 100;
    }

    checkThresholds() {
        if (this.metrics.cpu > 80 || this.metrics.memory > 80 || this.metrics.heap > 70) {
            notification.sendPerformanceAlert(this.metrics);
        }
    }

    logMetrics() {
        logger.info('성능 메트릭:', {
            timestamp: new Date().toISOString(),
            metrics: this.metrics
        });
    }

    // 요청 추적
    trackRequest() {
        this.metrics.requests++;
    }
}

module.exports = new PerformanceMonitor(); 