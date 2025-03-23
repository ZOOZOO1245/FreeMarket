const prometheus = require('prom-client');
const logger = require('./logger');

class MonitoringSystem {
    constructor() {
        this.register = new prometheus.Registry();
        
        // 메트릭 정의
        this.httpRequestDuration = new prometheus.Histogram({
            name: 'http_request_duration_seconds',
            help: 'HTTP 요청 처리 시간',
            labelNames: ['method', 'route', 'status_code']
        });

        this.paymentSuccessCounter = new prometheus.Counter({
            name: 'payment_success_total',
            help: '성공한 결제 수'
        });

        this.paymentFailureCounter = new prometheus.Counter({
            name: 'payment_failure_total',
            help: '실패한 결제 수'
        });

        this.activeUsers = new prometheus.Gauge({
            name: 'active_users',
            help: '현재 활성 사용자 수'
        });

        // 메트릭 등록
        this.register.registerMetric(this.httpRequestDuration);
        this.register.registerMetric(this.paymentSuccessCounter);
        this.register.registerMetric(this.paymentFailureCounter);
        this.register.registerMetric(this.activeUsers);
    }

    // 미들웨어 생성
    createMiddleware() {
        return (req, res, next) => {
            const start = Date.now();

            res.on('finish', () => {
                const duration = Date.now() - start;
                this.httpRequestDuration.observe(
                    {
                        method: req.method,
                        route: req.route?.path || req.path,
                        status_code: res.statusCode
                    },
                    duration / 1000
                );
            });

            next();
        };
    }

    // 결제 모니터링
    trackPayment(success) {
        if (success) {
            this.paymentSuccessCounter.inc();
        } else {
            this.paymentFailureCounter.inc();
        }
    }

    // 사용자 수 추적
    updateActiveUsers(count) {
        this.activeUsers.set(count);
    }

    // 메트릭 엔드포인트
    getMetricsHandler() {
        return async (req, res) => {
            try {
                res.set('Content-Type', this.register.contentType);
                res.end(await this.register.metrics());
            } catch (error) {
                logger.error('메트릭 수집 실패:', error);
                res.status(500).end();
            }
        };
    }
}

module.exports = new MonitoringSystem(); 