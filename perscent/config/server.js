const compression = require('compression');
const express = require('express');

class ServerOptimizer {
    constructor(app) {
        this.app = app;
    }

    optimize() {
        // GZIP 압축
        this.app.use(compression());

        // 정적 파일 캐싱
        this.app.use(express.static('public', {
            maxAge: '1d',
            etag: true
        }));

        // 요청 본문 크기 제한
        this.app.use(express.json({ limit: '10mb' }));
        this.app.use(express.urlencoded({ extended: true, limit: '10mb' }));

        // 커넥션 풀 설정
        this.app.set('trust proxy', 1);
        
        // 타임아웃 설정
        this.app.use((req, res, next) => {
            req.setTimeout(5000);
            res.setTimeout(5000);
            next();
        });

        // 에러 처리
        this.app.use((err, req, res, next) => {
            logger.error('서버 에러:', err);
            res.status(500).json({ error: '서버 오류가 발생했습니다.' });
        });
    }
}

module.exports = ServerOptimizer; 