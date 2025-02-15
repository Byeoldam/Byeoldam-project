<template>
<div class="feed-tabs">
    <div 
    v-for="feed in feeds" 
    :key="feed.rssId"
    :class="['tab', { active: selectedFeed === feed.rssId }]"
    @click="$emit('select-feed', feed.rssId)"
    >
    {{ feed.name }}
    <span v-if="!feed.read" class="unread-badge"></span>
    </div>
</div>
</template>

<script setup>
defineProps({
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

defineEmits(['select-feed'])
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