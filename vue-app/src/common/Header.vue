<template>
    <header class="header">
        <div class="logo">
            <router-link :to="{ name: 'main'}" class="logo-link">
                <img src="@/assets/logo.png" alt="별담 로고" class="logo-image">
                <div class="logo-text">
                    <span class="logo-title">별담</span>
                    <span class="logo-subtitle">:별을 담다</span>
                </div>
            </router-link>
        </div>
        
        <div class="auth-section">
            <!-- 로그아웃 버튼 추가 -->
            <div class="profile-area" @click="goToMyPage">
                <div>
                    <p class="welcome-message">{{ usernickname }} 님 환영합니다</p>
                </div>
                <img 
                    :src="userProfile || defaultProfileImage" 
                    alt="프로필 사진" 
                    class="profile-image"
                />
            </div>
            <button @click="logout" class="logout-button">로그아웃</button>
        </div>
    </header>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();
const usernickname = ref(userStore.user?.nickname || '');
const defaultProfileImage = 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png';
const userProfile = ref(userStore.user?.profileUrl || defaultProfileImage);

// 로그아웃 함수 수정
const logout = async () => {
    try {
        await userStore.logout();
        ElMessage.success('로그아웃되었습니다.');
        setTimeout(() => {
            router.push({ name: 'intro' });
        }, 100);
    } catch (error) {
        ElMessage.error('로그아웃 중 오류가 발생했습니다.');
    }
};

// 마이페이지 이동 함수 추가
const goToMyPage = () => {
    router.push({ name: 'mypage' });
};
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 20px;
    background-color: #1E1B4B;  /* 베이스 컬러 */
    padding: 1rem 1rem;
    background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
    /* box-shadow: 0 2px 4px rgba(0,0,0,0.1); */
    height: 60px;  /* 헤더 높이 고정 */
    color: white;
}

.logo {
    display: flex;
    align-items: center;
    gap: 10px;
}

.logo-image {
    width: 40px;  /* 로고 이미지 크기 조정 */
    height: 40px;  /* 로고 이미지 크기 조정 */
    object-fit: contain;  /* 이미지 비율 유지 */
}

.auth-section {
    display: flex;
    align-items: center;
    gap: 13px;
}

.welcome-message {
    color: rgba(255, 255, 255, 0.901);
}

.profile-image {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    object-fit: cover;
}

.logo-link {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    text-decoration: none;
    color: inherit;
    white-space: nowrap;  
    margin-top: 5px;
    margin-left: 3px;
}

.logo-text {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
    margin-top: 4px;
}

.logo-title {
    font-size: 1.8rem;
    font-weight: bold;
    color: #ffd900f5;
}

.logo-subtitle {
    font-size: 1rem;
    color: rgba(255, 255, 255, 0.7);
    font-weight: normal;
}

.logout-button {
    padding: 5px 10px;
    background-color: #c34d4d;
    color: rgba(255, 255, 255, 0.901);
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: all 0.3s ease;
}

.logout-button:hover {
    background-color: rgb(212, 110, 110);
}

.profile-area {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 5px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.profile-area:hover {
    background-color: rgba(255, 255, 255, 0.1);
}
</style>
