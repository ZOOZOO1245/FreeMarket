const errorHandler = (err, req, res, next) => {
    console.error(err.stack);

    if (err.name === 'ValidationError') {
        return res.status(400).json({
            error: '입력값 검증 실패',
            details: err.errors
        });
    }

    if (err.name === 'PaymentError') {
        return res.status(400).json({
            error: '결제 처리 실패',
            message: err.message
        });
    }

    res.status(500).json({
        error: '서버 오류',
        message: process.env.NODE_ENV === 'production' ? 
            '서버에서 오류가 발생했습니다.' : err.message
    });
};

module.exports = errorHandler; 