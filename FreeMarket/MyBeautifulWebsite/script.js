console.log("웹사이트가 로드되었습니다.");

fetch('http://localhost:3000/api/data')
    .then(response => response.json())
    .then(data => {
        console.log(data.message);
        document.querySelector('main').innerHTML += `<p>${data.message}</p>`;
    })
    .catch(error => console.error('Error:', error));

    // 로그인 및 회원가입 모달 열기/닫기
const loginBtn = document.getElementById('loginBtn');
const signupBtn = document.getElementById('signupBtn');
const loginModal = document.getElementById('loginModal');
const signupModal = document.getElementById('signupModal');
const closeLogin = document.getElementById('closeLogin');
const closeSignup = document.getElementById('closeSignup');

// 로그인 버튼 클릭 시 모달 열기
loginBtn.onclick = function() {
    loginModal.style.display = 'block';
}

// 회원가입 버튼 클릭 시 모달 열기
signupBtn.onclick = function() {
    signupModal.style.display = 'block';
}

// 로그인 모달 닫기
closeLogin.onclick = function() {
    loginModal.style.display = 'none';
}

// 회원가입 모달 닫기
closeSignup.onclick = function() {
    signupModal.style.display = 'none';
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target === loginModal) {
        loginModal.style.display = 'none';
    }
    if (event.target === signupModal) {
        signupModal.style.display = 'none';
    }
}