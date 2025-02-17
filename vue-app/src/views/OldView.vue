<template>
    <div class="layout">
        <Header class="header"/>
        <div class="content-wrapper">
            <SideBar class="sidebar"/>
            <div class="main-content">
                <div class="body">
                    <div class="page-header">
                        <div class="header-content">
                            <div class="title-section">
                                <i class="fas fa-clock title-icon"></i>
                                <h2 class="title">오래된 북마크</h2>
                            </div>
                            <p class="description">오랜기간 읽지 않은 북마크들을 모아서 보여드립니다</p>
                        </div>
                    </div>
                    
                    <div v-if="!bookmarkResults.length" class="empty-state">
                        <i class="fas fa-star empty-icon"></i>
                        <p class="empty-text">오랜기간 읽지않은 북마크가 없습니다.</p>
                        <p class="empty-description">북마크 등록 후 30일이 지난 북마크는 오래된 북마크로 분류됩니다.</p>
                    </div>
                
                    <div v-else class="cards-grid">
                        <Card
                            v-for="bookmark in bookmarkResults"
                            :key="bookmark.bookmarkId"
                            :bookmarkId="bookmark.bookmarkId"
                            :url="bookmark.url"
                            :img="bookmark.img"
                            :title="bookmark.title"
                            :description="bookmark.description"
                            :tag="bookmark.tags"
                            :priority="bookmark.priority"
                            :isPersonal="bookmark.isPersonal"
                            :createdAt="bookmark.createdAt"
                            :updatedAt="bookmark.updatedAt"
                            :readingTime="bookmark.readingTime"
                        />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import Header from '@/common/Header.vue';
import SideBar from '@/common/SideBar.vue';
import Card from '@/common/Card.vue';
import { useBookmarkStore } from '@/stores/bookmark';
import { storeToRefs } from 'pinia';

const bookmarkStore = useBookmarkStore();
const { oldBookmarks } = storeToRefs(bookmarkStore);

// 오래된 북마크 데이터 가져오기
const bookmarkResults = computed(() => {
    return oldBookmarks.value?.results || [];
});

onMounted(async () => {
    try {
        await bookmarkStore.getOldBookmarks();
    } catch (error) {
        console.error('오래된 북마크 로딩 실패:', error);
    }
});
</script>

<style scoped>
.layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #F5F5F5;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
}

.content-wrapper {
    display: flex;
    margin-top: 60px; /* 헤더 높이만큼 여백 추가 */
    height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 만큼 설정 */
}

.sidebar {
    position: fixed;
    left: 0;
    top: 60px; /* 헤더 높이만큼 떨어뜨림 */
    bottom: 0;
    width: 240px; /* 사이드바 너비 */
    z-index: 99;
}

.main-content {
    flex: 1;
    margin-left: 240px; /* 사이드바 너비만큼 여백 */
    overflow-y: auto; /* 본문 내용만 스크롤 가능하도록 */
    height: 100%;
}

.body {
    padding: 20px;
}

.page-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    background-color: #f8f9fa;
    border-radius: 12px;
    text-align: center;
}

.empty-icon {
    font-size: 3rem;
    color: #ddd;
    margin-bottom: 16px;
}

.empty-text {
    font-size: 1.2rem;
    color: #666;
    margin-bottom: 8px;
}

.empty-description {
    font-size: 0.9rem;
    color: #888;
}

.cards-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 24px;
    padding: 20px 20px;
    
}

.page-header {
    /* background: linear-gradient(to right, #f8f9fa, #ffffff); */
    padding: 16px 24px;
    border-radius: 12px;
    margin-bottom: 12px;
}

.header-content {
    max-width: 800px;
}

.title-section {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.title-icon {
    font-size: 1.5rem;
    color: #3730A3;
}

.title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
}

.description {
    font-size: 0.95rem;
    color: #666;
    margin: 0;
    line-height: 1.4;
}

</style>