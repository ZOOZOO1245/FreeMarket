const jwt = require('jsonwebtoken');
const rateLimit = require('express-rate-limit');

// 결제 요청 제한
exports.paymentLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15분
    max: 10 // IP당 최대 요청 수
});

// 결제 인증 검증
exports.verifyPaymentAuth = (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];
        if (!token) {
            return res.status(401).json({ error: '인증 토큰이 없습니다.' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        res.status(401).json({ error: '유효하지 않은 토큰입니다.' });
    }
};

// 결제 금액 검증
exports.verifyPaymentAmount = (req, res, next) => {
    const { amount, items } = req.body;
    const calculatedAmount = items.reduce((sum, item) => 
        sum + (item.price * item.quantity), 0);

    if (amount !== calculatedAmount) {
        return res.status(400).json({ error: '결제 금액이 일치하지 않습니다.' });
    }
    next();
}; 