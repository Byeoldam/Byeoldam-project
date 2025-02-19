<template>
    <div class="intro-container">
        <div class="stars"></div>
        <div class="stars2"></div>
        <div class="stars3"></div>
        <!-- 헤더 -->
        <header class="header">
            <div class="header-content">
                <h1 class="logo">
                    <span class="logo-title">별담</span>
                    <span class="logo-subtitle"> :별을 담다</span>
                </h1>
                <div class="auth-buttons">
                    <button class="register-btn">
                        <RouterLink :to="{ name: 'register' }">회원가입</RouterLink>
                    </button>
                    <button class="login-btn">
                        <RouterLink :to="{ name: 'login' }">로그인</RouterLink>
                    </button>
                </div>
            </div>
        </header>

        <!-- 히어로 섹션 -->
        <section class="hero-section">
            <div class="hero-content">
                <h2 class="hero-title">
                    당신의 웹서핑을<br>
                    <span class="highlight">더욱 스마트하게</span>
                </h2>
                <p class="hero-description">
                    원하는 페이지를 북마크하고 실시간 알림으로<br>
                    중요한 정보를 놓치지 마세요
                </p>
                <button class="start-btn">지금 익스텐션 설치하러 가기</button>
            </div>
        </section>

        <!-- 기능 소개 섹션 -->
        <section class="features-section">
            <div class="features-content">
                <h3 class="features-title">주요 기능</h3>
                <div class="features-grid">
                    <div class="feature-card">
                        <div class="feature-icon">🔖</div>
                        <h4 class="feature-card-title">스마트 북마크</h4>
                        <p class="feature-description">특정 페이지를 저장하고 원하는 정보를 쉽게 찾아보세요</p>
                    </div>
                    <div class="feature-card">
                        <div class="feature-icon">🔔</div>
                        <h4 class="feature-card-title">실시간 알림</h4>
                        <p class="feature-description">개인 맞춤형 알림으로 중요한 업데이트를 놓치지 마세요</p>
                    </div>
                    <div class="feature-card">
                        <div class="feature-icon">✨</div>
                        <h4 class="feature-card-title">직관적인 UI</h4>
                        <p class="feature-description">간단하고 깔끔한 인터페이스로 누구나 쉽게 사용할 수 있습니다</p>
                    </div>
                    <div class="feature-card">
                        <div class="feature-icon">🔒</div>
                        <h4 class="feature-card-title">안전한 보관</h4>
                        <p class="feature-description">데이터를 안전하게 보관하고 관리할 수 있습니다</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- 푸터 -->
        <footer class="footer">
            <div class="footer-content">
                <p>Copyright© 별담. All rights reserved.</p>
            </div>
        </footer>
    </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';

function generateStarsShadow(count, size) {
    let shadows = [];
    for(let i = 0; i < count; i++) {
        const x = Math.floor(Math.random() * 2000);
        const y = Math.floor(Math.random() * 2000);
        shadows.push(`${x}px ${y}px #FFF`);
    }
    return shadows.join(', ');
}
</script>

<style scoped>
.intro-container {
    height: 100vh;
    background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
    overflow: hidden;
    position: relative;
    display: flex;
    flex-direction: column;
}

@keyframes animStar {
    from {
        transform: translateY(0);
    }
    to {
        transform: translateY(-2000px);
    }
}

.stars {
    width: 1px;
    height: 1px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(700)');
    animation: animStar 50s linear infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars2 {
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(200)');
    animation: animStar 100s linear infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars3 {
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(100)');
    animation: animStar 150s linear infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 1px;
    height: 1px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(700)');
}

.stars2:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(200)');
}

.stars3:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(100)');
}

/* 기존 스타일에 z-index 추가 */
.header {
    position: fixed;
    width: 100%;
    padding: clamp(0.5rem, 1vw, 1rem) clamp(1.5rem, 3vw, 3rem);
    height: 10vh;
    z-index: 10;
    /* background-color: rgba(30, 27, 75, 0.9); */
    /* backdrop-filter: blur(12px); */
}

.header-content {
    max-width: min(80rem, 90vw);
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.logo {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
}

.logo-title {
    font-size: clamp(1.3rem, 2.5vw, 2.2rem);
    font-weight: bold;
    color: #ffd900f5;
}

.logo-subtitle {
    font-size: clamp(0.8rem, 1.2vw, 1.1rem);
    color: rgba(255, 255, 255, 0.7);
    font-weight: normal;
}

.auth-buttons {
    display: flex;
    gap: 1rem;
}

.register-btn, .login-btn {
    padding: clamp(0.4rem, 0.8vw, 0.6rem) clamp(1rem, 1.5vw, 1.8rem);
    font-size: clamp(0.8rem, 1vw, 1.1rem);
    font-weight: bold;
    border-radius: 9999px;
    transition: all 0.3s ease;
}

.register-btn {
    background-color: #FFD900F5;
    color: #1E1B4B;
}

.register-btn:hover {
    background-color: rgba(255, 217, 0, 0.599);
    box-shadow: 0 6px 8px rgba(255, 217, 0, 0.2);
}

.login-btn {
    border: 2px solid #FFD900F5;
    color: #FFD900F5;
}

.login-btn:hover {
    background-color: rgba(255, 217, 0, 0.599);
    box-shadow: 0 6px 8px rgba(255, 217, 0, 0.2);
}

.hero-section {
    padding-top: 0;
    height: 45vh;
    flex: 1;
    display: flex;
    align-items: center;
}

.hero-content {
    max-width: min(80rem, 90vw);
    margin: 0 auto;
    text-align: center;
}

.hero-title {
    font-size: clamp(2.5rem, 4vw, 4.5rem);
    font-weight: bold;
    color: white;
    margin-bottom: clamp(0.5rem, 1vw, 1rem);
}

.highlight {
    color: #FFD900F5;
}

.hero-description {
    font-size: clamp(0.9rem, 1.2vw, 1.3rem);
    color: rgb(209, 213, 219);
    margin-bottom: clamp(1rem, 2vw, 2rem);
    line-height: 1.7;
}

.start-btn {
    padding: clamp(0.6rem, 1vw, 1rem) clamp(1.2rem, 2vw, 2.5rem);
    background-color: #FFD900F5;
    color: #1E1B4B;
    font-size: clamp(0.9rem, 1.3vw, 1.5rem);
    font-weight: bold;
    border-radius: 9999px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 6px rgba(255, 217, 0, 0.1);
    cursor: pointer;
}

.start-btn:hover {
    background-color: rgba(255, 217, 0, 0.599);
    box-shadow: 0 6px 8px rgba(255, 217, 0, 0.2);
}

.features-section {
    height: 40vh;
    padding: clamp(1rem, 2vw, 2rem);
}

.features-content {
    max-width: min(80rem, 90vw);
    margin: 0 auto;
}

.features-title {
    font-size: clamp(1.2rem, 2vw, 2rem);
    font-weight: bold;
    color: white;
    text-align: center;
    margin-bottom: clamp(1rem, 2vw, 2rem);
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: clamp(1rem, 1.5vw, 2rem);
}

@media (min-width: 768px) {
    .features-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (min-width: 1024px) {
    .features-grid {
        grid-template-columns: repeat(4, 1fr);
    }
}

.feature-card {
    background-color: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(12px);
    padding: clamp(0.8rem, 1.5vw, 1.5rem);
    border-radius: 1rem;
    transition: transform 0.3s ease;
}

.feature-card:hover {
    transform: scale(1.05);
}

.feature-icon {
    width: clamp(2rem, 3vw, 3rem);
    height: clamp(2rem, 3vw, 3rem);
    background-color: #F5f5f5;
    border-radius: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: clamp(1rem, 1.5vw, 1.8rem);
    margin-bottom: clamp(0.5rem, 0.8vw, 1rem);
}

.feature-card-title {
    font-size: clamp(0.9rem, 1.2vw, 1.3rem);
    font-weight: bold;
    color: white;
    margin-bottom: clamp(0.3rem, 0.5vw, 0.8rem);
}

.feature-description {
    color: rgb(209, 213, 219);
    font-size: clamp(0.8rem, 1vw, 1.1rem);
    line-height: 1.3;
}

.footer {
    height: 5vh;
    padding: clamp(0.5rem, 1vw, 1rem);
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    position: relative;
    z-index: 5;
}

.footer-content {
    max-width: 80rem;
    margin: 0 auto;
    text-align: center;
    color: rgb(156, 163, 175);
}

/* 별이 반짝이는 효과 */
@keyframes twinkle {
    0% { opacity: 0.2; }
    50% { opacity: 1; }
    100% { opacity: 0.2; }
}

.stars, .stars2, .stars3 {
    &::before {
        content: '';
        position: absolute;
        width: 100%;
        height: 100%;
        animation: twinkle 4s infinite;
    }
}

/* 대형 화면 최적화 */
@media (min-width: 1800px) {
    .header-content,
    .hero-content,
    .features-content {
        max-width: 100rem;
    }
}

/* 별 애니메이션 크기 조정 */
.stars, .stars2, .stars3 {
    width: clamp(1px, 0.1vw, 2px);
    height: clamp(1px, 0.1vw, 2px);
}

.stars2 {
    width: clamp(2px, 0.15vw, 3px);
    height: clamp(2px, 0.15vw, 3px);
}

.stars3 {
    width: clamp(3px, 0.2vw, 4px);
    height: clamp(3px, 0.2vw, 4px);
}
</style>
