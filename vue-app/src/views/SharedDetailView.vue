<template>
    <div class="layout">
        <Header class="header"/>
        <div class="content-wrapper">
            <SideBar class="sidebar"/>
            <div class="main-content">
                <div v-if="error" class="error-message">
                    {{ error }}
                </div>
                
                <div v-if="isInitializing" class="loading">
                    데이터를 불러오는 중...
                </div>
                
                <div v-else class="detail-view">
                    <!-- URL 프리뷰 섹션 -->
                    <section class="preview-section">
                        <h2>URL 프리뷰</h2>
                        <div v-if="bookmark" class="content">
                            <div class="action-buttons">
                                <a :href="bookmark.url" target="_blank" class="original-link-btn">
                                    <i class="fas fa-external-link-alt"></i>
                                    원본 페이지로 이동
                                </a>
                            </div>
                            <iframe
                                v-if="!iframeError"
                                :src="bookmark.url"
                                class="website-preview"
                                frameborder="0"
                                sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
                                loading="lazy"
                                @error="handleIframeError"
                            ></iframe>
                            <div v-if="iframeError" class="iframe-fallback">
                                <p>미리보기를 불러올 수 없습니다.</p>
                            </div>
                        </div>
                    </section>
            

                    <!-- 메모장 섹션 -->
                    <section class="memo-section">
                        <h2>메모장</h2>
                        <div class="memo-input">
                            <textarea 
                                v-model="newMemo" 
                                placeholder="메모를 입력하세요..."
                                rows="4"
                                :disabled="isSubmitting"
                            ></textarea>
                            <button 
                                @click="addMemo" 
                                class="add-memo-btn"
                                :disabled="isSubmitting || !newMemo.trim()"
                            >
                                {{ isSubmitting ? '추가 중...' : '메모 추가' }}
                            </button>
                        </div>
                        
                        <div v-if="isLoading" class="loading">
                            메모를 불러오는 중...
                        </div>
                        
                        <div v-else class="memo-list">
                            <div v-for="memo in memos" :key="memo.id" class="memo-item">
                                <div class="memo-user">
                                    <img v-if="memo.imageUrl" :src="memo.imageUrl" class="user-avatar" alt="사용자 프로필">
                                    <span>{{ memo.userName }}</span>
                                </div>
                                <div class="memo-content">{{ memo.content }}</div>
                                <div class="memo-date">{{ memo.date }}</div>
                                <button 
                                    @click="deleteMemo(memo.id)" 
                                    class="delete-memo-btn"
                                    :disabled="isSubmitting"
                                >
                                    {{ isSubmitting ? '삭제 중...' : '삭제' }}
                                </button>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookmarkStore } from '@/stores/bookmark';
import Header from '@/common/Header.vue';
import SideBar from '@/common/SideBar.vue';

const route = useRoute();
const router = useRouter();
const bookmarkStore = useBookmarkStore();

// 상태 관리
const bookmark = ref(null);
const bookmarkId = computed(() => Number(route.params.id));
const newMemo = ref('');
const memos = ref([]);
const error = ref(null);
const isLoading = ref(false);
const isSubmitting = ref(false);
const isInitializing = ref(true);
const iframeError = ref(false);
const isComponentMounted = ref(true);

// 메모 목록 조회
const fetchMemos = async () => {
    if (!bookmark.value?.id) {
        console.log('No bookmark ID available for fetching memos');
        return;
    }
    
    try {
        isLoading.value = true;
        error.value = null;
        
        console.log('Fetching memos for bookmark ID:', bookmark.value.id);
        const response = await bookmarkStore.getMemo(bookmark.value.id);
        console.log('Memos response:', response);

        if (response?.data?.status) {
            memos.value = response.data.results.map(memo => ({
                id: memo.memoId,
                content: memo.content,
                date: new Date(memo.createdAt).toLocaleString(),
                userName: memo.nickname,
                imageUrl: memo.imageUrl
            }));
            console.log('Processed memos:', memos.value);
        } else {
            console.log('No memos found or invalid response structure');
            memos.value = [];
        }
    } catch (err) {
        error.value = '메모 로딩 실패: ' + err.message;
        console.error('메모 로딩 실패:', err);
    } finally {
        if (isComponentMounted.value) {
            isLoading.value = false;
        }
    }
};

// 메모 추가
const addMemo = async () => {
    if (isSubmitting.value || !newMemo.value.trim()) return;
    
    try {
        isSubmitting.value = true;
        error.value = null;
        
        const response = await bookmarkStore.createMemo(bookmark.value.id, newMemo.value);
        if (response.data.status) {
            const newMemoData = response.data.results;
            memos.value.push({
                id: newMemoData.memoId,
                content: newMemoData.content,
                date: new Date(newMemoData.createdAt).toLocaleString(),
                userName: newMemoData.nickname,
                imageUrl: newMemoData.imageUrl
            });
            newMemo.value = '';
        }
    } catch (err) {
        error.value = '메모 추가 실패: ' + err.message;
        console.error('메모 추가 실패:', err);
    } finally {
        if (isComponentMounted.value) {
            isSubmitting.value = false;
        }
    }
};

// 메모 삭제
const deleteMemo = async (memoId) => {
    if (isSubmitting.value) return;
    
    try {
        isSubmitting.value = true;
        error.value = null;
        
        const response = await bookmarkStore.deleteMemo(bookmark.value.id, memoId);
        if (response.data.status) {
            memos.value = memos.value.filter(memo => memo.id !== memoId);
        }
    } catch (err) {
        error.value = '메모 삭제 실패: ' + err.message;
        console.error('메모 삭제 실패:', err);
    } finally {
        if (isComponentMounted.value) {
            isSubmitting.value = false;
        }
    }
};

// iframe 에러 처리
const handleIframeError = () => {
    iframeError.value = true;
};

onMounted(async () => {
    try {
        isInitializing.value = true;
        error.value = null;
        
        console.log('Route query:', route.query);
        console.log('Route params:', route.params);

        // URL query에서 북마크 데이터 가져오기
        if (route.query.data) {
            try {
                bookmark.value = JSON.parse(route.query.data);
                console.log('Bookmark data from query:', bookmark.value);
                await fetchMemos(); // 메모만 가져오기
            } catch (parseError) {
                console.error('Failed to parse bookmark data:', parseError);
                error.value = '북마크 데이터 파싱 실패';
            }
        } else {
            console.log('No bookmark data in query');
            error.value = '북마크 데이터를 찾을 수 없습니다.';
        }
    } catch (err) {
        error.value = '데이터 로딩 실패: ' + err.message;
        console.error('메모 로딩 실패:', err);
    } finally {
        if (isComponentMounted.value) {
            isInitializing.value = false;
        }
    }
});

onUnmounted(() => {
    isComponentMounted.value = false;
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
    margin-top: 60px;
    height: calc(100vh - 60px);
    min-height: 0;
}

.sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    width: 240px;
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

.detail-view {
    flex: 1;
    padding: 20px;
    display: grid;
    grid-template-columns: 7fr 3fr;
    gap: 20px;
    height: calc(100vh - 80px);
    overflow: hidden;
}

.preview-section {
    background: #F5F5F5;
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.memo-section {
    background: #F5F5F5;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

h2 {
    font-size: 1.4rem;
    font-weight: 600;
    color: #2D3748;
    padding-bottom: 12px;
    margin-bottom: 16px;
    border-bottom: 2px solid #E2E8F0;
    position: relative;
    display: flex;
    align-items: center;
}


/* 각 섹션별 아이콘 추가를 위한 스타일 */
.preview-section h2::before {
    content: '🔗';
    margin-right: 8px;
    font-size: 1.2rem;
}

.memo-section h2::before {
    content: '📝';
    margin-right: 8px;
    font-size: 1.2rem;
}

.main-image {
    width: 100%;
    height: 300px;
    object-fit: cover;
    border-radius: 8px;
    margin: 10px 0;
}

.description {
    font-size: 1.1rem;
    line-height: 1.6;
    margin: 10px 0;
}

.tag {
    background: #f0f0f0;
    padding: 4px 8px;
    border-radius: 20px;
    margin-right: 8px;
    font-size: 0.9rem;
}

.visit-link {
    display: inline-block;
    padding: 8px 16px;
    background: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 6px;
    margin-top: 10px;
}

/* 메모장 스타일 */
.memo-input {
    margin-bottom: 20px;
}

.memo-section {
    max-width: none;
}

textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 8px;
    resize: vertical;
    margin-bottom: 8px;
    font-size: 0.9rem;
}

.add-memo-btn {
    padding: 8px 16px;
    background: #28a745;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.memo-item {
    background: #f8f9fa;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 8px;
    position: relative;
    font-size: 0.9rem;
    display: flex;
    flex-direction: column;
}

.memo-content {
    margin-bottom: 8px;
    padding-right: 70px; /* 삭제 버튼 공간 확보 */
    word-break: break-all; /* 긴 텍스트 줄바꿈 */
    white-space: pre-wrap; /* 줄바꿈 유지 */
}

.memo-user {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 0;
    border-bottom: 1px solid #edf2f7;
    margin-bottom: 8px;
}

.user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.memo-user span {
    font-weight: 500;
    color: #2d3748;
    font-size: 0.95rem;
}

/* 이미지가 없을 때의 대체 스타일 */
.memo-user:not(:has(img)) span {
    margin-left: 8px;
}

.memo-date {
    font-size: 0.8rem;
    color: #666;
    margin-top: auto;
}

.delete-memo-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    padding: 4px 8px;
    background: #ce3e3ebc;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    min-width: 60px; /* 버튼 최소 너비 설정 */
}

.preview-section .content {
    display: flex;
    flex-direction: column;
    height: 100%; /* 컨테이너 전체 높이 사용 */
}

.action-buttons {
    margin-bottom: 10px; /* 여백 축소 */
    text-align: right;
    flex-shrink: 0; /* 버튼 영역 크기 고정 */
}

.website-preview {
    width: 100%;
    flex: 1; /* 남은 공간 모두 사용 */
    border: 1px solid #ddd;
    border-radius: 8px;
    margin: 0; /* 마진 제거 */
}

.iframe-fallback {
    flex: 1; /* 에러 시에도 공간 채우기 */
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    background: #f8f9fa;
    border-radius: 8px;
    margin: 0;
}

.original-link-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background-color: #3730A3;
    color: white;
    border: none;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 500;
    transition: background-color 0.2s;
}

.original-link-btn:hover {
    background-color: #0056b3;
}

.original-link-btn i {
    font-size: 0.9em;
}

.error-message {
    color: #dc3545;
    padding: 10px;
    margin: 10px 0;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
}

.loading {
    text-align: center;
    padding: 20px;
    color: #666;
}

button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

.memo-list {
    flex: 1;
    overflow-y: auto;
    margin-top: 10px;
}
</style>