<template>
    <div class="card">
        <div class="card-header">
            <div class="priority" v-show="props.isPersonal !== false">
                <span v-if="props.priority" class="star-icon">★</span>
                <span v-else class="star-icon empty">☆</span>
            </div>
            <div class="settings">
                <BookmarkSettings 
                    :priority="props.priority"
                    :bookmark-id="props.bookmarkId"
                    :is-personal="props.isPersonal"
                    :tag="props.tag"
                    :collection-id="props.collectionId"
                    @toggle-priority="handlePriorityToggle"
                />
            </div>
        </div>
        <img 
            :src="imageSrc" 
            :alt="props.title" 
            class="card-image"
            @click="handleImageClick"
            style="cursor: pointer"
        >
        <div class="card-content">
            <h2 class="card-title">{{ props.title }}</h2>
            <p class="card-description">{{ props.description }}</p>
            
            <div class="card-url">
                {{ props.url }}
            </div>
            
            <div class="card-footer">
                <div class="tags-container">
                    <span 
                        v-for="tag in visibleTags" 
                        :key="tag.tagName"
                        class="tag"
                        :style="{
                            backgroundColor: tag.tagColor,
                            borderColor: tag.tagBolder
                        }"
                    >
                        #{{ tag.tagName }}
                    </span>
                    <span 
                        v-if="remainingTagsCount > 0" 
                        class="remaining-count"
                    >
                        +{{ remainingTagsCount }}
                    </span>
                </div>
                <div v-if="props.readingTime" class="read-time">
                    {{ props.readingTime }}분
                </div>
            </div>
        </div>
    </div>
    
</template>

<script setup>
import { ref, computed } from 'vue';
import BookmarkSettings from '@/common/BookmarkSettings.vue';
import { useRouter } from 'vue-router';

const props = defineProps({
    key: {
        type: Number,
        required: true
    },
    bookmarkId: {
        type: Number,
        required: true
    },
    img: {
        type: String,
        required: true
    },
    title: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    url: {
        type: String,
        required: true
    },
    tag: {
        type: Array,
        default: () => [],
        validator: (value) => {
            return value.every(tag => 
                typeof tag === 'object' && 
                'tagName' in tag && 
                'tagColor' in tag && 
                'tagBolder' in tag
            )
        }
    },
    priority: {
        type: Boolean,
        default: false
    },
    createdAt: {
        type: String,
        required: true
    },
    updatedAt: {
        type: String,
        required: true
    },
    isPersonal: {
        type: Boolean,
        required: true
    },
    readingTime: {
        type: Number,
        required: false
    }
});

const router = useRouter();

const visibleTags = computed(() => props.tag.slice(0, 2));
const remainingTagsCount = computed(() => Math.max(0, props.tag.length - 2));

const imageSrc = computed(() => {
    return props.img && props.img.startsWith('http') 
        ? props.img 
        : 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?q=80&w=2128&auto=format&fit=crop';
});

const handleImageClick = () => {
    const bookmarkData = {
        id: props.bookmarkId,
        url: props.url,
        title: props.title,
        description: props.description,
        img: props.img,
        tag: props.tag,
        priority: props.priority,
        isPersonal: props.isPersonal,
        createdAt: props.createdAt,
        updatedAt: props.updatedAt
    };
    
    // isPersonal 값에 따라 다른 라우트로 이동
    const route = props.isPersonal 
        ? `/personal-collection/${props.bookmarkId}`
        : `/shared-collection/${props.bookmarkId}`;
    
    // 북마크 데이터를 query parameter로 전달
    router.push({
        path: route,
        query: { data: JSON.stringify(bookmarkData) }
    });
};

const emit = defineEmits(['update:priority']);

const handlePriorityToggle = () => {
    emit('update:priority', !props.priority);
};
</script>

<style scoped>
.card {
    border-radius: 8px;
    background: white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    overflow: hidden;
    transition: transform 0.2s;
    width: 240px;
    position: relative;
}

.card:hover {
    transform: translateY(-4px);
}

.card-image {
    width: 100%;
    height: 140px;
    object-fit: cover;
}

.card-content {
    padding: 12px;
    height: 140px;
    display: flex;
    flex-direction: column;
}

.card-title {
    margin: 0 0 8px 0;
    font-size: 1.1rem;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.card-description {
    color: #666;
    font-size: 0.9rem;
    line-height: 1.4;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-bottom: 6px;
}

.card-url {
    font-size: 0.8rem;
    color: #666;
    margin-bottom: 6px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
}

.tags-container {
    display: flex;
    align-items: center;
    gap: 4px;
    flex-wrap: nowrap;
    overflow: hidden;
    max-width: 70%;
}

.tag {
    padding: 2px 6px;
    border-radius: 12px;
    white-space: nowrap;
    border: 1px solid;
}

.remaining-count {
    color: #888;
    font-size: 0.8rem;
    position: relative;
    cursor: pointer;
}

.read-time {
    white-space: nowrap;
    color: #666;
    font-size: 0.8rem;
}

.tooltip {
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background-color: #333;
    color: white;
    padding: 8px;
    border-radius: 4px;
    font-size: 0.8rem;
    white-space: nowrap;
    z-index: 1000;
    margin-bottom: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.tooltip::after {
    content: '';
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%);
    border-width: 4px;
    border-style: solid;
    border-color: #333 transparent transparent transparent;
}

.tooltip-tag {
    margin-right: 8px;
}

.tooltip-tag:last-child {
    margin-right: 0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
}

.priority {
    display: flex;
    align-items: center;
}

.star-icon {
    color: #ffd900f5;
    font-size: 1.2rem;
    /* cursor: pointer; */
}

.star-icon.empty {
    color: #ccc;
}

.settings {
    position: relative;
    margin-left: auto;
}


</style>