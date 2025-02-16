<template>
<div class="feed-tabs">
    <div 
    v-for="feed in localFeeds" 
    :key="feed.rssId"
    :class="['tab', { active: selectedFeed === feed.rssId }]"
    @click="handleFeedSelect(feed.rssId)"
    >
    {{ feed.name }}
    <span v-if="!feed.read" class="unread-badge"></span>
    </div>
</div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'

const props = defineProps({
    feeds: {
        type: Array,
        required: true,
        default: () => []
    },
    selectedFeed: {
        type: Number,
        required: true
    }
})

const emit = defineEmits(['select-feed'])

// 로컬 상태로 feeds 복사
const localFeeds = ref([])

watchEffect(() => {
    localFeeds.value = props.feeds.map(feed => ({...feed}))
})

const handleFeedSelect = (rssId) => {
    // 선택된 피드의 read 상태를 true로 변경
    const selectedFeed = localFeeds.value.find(feed => feed.rssId === rssId)
    if (selectedFeed) {
        selectedFeed.read = true
    }
    emit('select-feed', rssId)
}
</script>

<style scoped>
.feed-tabs {
display: flex;
gap: 0.5rem;
padding: 0.5rem;
background-color: #f5f5f5;
border-bottom: 1px solid #ddd;
}

.tab {
padding: 0.25rem 0.75rem;
cursor: pointer;
border-radius: 4px;
position: relative;
}

.tab.active {
background-color: #007bff;
color: white;
}

.unread-badge {
position: absolute;
top: -5px;
right: -5px;
width: 8px;
height: 8px;
background-color: #ff4444;
border-radius: 50%;
}
</style>