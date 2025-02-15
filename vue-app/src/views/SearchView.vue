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
                                <i class="fas fa-search title-icon"></i>
                                <h2 class="title">북마크 검색</h2>
                            </div>
                            <p class="description">태그를 통해 원하는 북마크를 빠르게 찾아보세요</p>
                        </div>
                    </div>
                    <div class="search-page">
                        <div class="search-container">
                            <input 
                                v-model="searchTag" 
                                @keyup.enter="handleSearch"
                                placeholder="태그를 입력하세요"
                                class="search-input"
                            />
                            <button @click="handleSearch" class="search-button">
                                검색
                            </button>
                        </div>
                        
                        <!-- 검색 전 안내 메시지 -->
                        <!-- <div v-if="!hasSearched" class="initial-message">
                            <div class="message-box">
                                <i class="fas fa-search search-icon"></i>
                                <h3>태그로 북마크를 검색해보세요</h3>
                                <p>원하는 태그를 입력하고 검색하면 관련된 북마크들을 찾을 수 있습니다.</p>
                            </div>
                        </div> -->
                    
                        <!-- 검색 결과 -->
                        <div class="search-results-container">
                            <!-- 왼쪽: 검색 결과 -->
                            <div class="main-results">
                                <h2 v-if="hasSearched" class="section-title">검색결과</h2>
                                <div v-if="bookmarks.length > 0" class="bookmarks-grid">
                                    <Card
                                        v-for="bookmark in bookmarks"
                                        :key="bookmark.bookmarkId"
                                        :bookmarkId="bookmark.bookmarkId"
                                        :url="bookmark.url"
                                        :img="bookmark.img"
                                        :title="bookmark.title"
                                        :description="bookmark.description"
                                        :tag="bookmark.tags"
                                        :priority="bookmark.priority"
                                        :readingTime="bookmark.readingTime"
                                        :createdAt="bookmark.createdAt"
                                        :updatedAt="bookmark.updatedAt"
                                        :is-personal="true"
                                        :collection-id="bookmark.collectionId"
                                    />
                                </div>
                                
                                <div v-if="loading" class="loading">
                                    데이터를 불러오는 중...
                                </div>
                                
                                <div v-if="hasSearched && bookmarks.length === 0 && !loading" class="no-results">
                                    검색 결과가 없습니다.
                                </div>
                            </div>

                            <!-- 오른쪽: 추천 북마크 -->
                            <div v-if="recommendedBookmarks.length > 0" class="recommended-section">
                                <h2 class="section-title">관련 북마크</h2>
                                <div class="recommended-list">
                                    <div 
                                        v-for="bookmark in recommendedBookmarks" 
                                        :key="bookmark.url"
                                        class="recommended-item"
                                    >
                                        <div class="recommended-content-wrapper" @click="goToUrl(bookmark.url)">
                                            <img :src="bookmark.imageUrl" :alt="bookmark.title" class="recommended-image">
                                            <div class="recommended-content">
                                                <h3>{{ bookmark.title }}</h3>
                                            </div>
                                        </div>
                                        <button 
                                            class="save-button"
                                            @click.stop="showSaveModal(bookmark)"
                                        >
                                            save
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 북마크 저장 모달 -->
    <BookmarkSave 
        v-if="selectedBookmark"
        :key="selectedBookmark.url"
        :url="selectedBookmark.url"
        :title="selectedBookmark.title"
        :description="selectedBookmark.description"
        :img="selectedBookmark.image"
        @close="selectedBookmark = null"
        @save="handleSave"
    />
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useBookmarkStore } from '@/stores/bookmark'
import { storeToRefs } from 'pinia'
import Header from '@/common/Header.vue'
import SideBar from '@/common/SideBar.vue'
import Card from '@/common/Card.vue'
import BookmarkSave from '@/modal/BookmarkSave.vue'

const bookmarkStore = useBookmarkStore()
const { searchBookmarksByTag } = storeToRefs(bookmarkStore)
const searchTag = ref('')
const loading = ref(false)
const hasSearched = ref(false)
const lastCursorId = ref(null)
const hasMore = ref(true)
const selectedBookmark = ref(null)

const bookmarks = computed(() => {
    return searchBookmarksByTag.value?.results?.personalBookmarkList || []
})

const recommendedBookmarks = computed(() => {
    return searchBookmarksByTag.value?.results?.recommendedUrlList || []
})


const handleSearch = async () => {
    console.log("검색 시작");
    console.log("검색어:" + searchTag.value);
    
    if (!searchTag.value.trim()) return
    
    try {
        loading.value = true
        hasSearched.value = true
        lastCursorId.value = null // 검색 시 커서 초기화
        hasMore.value = true // 검색 시 hasMore 초기화
        
        await bookmarkStore.getSearchBookmarksByTag(searchTag.value,0,10)
        
        // 마지막 북마크의 ID를 커서로 설정
        if (bookmarks.value.length > 0) {
            lastCursorId.value = bookmarks.value[bookmarks.value.length - 1].bookmarkId
        }
        
        // 받아온 데이터가 페이지 사이즈보다 작으면 더 이상 데이터가 없음
        if (bookmarks.value.length < 6) {
            hasMore.value = false
        }
        
    } catch (error) {
        console.error('검색 중 오류 발생:', error)
    } finally {
        loading.value = false
    }
}


const goToUrl = (url) => {
    window.open(url, '_blank')
}

const loadMoreBookmarks = async () => {
    if (!hasMore.value || loading.value || !lastCursorId.value) return
    
    try {
        loading.value = true
        await bookmarkStore.getSearchBookmarksByTag(
            searchTag.value,
            lastCursorId.value
        )
        
        // 새로운 북마크들만 기존 목록에 추가
        if (bookmarks.value.length > 0) {
            lastCursorId.value = bookmarks.value[bookmarks.value.length - 1].bookmarkId
        }
        
        // 받아온 데이터가 페이지 사이즈보다 작으면 더 이상 데이터가 없음
        if (bookmarks.value.length < 6) {
            hasMore.value = false
        }
    } catch (error) {
        console.error('추가 북마크 로딩 중 오류 발생:', error)
    } finally {
        loading.value = false
    }
}

const handleScroll = () => {
    const mainContent = document.querySelector('.main-content')
    if (!mainContent) return
    
    const { scrollTop, scrollHeight, clientHeight } = mainContent
    if (scrollTop + clientHeight >= scrollHeight - 300 && hasMore.value) {
        loadMoreBookmarks()
    }
}

const showSaveModal = (bookmark) => {
    selectedBookmark.value = bookmark
}

const handleSave = () => {
    // 북마크 저장 로직 구현
    console.log('Bookmark saved:', selectedBookmark.value)
    selectedBookmark.value = null
}

onMounted(() => {
    const mainContent = document.querySelector('.main-content')
    if (mainContent) {
        mainContent.addEventListener('scroll', handleScroll)
    }
})

onUnmounted(() => {
    const mainContent = document.querySelector('.main-content')
    if (mainContent) {
        mainContent.removeEventListener('scroll', handleScroll)
    }
})
</script>

<style scoped>
.layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: white;
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
    background: white;
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
.search-page {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

.search-container {
    margin-bottom: 30px;
    display: flex;
    gap: 10px;
}

.search-input {
    flex: 1;
    padding: 12px;
    border: 2px solid #ddd;
    border-radius: 8px;
    font-size: 16px;
}

.search-button {
    padding: 0 24px;
    background-color: #3182ce;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: bold;
}

.search-results-container {
    display: flex;
    gap: 40px;
}

.main-results {
    flex: 1;
}

.recommended-section {
    width: 300px;
    flex-shrink: 0;
}

.section-title {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 20px;
    color: #2d3748;
}

.bookmarks-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
}

.recommended-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.recommended-item {
    display: flex;
    gap: 12px;
    padding: 12px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    cursor: pointer;
    transition: transform 0.2s;
    height: 100px;
    width: 100%;
}

.recommended-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.recommended-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
}

.recommended-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.recommended-content h3 {
    font-size: 0.9rem;
    margin: 0;
    color: #2d3748;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.save-button {
    padding: 4px 12px;
    background-color: transparent;
    border: 1px solid #e2e8f0;
    border-radius: 4px;
    color: #718096;
    cursor: pointer;
    font-size: 0.8rem;
}

.save-button:hover {
    background-color: #f7fafc;
}

.loading {
    text-align: center;
    padding: 20px;
    color: #718096;
}

.no-more {
    text-align: center;
    padding: 20px;
    color: #718096;
    font-style: italic;
}

/* .initial-message {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
} */
.initial-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    background-color: #f8f9fa;
    border-radius: 12px;
    text-align: center;
}

.message-box {
    text-align: center;
    padding: 20px;
    background-color: #f8fafc;
    border-radius: 12px;
    max-width: 500px;
}

.search-icon {
    font-size: 48px;
    margin-bottom: 20px;
    color: #ddd;
    display: block;
}

.message-box h3 {
    color: #2d3748;
    font-size: 24px;
    margin-bottom: 12px;
}

.message-box p {
    color: #718096;
    font-size: 16px;
    line-height: 1.5;
}

.no-results {
    text-align: center;
    padding: 40px;
    color: #718096;
    font-style: italic;
}

.page-header {
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    padding: 16px 24px;
    border-radius: 12px;
    margin-bottom: 24px;
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
    color: #007bff;
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



