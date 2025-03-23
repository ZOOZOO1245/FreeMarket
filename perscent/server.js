const express = require('express');
const app = express();
const port = 3000;
const axios = require('axios');

app.use(express.json());
app.use(express.static('public'));

// 세션 관리를 위한 미들웨어
const session = require('express-session');
app.use(session({
    secret: 'your-secret-key',
    resave: false,
    saveUninitialized: true
}));

// 데이터베이스 대신 메모리에 저장 (실제 구현시에는 DB 사용 필요)
const users = [];
const carts = {};

// 회원가입 API
app.post('/api/register', (req, res) => {
    const { username, password } = req.body;
    if (users.find(user => user.username === username)) {
        return res.status(400).json({ error: '이미 존재하는 사용자입니다.' });
    }
    users.push({ username, password });
    res.json({ message: '회원가입 성공!' });
});

// 로그인 API
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    const user = users.find(u => u.username === username && u.password === password);
    if (user) {
        req.session.user = username;
        res.json({ message: '로그인 성공!' });
    } else {
        res.status(401).json({ error: '로그인 실패' });
    }
});

// 장바구니 API
app.post('/api/cart', (req, res) => {
    if (!req.session.user) {
        return res.status(401).json({ error: '로그인이 필요합니다.' });
    }
    const { productId } = req.body;
    if (!carts[req.session.user]) {
        carts[req.session.user] = [];
    }
    carts[req.session.user].push(productId);
    res.json({ message: '장바구니에 추가되었습니다.' });
});

// 카카오페이 결제 API
app.post('/api/payment/kakaopay', (req, res) => {
    const { amount, items } = req.body;
    
    // 실제 구현시에는 카카오페이 API를 호출
    // 여기서는 예시로 성공 응답만 반환
    res.json({
        success: true,
        next_redirect_pc_url: 'https://mockup-kakaopay.com/payment'
    });
});

// 카드 결제 API
app.post('/api/payment/card', (req, res) => {
    const { cardData, amount, items } = req.body;
    
    // 실제 구현시에는 PG사 API를 호출
    // 여기서는 예시로 성공 응답만 반환
    res.json({
        success: true,
        message: '결제가 성공적으로 처리되었습니다.'
    });
});

// 네이버페이 결제 API
app.post('/api/payment/naverpay', (req, res) => {
    const { amount, items } = req.body;
    
    // 실제 구현시에는 네이버페이 API를 호출
    res.json({
        success: true,
        next_redirect_pc_url: 'https://nsp.pay.naver.com/payments'
    });
});

// 삼성페이 결제 API
app.post('/api/payment/samsungpay', (req, res) => {
    const { amount, items } = req.body;
    
    // 실제 구현시에는 삼성페이 API를 호출
    res.json({
        success: true,
        next_redirect_pc_url: 'https://pay.samsung.com/payments'
    });
});

// 결제 검증 API
app.post('/api/payment/verify', async (req, res) => {
    const { imp_uid, merchant_uid } = req.body;

    try {
        // 1. 액세스 토큰 발급
        const getToken = await axios.post('https://api.iamport.kr/users/getToken', {
            imp_key: process.env.IAMPORT_API_KEY,
            imp_secret: process.env.IAMPORT_API_SECRET
        });
        const { access_token } = getToken.data.response;

        // 2. 결제 정보 조회
        const getPaymentData = await axios.get(`https://api.iamport.kr/payments/${imp_uid}`, {
            headers: { "Authorization": access_token }
        });
        const paymentData = getPaymentData.data.response;

        // 3. 결제 검증
        const { amount, status } = paymentData;
        if (status === 'paid') {
            // 결제 금액 검증
            const order = await Order.findOne({ merchant_uid });
            if (order.amount === amount) {
                // 결제 성공 처리
                await Order.updateOne(
                    { merchant_uid },
                    { $set: { status: 'paid', imp_uid } }
                );
                res.json({ success: true });
            } else {
                // 결제 금액 불일치
                throw new Error('결제 금액 불일치');
            }
        } else {
            throw new Error('결제 실패');
        }
    } catch (error) {
        res.status(400).json({
            success: false,
            message: error.message
        });
    }
});

app.listen(port, () => {
    console.log(`서버가 http://localhost:${port} 에서 실행중입니다.`);
}); 