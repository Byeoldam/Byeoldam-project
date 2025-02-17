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
                                <i class="fas fa-thumbs-up title-icon"></i>
                                <h2 class="title">추천 태그</h2>
                            </div>
                            <p class="description">자주 사용되는 태그들을 확인해보세요</p>
                        </div>
                    </div>
                    
                    <div class="filter-buttons">
                        <button 
                            v-for="tag in tags" 
                            :key="tag.tagName"
                            :class="['filter-btn', selectedTag === tag.tagName ? 'active' : '']"
                            @click="handleTagClick(tag.tagName)"
                        >
                            {{ tag.tagName }}
                        </button>
                    </div>

                    <!-- 추천 북마크 카드 표시 영역 -->
                    <Suspense>
                        <template #default>
                            <div v-if="isLoading" class="loading-spinner">
                                <div class="spinner"></div>
                                <p>로딩 중...</p>
                            </div>
                            <div v-else-if="recommendedBookmarks.length > 0" class="cards-grid">
                                <CardUnbookmarked
                                    v-for="bookmark in recommendedBookmarks"
                                    :key="bookmark.url"
                                    :url="bookmark.url"
                                    :title="bookmark.title"
                                    :description="bookmark.description"
                                    :img="bookmark.imageUrl"
                                    :readingTime="bookmark.readingTime"
                                />
                            </div>
                            <div v-else class="empty-state">
                                <p class="empty-text">추천 북마크가 없습니다.</p>
                            </div>
                        </template>
                        <template #fallback>
                            <div class="loading-spinner">
                                <div class="spinner"></div>
                                <p>로딩 중...</p>
                            </div>
                        </template>
                    </Suspense>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import Header from '@/common/Header.vue';
import SideBar from '@/common/SideBar.vue';
import CardUnbookmarked from '@/common/CardUnbookmarked.vue';
import { useBookmarkStore } from '@/stores/bookmark';
import api from '@/utils/api';

const router = useRouter();
const bookmarkStore = useBookmarkStore();
const { userDefineTags, recommendedBookmarks } = storeToRefs(bookmarkStore);
const { getUserDefineTags, getRecommendedBookmarks } = bookmarkStore;

const isLoading = ref(true);
const selectedTag = ref(null);
const userTags = ref([]);
const page = ref(1);
const hasMore = ref(true);
const observer = ref(null);

const hasUserTags = computed(() => userTags.value && userTags.value.length > 0);

const fetchUserTags = async () => {
    try {
        await getUserDefineTags();
        // 실제 API 준비될 때까지 예시 데이터 사용
        const tagList = userDefineTags.value.results.tagList;
        console.log('태그 리스트:', tagList);
        

        if (tagList && tagList.length > 0) {
            userTags.value = tagList.map((tagName, index) => ({
                id: index + 1,
                name: tagName
            }));
        }
    } catch (error) {
        console.error('태그 로딩 실패:', error);
        userTags.value = [];
    }
};

const fetchRecommendedBookmarks = async (tagName = null) => {
    try {
        isLoading.value = true;
    } catch (error) {
        console.error('추천 북마크 로딩 실패:', error);
    } finally {
        isLoading.value = false;
    }
};

const setupIntersectionObserver = () => {
    observer.value = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasMore.value && !isLoading.value) {
            page.value++;
            fetchRecommendedBookmarks(selectedTag.value);
        }
    });

    const target = document.querySelector('.bookmarks-grid');
    if (target) observer.value.observe(target);
};

const goToBookmarks = () => {
    router.push('/personal-collection');  // 북마크 관리 페이지로 이동
};

const tags = ref([]);

const handleTagClick = async (tagName) => {
    selectedTag.value = tagName;
    isLoading.value = true;  // 로딩 시작
    try {
        const response = await api.get(`/tags/recommend-search`, {
            params: {
                cursorId: 1,
                size: 10,
                tag: tagName
            }
        });
        console.log('Recommended bookmarks:', response.data, 'hakjun0412');
        recommendedBookmarks.value = response.data.results;
    } catch (error) {
        console.error('추천 북마크 로딩 실패:', error, 'hakjun0412');
        recommendedBookmarks.value = [];
    } finally {
        isLoading.value = false;  // 로딩 완료
    }
};

onMounted(async () => {
    await fetchUserTags();
    setupIntersectionObserver();
    try {
        const response = await api.get('/tags');
        console.log('Tags response:', response.data, 'hakjun0412');
        tags.value = response.data.results.tagList;
        if (tags.value && tags.value.length > 0) {
            selectedTag.value = tags.value[0].tagName;
            await handleTagClick(selectedTag.value);
        }
    } catch (error) {
        console.error('태그 로딩 실패:', error, 'hakjun0412');
        tags.value = [];
    }
});

onUnmounted(() => {
    if (observer.value) observer.value.disconnect();
});
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
    /* background: white; */
}

.content-wrapper {
    display: flex;
    margin-top: 60px;
    height: calc(100vh - 60px);
}

.sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    width: 240px;
    /* background: white; */
    z-index: 99;
}

.main-content {
    flex: 1;
    margin-left: 240px;
    overflow-y: auto;
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

.filter-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin: 20px;
    justify-content: flex-start;
    max-width: 1800px;
}

.filter-btn {
    flex: 0 0 calc(8.33% - 10px);
    padding: 10px 15px;
    border-radius: 20px;
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    color: #495057;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.95rem;
    font-weight: 500;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    min-width: 120px;
}

.filter-btn:hover {
    background-color: #e9ecef;
    transform: scale(1.05);
}

.filter-btn.active {
    background-color: #007bff;
    color: white;
    border-color: #007bff;
    box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
}

@media (max-width: 1600px) {
    .filter-btn {
        flex: 0 0 calc(10% - 10px);
    }
}

@media (max-width: 1200px) {
    .filter-btn {
        flex: 0 0 calc(12.5% - 10px);
    }
}

@media (max-width: 992px) {
    .filter-btn {
        flex: 0 0 calc(16.66% - 10px);
    }
}

@media (max-width: 768px) {
    .filter-btn {
        flex: 0 0 calc(25% - 10px);
    }
}

@media (max-width: 576px) {
    .filter-btn {
        flex: 0 0 calc(33.33% - 10px);
    }
}

.bookmarks-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 24px;
    padding: 20px 0;
}

.loading {
    display: flex;
    justify-content: center;
    padding: 24px;
    font-size: 24px;
    color: #007bff;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
    background: #f8f9fa;
    border-radius: 12px;
}

.empty-icon {
    font-size: 48px;
    color: #ccc;
    margin-bottom: 16px;
}

.empty-text {
    font-size: 1.2rem;
    color: #666;
    margin-bottom: 8px;
}

.empty-description {
    color: #888;
    font-size: 0.9rem;
}

.cards-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    padding: 20px;
}

.loading-spinner {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    padding-top: 200px;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #3730A3;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>