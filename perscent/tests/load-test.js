const autocannon = require('autocannon');
const logger = require('../utils/logger');

const loadTest = async () => {
    const test = autocannon({
        url: 'https://your-domain.com',
        connections: 100,
        pipelining: 10,
        duration: 30,
        scenarios: [
            {
                name: '상품 목록 조회',
                method: 'GET',
                path: '/api/products'
            },
            {
                name: '결제 처리',
                method: 'POST',
                path: '/api/payment',
                body: JSON.stringify({
                    amount: 10000,
                    method: 'card'
                }),
                headers: {
                    'content-type': 'application/json'
                }
            }
        ]
    });

    autocannon.track(test, { renderProgressBar: true });

    test.on('done', (results) => {
        logger.info('부하 테스트 결과:', {
            averageLatency: results.latency.average,
            requestsPerSecond: results.requests.average,
            errors: results.errors,
            timeouts: results.timeouts
        });
    });
};

module.exports = loadTest; 