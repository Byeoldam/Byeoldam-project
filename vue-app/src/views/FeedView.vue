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
                                <i class="fas fa-rss title-icon"></i>
                                <h2 class="title">RSS 피드</h2>
                            </div>
                            <p class="description">구독 중인 RSS 피드의 최신 글들을 한눈에 확인하세요</p>
                        </div>
                    </div>
                    <div class="rss-container">
                        <FeedTabs 
                            :feeds="rssList.results"
                            :selected-feed="selectedFeed"
                            @select-feed="selectFeed"
                        />
                        <div class="content-container">
                            <template v-if="!rssStore.isLoading">
                                <FeedPostList 
                                    :posts="rssArticles.results?.latestPosts?.content"
                                    @select-post="selectPost"
                                />
                                <FeedPostContent :url="selectedPostUrl" />
                            </template>
                            <div v-if="rssStore.isLoading" class="loading-spinner">
                                <div class="spinner"></div>
                                <p>로딩 중...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import Header from '@/common/Header.vue'
import SideBar from '@/common/SideBar.vue'
import FeedTabs from '@/component/FeedTabs.vue'
import FeedPostList from '@/component/FeedPostList.vue'
import FeedPostContent from '@/component/FeedPostContent.vue'
import { ref, onMounted } from 'vue'
import { useRssStore } from '@/stores/rss'
import { storeToRefs } from 'pinia'

const rssStore = useRssStore()
const { rssList, rssArticles } = storeToRefs(rssStore)
const selectedFeed = ref(1)
const selectedPostUrl = ref(null)

// RSS 피드 아이템 로드
const loadRssItems = async (rssId) => {
  try {
    await rssStore.getRssArticles(rssId, 0, 10)
  } catch (error) {
    console.error('RSS 아이템 로드 실패:', error)
  }
}

// 피드 선택 시 해당 피드의 아이템 로드
const selectFeed = async (rssId) => {
  selectedFeed.value = rssId
  await loadRssItems(rssId)
}

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(async () => {
  try {
    await rssStore.getRss()
    await loadRssItems(selectedFeed.value)
  } catch (error) {
    console.error('RSS 데이터 로드 실패:', error)
  }
})

const selectPost = (url) => {
  selectedPostUrl.value = url
}
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

.rss-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-container {
  display: flex;
  flex: 1;
  gap: 1rem;
  padding: 1rem;
  position: relative;
  min-height: 500px;
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

.loading-spinner {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 10;
    background-color: rgba(255, 255, 255, 0.8);
    padding: 2rem;
    border-radius: 8px;
    width: fit-content;
    margin: auto;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>