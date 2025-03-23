const cluster = require('cluster');
const os = require('os');
const logger = require('../utils/logger');

class LoadBalancer {
    constructor() {
        this.numCPUs = os.cpus().length;
    }

    init() {
        if (cluster.isMaster) {
            logger.info(`마스터 프로세스 ${process.pid} 실행중`);

            // CPU 코어 수만큼 워커 생성
            for (let i = 0; i < this.numCPUs; i++) {
                cluster.fork();
            }

            // 워커 종료시 새로운 워커 생성
            cluster.on('exit', (worker, code, signal) => {
                logger.warn(`워커 ${worker.process.pid} 종료. 새 워커 생성 중...`);
                cluster.fork();
            });

            // 워커 상태 모니터링
            this.monitorWorkers();
        } else {
            logger.info(`워커 프로세스 ${process.pid} 실행중`);
        }
    }

    monitorWorkers() {
        setInterval(() => {
            const activeWorkers = Object.keys(cluster.workers).length;
            logger.info(`활성 워커 수: ${activeWorkers}`);
            
            if (activeWorkers < this.numCPUs) {
                const newWorkers = this.numCPUs - activeWorkers;
                logger.warn(`${newWorkers}개의 새로운 워커 생성 중...`);
                for (let i = 0; i < newWorkers; i++) {
                    cluster.fork();
                }
            }
        }, 30000); // 30초마다 체크
    }
}

module.exports = new LoadBalancer(); 