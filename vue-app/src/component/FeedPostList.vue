<template>
<div class="posts-list">
    <template v-if="localPosts?.length">
    <div 
        v-for="post in localPosts" 
        :key="post.url"
        :class="['post-item', { 'read': post.read }]"
        @click="handlePostSelect(post)"
    >
        <h3>{{ post.title }}</h3>
        <div v-if="!post.read" class="unread-dot"></div>
    </div>
    </template>
    <div v-else class="no-posts">
    게시글이 없습니다
    </div>
</div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'

const props = defineProps({
    posts: {
        type: Array,
        default: () => [],
        required: false
    }
})

const emit = defineEmits(['select-post'])

// 로컬 상태로 posts 복사
const localPosts = ref([])

watchEffect(() => {
    localPosts.value = props.posts.map(post => ({...post}))
})

const handlePostSelect = (post) => {
    // 선택된 포스트의 read 상태를 true로 변경
    post.read = true
    emit('select-post', post.url)
}
</script>

<style scoped>
.posts-list {
width: 280px;
overflow-y: auto;
border-right: 1px solid #ddd;
}

.post-item {
padding: 1rem;
cursor: pointer;
border-bottom: 1px solid #eee;
position: relative;
}

.post-item:hover {
background-color: #f8f9fa;
}

.post-item.read {
opacity: 0.5;
}

.post-item h3 {
  margin: 0;
  margin-right: 20px;
  display: -webkit-box;              /* 웹킷 기반 브라우저 지원 */
  -webkit-line-clamp: 2;            /* 최대 2줄까지 표시 */
  -webkit-box-orient: vertical;      /* 수직 방향으로 텍스트 배치 */
  overflow: hidden;                  /* 넘치는 텍스트 숨김 */
  text-overflow: ellipsis;          /* 말줄임표 표시 */
  line-height: 1.3;                 /* 줄 간격 설정 */
  max-height: 2.6em;                /* 최대 높이 설정 (line-height * 2) */
}

.unread-dot {
position: absolute;
right: 1rem;
top: 50%;
transform: translateY(-50%);
width: 8px;
height: 8px;
background-color: #ce3e3ebc;
border-radius: 50%;
}

.no-posts {
display: flex;
align-items: center;
justify-content: center;
height: 100%;
color: #666;
font-size: 1.1rem;
}
</style>