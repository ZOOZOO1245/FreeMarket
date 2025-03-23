const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const cors = require('cors');

const securityConfig = (app) => {
    // 기본 보안 헤더 설정
    app.use(helmet());

    // CORS 설정
    app.use(cors({
        origin: process.env.ALLOWED_ORIGINS.split(','),
        methods: ['GET', 'POST', 'PUT', 'DELETE'],
        allowedHeaders: ['Content-Type', 'Authorization'],
        credentials: true
    }));

    // API 요청 제한
    app.use('/api/', rateLimit({
        windowMs: 15 * 60 * 1000, // 15분
        max: 100 // IP당 최대 요청 수
    }));

    // XSS 방지
    app.use(helmet.xssFilter());

    // 클릭재킹 방지
    app.use(helmet.frameguard({ action: 'deny' }));

    // MIME 타입 스니핑 방지
    app.use(helmet.noSniff());

    // HTTP를 HTTPS로 리다이렉트
    app.use((req, res, next) => {
        if (!req.secure && process.env.NODE_ENV === 'production') {
            return res.redirect(`https://${req.headers.host}${req.url}`);
        }
        next();
    });
};

module.exports = securityConfig; 