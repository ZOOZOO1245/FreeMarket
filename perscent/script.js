// 스크롤 시 헤더 스타일 변경
window.addEventListener('scroll', () => {
    const header = document.querySelector('header');
    if (window.scrollY > 50) {
        header.classList.add('scrolled');
    } else {
        header.classList.remove('scrolled');
    }
});

// 제품 데이터
const products = {
    all: [
        {
            id: 'sc1',
            name: 'Hydrating Toner',
            price: 32000,
            image: 'https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8',
            category: 'toner',
            isNew: false
        },
        {
            id: 'sc2',
            name: 'Vitamin C Serum',
            price: 45000,
            image: 'https://images.unsplash.com/photo-1556228578-0d85b1a4d571',
            category: 'serum',
            isNew: true
        },
        {
            id: 'sc3',
            name: 'Moisturizing Cream',
            price: 38000,
            image: 'https://images.unsplash.com/photo-1556228720-195a672e8a03',
            category: 'cream',
            isNew: false
        },
        {
            id: 'sc4',
            name: 'Peptide Serum',
            price: 52000,
            image: 'https://images.unsplash.com/photo-1556228841-a4c3eb81517a',
            category: 'serum',
            isNew: true
        },
        {
            id: 'sc5',
            name: 'Gentle Cleanser',
            price: 28000,
            image: 'https://images.unsplash.com/photo-1556228453-efd6c1ff04f6',
            category: 'cleanser',
            isNew: true
        }
    ]
};

// 신상품 필터링
products.new = products.all.filter(product => product.isNew);

// 장바구니 관련 함수들
let cart = [];

// 장바구니에 제품 추가
function addToCart(productId) {
    const product = products.all.find(p => p.id === productId);
    if (product) {
        // 이미 장바구니에 있는 상품인지 확인
        const existingItem = cart.find(item => item.id === productId);
        
        if (existingItem) {
            existingItem.quantity = (existingItem.quantity || 1) + 1;
            showCartNotification('수량이 증가되었습니다.');
        } else {
            cart.push({ ...product, quantity: 1 });
            showCartNotification('장바구니에 추가되었습니다.');
        }
        
        saveCart();
        updateCartDisplay();
        updateCartCount();
    }
}

// 장바구니 알림 표시
function showCartNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'cart-notification';
    notification.innerHTML = `
        <div class="notification-content">
            <p>${message}</p>
            <a href="cart.html" class="view-cart-btn">장바구니 보기</a>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // 3초 후 알림 제거
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// 장바구니 수량 업데이트
function updateCartCount() {
    const cartCount = document.createElement('span');
    cartCount.className = 'cart-count';
    cartCount.textContent = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
    
    const cartLink = document.querySelector('a[href="cart.html"]');
    const existingCount = cartLink.querySelector('.cart-count');
    
    if (existingCount) {
        existingCount.remove();
    }
    
    cartLink.appendChild(cartCount);
}

// 장바구니 저장
function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cart));
}

// 장바구니 불러오기
function loadCart() {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
        cart = JSON.parse(savedCart);
        updateCartCount();
    }
}

// 페이지 로드 시 장바구니 불러오기
document.addEventListener('DOMContentLoaded', () => {
    loadCart();
    displayProducts('all'); // 초기 제품 표시
    updateCartDisplay(); // 이 부분 추가

    // 카테고리 버튼 이벤트 리스너
    const categoryButtons = document.querySelectorAll('.category-btn');
    categoryButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 활성 버튼 스타일 변경
            categoryButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // 선택된 카테고리의 제품 표시
            const category = button.dataset.category;
            displayProducts(category);
        });
    });

    // URL에 제품 ID가 있으면 해당 제품 하이라이트
    const productId = window.location.hash.slice(1);
    if (productId) {
        const productElement = document.querySelector(`[data-product-id="${productId}"]`);
        if (productElement) {
            productElement.scrollIntoView({ behavior: 'smooth' });
            productElement.classList.add('highlighted');
        }
    }
});

// 로그인 모달 관련 기능
const modal = document.getElementById('loginModal');
const loginBtn = document.getElementById('loginBtn');
const closeBtn = document.querySelector('.close');
const authTabs = document.querySelectorAll('.auth-tab');
const authForms = document.querySelectorAll('.auth-form');

// 로그인 버튼 클릭 시 모달 열기
loginBtn.onclick = function() {
    modal.style.display = "block";
}

// X 버튼 클릭 시 모달 닫기
closeBtn.onclick = function() {
    modal.style.display = "none";
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

// 탭 전환 기능
authTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        // 활성 탭 변경
        authTabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        
        // 폼 전환
        const targetForm = tab.dataset.tab;
        authForms.forEach(form => {
            form.classList.remove('active');
            if (form.id === `${targetForm}Form`) {
                form.classList.add('active');
            }
        });
    });
});

// 로그인 폼 제출
document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const email = this.querySelector('input[type="email"]').value;
    const password = this.querySelector('input[type="password"]').value;
    
    // 여기에 로그인 API 호출 로직 추가
    console.log('Login attempt:', { email, password });
});

// 회원가입 폼 제출
document.getElementById('registerForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = {
        name: this.querySelector('input[type="text"]').value,
        email: this.querySelector('input[type="email"]').value,
        password: this.querySelectorAll('input[type="password"]')[0].value,
        confirmPassword: this.querySelectorAll('input[type="password"]')[1].value
    };
    
    // 비밀번호 확인
    if (formData.password !== formData.confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    
    // 여기에 회원가입 API 호출 로직 추가
    console.log('Register attempt:', formData);
});

// 제품 표시 함수
function displayProducts(category = 'all') {
    const productGrid = document.querySelector('.products-grid') || document.querySelector('.product-grid');
    if (!productGrid) return;

    const productsToShow = products[category] || products.all;
    
    productGrid.innerHTML = productsToShow.map(product => `
        <div class="product-card">
            ${product.isNew ? '<span class="new-badge">New</span>' : ''}
            <img src="${product.image}" alt="${product.name}" class="product-image">
            <div class="product-info">
                <h4>${product.name}</h4>
                <p class="category">${product.category}</p>
                <p class="price">₩${product.price.toLocaleString()}</p>
                <button class="btn-secondary" onclick="addToCart('${product.id}')">
                    Add to Cart
                </button>
            </div>
        </div>
    `).join('');
}

// 결제 관련 함수들
function processPayment() {
    const selectedPayment = document.querySelector('input[name="payment"]:checked');
    if (!selectedPayment) {
        alert('결제 수단을 선택해주세요.');
        return;
    }

    switch(selectedPayment.value) {
        case 'kakaopay':
            processKakaoPay();
            break;
        case 'naverpay':
            processNaverPay();
            break;
        case 'samsungpay':
            processSamsungPay();
            break;
        case 'card':
            showCardPaymentModal();
            break;
    }
}

function processKakaoPay() {
    const totalAmount = calculateTotal();
    const items = cart.map(item => ({
        item_name: item.name,
        quantity: item.quantity || 1,
        total_amount: item.price * (item.quantity || 1)
    }));
    
    // 카카오페이 API 요청
    fetch('/api/payment/kakaopay', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `KakaoAK ${KAKAO_ADMIN_KEY}` // 카카오 어드민 키
        },
        body: JSON.stringify({
            cid: 'TC0ONETIME', // 가맹점 코드
            partner_order_id: generateOrderId(),
            partner_user_id: getUserId(),
            item_name: items[0].item_name + (items.length > 1 ? ` 외 ${items.length - 1}건` : ''),
            quantity: items.reduce((sum, item) => sum + item.quantity, 0),
            total_amount: totalAmount,
            tax_free_amount: 0,
            approval_url: `${window.location.origin}/payment/success`,
            cancel_url: `${window.location.origin}/payment/cancel`,
            fail_url: `${window.location.origin}/payment/fail`
        })
    })
    .then(response => response.json())
    .then(data => {
        window.location.href = data.next_redirect_pc_url;
    })
    .catch(handlePaymentError);
}

function processNaverPay() {
    const totalAmount = calculateTotal();
    const items = cart.map(item => ({
        categoryType: "PRODUCT",
        categoryId: item.category,
        uid: item.id,
        name: item.name,
        count: item.quantity || 1,
        payReferrer: "NAVER_BOOKING",
        sellerId: "STORE_ID", // 스토어 ID
        paymentId: generateOrderId()
    }));

    // 네이버페이 SDK 초기화
    Naver.Pay.create({
        mode: 'development', // 실제 운영시 'production'으로 변경
        clientId: NAVER_CLIENT_ID, // 네이버페이 클라이언트 ID
        openType: 'popup',
        payType: 'payment',
        products: items,
        totalPayAmount: totalAmount,
        taxScopeAmount: totalAmount,
        taxExScopeAmount: 0,
        returnUrl: `${window.location.origin}/payment/success`,
        failUrl: `${window.location.origin}/payment/fail`,
        onSuccess: function(response) {
            handlePaymentSuccess(response);
        },
        onFailure: function(error) {
            handlePaymentError(error);
        }
    });
}

function processSamsungPay() {
    const totalAmount = calculateTotal();
    
    // 삼성페이 SDK 초기화
    SamsungPay.init({
        storeId: SAMSUNG_STORE_ID, // 삼성페이 스토어 ID
        serviceId: SAMSUNG_SERVICE_ID, // 삼성페이 서비스 ID
        channelId: SAMSUNG_CHANNEL_ID, // 삼성페이 채널 ID
        mode: 'development' // 실제 운영시 'production'으로 변경
    });

    // 결제 요청
    SamsungPay.requestPayment({
        orderNumber: generateOrderId(),
        paymentInfo: {
            productName: cart[0].name + (cart.length > 1 ? ` 외 ${cart.length - 1}건` : ''),
            productCount: cart.reduce((sum, item) => sum + (item.quantity || 1), 0),
            amount: totalAmount,
            currency: 'KRW'
        },
        showLoadingScreen: true,
        success: function(response) {
            handlePaymentSuccess(response);
        },
        fail: function(error) {
            handlePaymentError(error);
        }
    });
}

// 카드 결제 처리 함수 수정 (KG이니시스 예시)
function processCardPayment(cardData) {
    const totalAmount = calculateTotal();
    
    // KG이니시스 SDK 초기화
    const IMP = window.IMP;
    IMP.init(INICIS_MERCHANT_ID); // 가맹점 식별코드

    IMP.request_pay({
        pg: 'html5_inicis', // PG사 구분
        pay_method: 'card',
        merchant_uid: generateOrderId(),
        name: cart[0].name + (cart.length > 1 ? ` 외 ${cart.length - 1}건` : ''),
        amount: totalAmount,
        buyer_email: getUserEmail(),
        buyer_name: getUserName(),
        buyer_tel: getUserPhone(),
        buyer_addr: getUserAddress(),
        card_quota: [1, 2, 3, 4, 5, 6], // 할부개월 설정
        card_number: cardData.number.replace(/-/g, ''),
        expiry: cardData.expiry.replace('/', ''),
        birth: getUserBirth(),
        pwd_2digit: cardData.cvc
    }, function(response) {
        if (response.success) {
            handlePaymentSuccess(response);
        } else {
            handlePaymentError(response);
        }
    });
}

// 유틸리티 함수들
function generateOrderId() {
    return 'ORDER_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

function handlePaymentSuccess(response) {
    // 결제 성공 처리
    fetch('/api/payment/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(response)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('결제가 완료되었습니다.');
            clearCart();
            window.location.href = '/order-complete.html';
        } else {
            throw new Error('결제 검증 실패');
        }
    })
    .catch(handlePaymentError);
}

function handlePaymentError(error) {
    console.error('Payment error:', error);
    alert('결제 처리 중 오류가 발생했습니다.');
}

// 카드 결제 모달 닫기 함수
function closeCardModal() {
    const modal = document.getElementById('cardPaymentModal');
    modal.style.display = 'none';
    // 폼 초기화
    document.getElementById('cardPaymentForm').reset();
}

// 카드 결제 모달 표시 함수 수정
function showCardPaymentModal() {
    const modal = document.getElementById('cardPaymentModal');
    modal.style.display = 'block';

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        if (event.target == modal) {
            closeCardModal();
        }
    }

    // ESC 키 누를 때 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeCardModal();
        }
    });

    // 카드 번호 입력 포맷팅
    const cardNumber = document.getElementById('cardNumber');
    cardNumber.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 0) {
            value = value.match(new RegExp('.{1,4}', 'g')).join('-');
        }
        e.target.value = value;
    });

    // 유효기간 입력 포맷팅
    const cardExpiry = document.getElementById('cardExpiry');
    cardExpiry.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 2) {
            value = value.slice(0, 2) + '/' + value.slice(2);
        }
        e.target.value = value;
    });

    // CVC 숫자만 입력
    const cardCvc = document.getElementById('cardCvc');
    cardCvc.addEventListener('input', function(e) {
        e.target.value = e.target.value.replace(/\D/g, '');
    });
}

// 카드 결제 폼 제출 처리 수정
document.getElementById('cardPaymentForm')?.addEventListener('submit', function(e) {
    e.preventDefault();
    
    const cardData = {
        bank: document.getElementById('cardBank').value,
        number: document.getElementById('cardNumber').value,
        expiry: document.getElementById('cardExpiry').value,
        cvc: document.getElementById('cardCvc').value
    };

    // 카드사 선택 확인
    if (!cardData.bank) {
        alert('카드사를 선택해주세요.');
        return;
    }

    // 실제 구현시에는 카드 결제 API를 호출
    fetch('/api/payment/card', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            cardData,
            amount: calculateTotal(),
            items: cart
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('결제가 완료되었습니다.');
            clearCart();
            closeCardModal();
            window.location.href = '/order-complete.html';
        } else {
            alert('결제 처리 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        alert('결제 처리 중 오류가 발생했습니다.');
        console.error('Payment error:', error);
    });
});

function calculateTotal() {
    const subtotal = cart.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0);
    return subtotal + (subtotal > 0 ? 3000 : 0); // 배송비 포함
}

function clearCart() {
    cart = [];
    saveCart();
    updateCartDisplay();
}

// 검색 처리 함수
function handleSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchTerm = searchInput.value.toLowerCase().trim();
    
    // 검색어가 없으면 모든 제품 표시
    if (!searchTerm) {
        displayProducts('all');
        return;
    }

    // 검색어로 제품 필터링
    const filteredProducts = products.all.filter(product => 
        product.name.toLowerCase().includes(searchTerm) ||
        product.category.toLowerCase().includes(searchTerm)
    );

    // 필터링된 제품 표시
    displayFilteredProducts(filteredProducts, searchTerm);
}

// 필터링된 제품 표시 함수
function displayFilteredProducts(filteredProducts, searchTerm) {
    const productGrid = document.querySelector('.products-grid') || document.querySelector('.product-grid');
    if (!productGrid) return;

    if (filteredProducts.length === 0) {
        productGrid.innerHTML = `
            <div class="no-results">
                <p>No products found for "${searchTerm}"</p>
            </div>
        `;
        return;
    }

    productGrid.innerHTML = filteredProducts.map(product => `
        <div class="product-card">
            ${product.isNew ? '<span class="new-badge">New</span>' : ''}
            <img src="${product.image}" alt="${product.name}" class="product-image">
            <div class="product-info">
                <h4>${highlightSearchTerm(product.name, searchTerm)}</h4>
                <p class="category">${highlightSearchTerm(product.category, searchTerm)}</p>
                <p class="price">₩${product.price.toLocaleString()}</p>
                <button class="btn-secondary" onclick="addToCart('${product.id}')">
                    Add to Cart
                </button>
            </div>
        </div>
    `).join('');
}

// 검색어 하이라이트 함수
function highlightSearchTerm(text, searchTerm) {
    if (!searchTerm) return text;
    const regex = new RegExp(`(${searchTerm})`, 'gi');
    return text.replace(regex, '<mark>$1</mark>');
}

// 장바구니 표시 함수
function updateCartDisplay() {
    const cartItemsList = document.querySelector('.cart-items-list');
    const emptyCart = document.querySelector('.empty-cart');
    
    if (!cartItemsList || !emptyCart) return;

    if (cart.length === 0) {
        emptyCart.style.display = 'block';
        cartItemsList.style.display = 'none';
        updateCartSummary(0);
        return;
    }

    emptyCart.style.display = 'none';
    cartItemsList.style.display = 'block';

    // 장바구니 아이템 표시
    cartItemsList.innerHTML = cart.map(item => `
        <div class="cart-item">
            <img src="${item.image}" alt="${item.name}" class="cart-item-image">
            <div class="cart-item-info">
                <h4>${item.name}</h4>
                <p class="category">${item.category}</p>
                <p class="cart-item-price">₩${(item.price * (item.quantity || 1)).toLocaleString()}</p>
                <div class="quantity-controls">
                    <button onclick="updateQuantity('${item.id}', ${(item.quantity || 1) - 1})">-</button>
                    <span>${item.quantity || 1}</span>
                    <button onclick="updateQuantity('${item.id}', ${(item.quantity || 1) + 1})">+</button>
                </div>
            </div>
            <button class="remove-item" onclick="removeFromCart('${item.id}')">×</button>
        </div>
    `).join('');

    // 합계 업데이트
    const subtotal = cart.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0);
    updateCartSummary(subtotal);
}

// 장바구니 수량 업데이트 함수
function updateQuantity(productId, newQuantity) {
    if (newQuantity < 1) {
        removeFromCart(productId);
        return;
    }

    const cartItem = cart.find(item => item.id === productId);
    if (cartItem) {
        cartItem.quantity = newQuantity;
        saveCart();
        updateCartDisplay();
        updateCartCount();
    }
}

// 장바구니에서 제품 제거
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    saveCart();
    updateCartDisplay();
    updateCartCount();
    showCartNotification('상품이 장바구니에서 제거되었습니다.');
}

// 장바구니 합계 업데이트
function updateCartSummary(subtotal) {
    const subtotalElem = document.querySelector('.subtotal');
    const totalElem = document.querySelector('.total-price');
    if (!subtotalElem || !totalElem) return;

    const shipping = subtotal > 0 ? 3000 : 0;
    const total = subtotal + shipping;

    subtotalElem.textContent = `₩${subtotal.toLocaleString()}`;
    totalElem.textContent = `₩${total.toLocaleString()}`;
} 