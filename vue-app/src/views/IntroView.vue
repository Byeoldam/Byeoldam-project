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

// 별을 생성하는 함수
function generateStarsShadow(count) {
    let shadows = [];
    for(let i = 0; i < count; i++) {
        // 범위를 더 넓게 설정 (4000px)
        const x = Math.floor(Math.random() * 4000) - 2000; // -2000px ~ 2000px
        const y = Math.floor(Math.random() * 4000) - 2000; // -2000px ~ 2000px
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
    top: 50%;
    left: 50%;
}

.stars2 {
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(200)');
    animation: animStar 100s linear infinite;
    position: absolute;
    top: 50%;
    left: 50%;
}

.stars3 {
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: v-bind('generateStarsShadow(100)');
    animation: animStar 150s linear infinite;
    position: absolute;
    top: 50%;
    left: 50%;
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
    padding: 0.8rem 1.5rem;
    z-index: 10;
    background-color: rgba(30, 27, 75, 0.9);
    backdrop-filter: blur(12px);
}

.header-content {
    max-width: 80rem;
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
    font-size: 1.875rem;
    font-weight: bold;
    color: white;
}

.logo-subtitle {
    font-size: 1rem;
    color: rgba(255, 255, 255, 0.7);
    font-weight: normal;
}

.auth-buttons {
    display: flex;
    gap: 1rem;
}

.register-btn, .login-btn {
    padding: 0.5rem 1.5rem;
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
    padding-top: 4rem;
    flex: 1;
    display: flex;
    align-items: center;
}

.hero-content {
    max-width: 80rem;
    margin: 0 auto;
    text-align: center;
}

.hero-title {
    font-size: 2.5rem;
    font-weight: bold;
    color: white;
    margin-bottom: 1rem;
}

.highlight {
    color: #FFD900F5;
}

.hero-description {
    font-size: 1rem;
    color: rgb(209, 213, 219);
    margin-bottom: 2rem;
    line-height: 1.7;
}

.start-btn {
    padding: 1rem 2rem;
    background-color: #FFD900F5;
    color: #1E1B4B;
    font-size: 1.25rem;
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
    padding: 2rem 1.5rem;
}

.features-content {
    max-width: 80rem;
    margin: 0 auto;
}

.features-title {
    font-size: 1.5rem;
    font-weight: bold;
    color: white;
    text-align: center;
    margin-bottom: 2rem;
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: 2rem;
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
    padding: 1.5rem;
    border-radius: 1rem;
    transition: transform 0.3s ease;
}

.feature-card:hover {
    transform: scale(1.05);
}

.feature-icon {
    width: 3rem;
    height: 3rem;
    background-color: #F5f5f5;
    border-radius: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    margin-bottom: 1rem;
}

.feature-card-title {
    font-size: 1rem;
    font-weight: bold;
    color: white;
    margin-bottom: 0.5rem;
}

.feature-description {
    color: rgb(209, 213, 219);
    font-size: 0.875rem;
}

.footer {
    padding: 1rem;
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
</style>
