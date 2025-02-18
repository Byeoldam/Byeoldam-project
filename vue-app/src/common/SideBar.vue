<template>
    <div class="sidebar">
        <nav>
            <router-link :to="{ name: 'main'}" class="nav-item">
                <i class="fas fa-home"></i>
                <span>메인페이지</span>
            </router-link>
            <router-link :to="{ name: 'important'}" class="nav-item">
                <i class="fas fa-star"></i>
                <span>중요페이지</span>
            </router-link>
            <router-link :to="{ name: 'recommended'}" class="nav-item">
                <i class="fas fa-thumbs-up"></i>
                <span>추천페이지</span>
            </router-link>
            <router-link :to="{ name: 'personal'}" class="nav-item">
                <i class="fas fa-folder"></i>
                <span>개인 컬렉션</span>
            </router-link>
            <router-link :to="{ name: 'shared'}" class="nav-item">
                <i class="fas fa-share-alt"></i>
                <span>공유 컬렉션</span>
            </router-link>
            <router-link :to="{ name: 'search'}" class="nav-item">
                <i class="fas fa-search"></i>
                <span>검색</span>
            </router-link>
            <router-link :to="{ name: 'feed'}" class="nav-item">
                <i class="fas fa-rss"></i>
                <span>피드</span>
            </router-link>
            <router-link :to="{ name: 'old'}" class="nav-item">
                <i class="fas fa-clock"></i>
                <span>오래된 북마크</span>
            </router-link>
        </nav>
    </div>
</template>

<script setup>
// Font Awesome을 사용하기 위해서는 별도 설치가 필요합니다
</script>

<style scoped>
.sidebar {
    width: 240px;
    background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
    color: white;
    height: 100%;
    padding: 20px;
    border-right: 1px solid #e9ecef;
    flex-shrink: 0;
}

.nav-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    border-radius: 8px;
    margin-bottom: 8px;
}

.nav-item:hover {
    background-color: rgba(255, 255, 255, 0.1);
    color: #ffd900bf;  /* 키 컬러로 변경 */
}

.nav-item.router-link-active {
    background-color: rgba(255, 255, 255, 0.1);
    color: #ffd900f5;  /* 키 컬러로 변경 */
    font-weight: bold;
}

.nav-item i {
    margin-right: 12px;
    width: 20px;
}

.nav-item span {
    font-size: 14px;
}
</style>