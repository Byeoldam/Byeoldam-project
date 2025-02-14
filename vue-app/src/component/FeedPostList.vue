<template>
<div class="posts-list">
    <template v-if="posts?.length">
    <div 
        v-for="post in posts" 
        :key="post.url"
        :class="['post-item', { 'read': post.read }]"
        @click="$emit('select-post', post.url)"
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
defineProps({
    posts: {
        type: Array,
        default: () => [],
        required: false
    }
})

defineEmits(['select-post'])
</script>

<style scoped>
.posts-list {
width: 300px;
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
opacity: 0.7;
}

.unread-dot {
position: absolute;
right: 1rem;
top: 50%;
transform: translateY(-50%);
width: 8px;
height: 8px;
background-color: #007bff;
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