<template>
    <div class="card">
        <div class="card-header">
            <div class="priority" v-show="props.isPersonal !== false">
                <span 
                    v-if="props.priority" 
                    class="star-icon" 
                    @click="newHandlePriorityToggle"
                >★</span>
                <span 
                    v-else 
                    class="star-icon empty"
                    @click="newHandlePriorityToggle"
                >☆</span>
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
            @click="handleCardClick"
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
                  <i class="far fa-clock"></i>
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
import { useBookmarkStore } from '@/stores/bookmark';
import { ElMessage } from 'element-plus';

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
const bookmarkStore = useBookmarkStore();

const visibleTags = computed(() => props.tag.slice(0, 2));
const remainingTagsCount = computed(() => Math.max(0, props.tag.length - 2));

const imageSrc = computed(() => {
    return props.img && props.img.startsWith('http') 
        ? props.img 
        : 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?q=80&w=2128&auto=format&fit=crop';
});

const handleCardClick = () => {
  // URL은 깔끔하게 bookmarkId만 전달
  router.push({
    name: props.isPersonal ? 'personal-bookmark-detail' : 'shared-bookmark-detail',
    params: { bookmarkId: props.bookmarkId }
  });
};

const emit = defineEmits(['update:priority']);

const handlePriorityToggle = () => {
    emit('update:priority', !props.priority);
};

const newHandlePriorityToggle = async () => {
    try {
        const success = await bookmarkStore.changePriority(
            props.bookmarkId,
            !props.priority
        );
        
        if (success) {
            ElMessage.success('중요도 변경에 성공했습니다.');
            emit('update:priority', !props.priority);
        } else {
            ElMessage.error('중요도 변경에 실패했습니다.');
        }
    } catch (error) {
        console.error('중요도 변경 중 오류 발생:', error);
        ElMessage.error('중요도 변경 중 오류가 발생했습니다.');
    }
};
</script>

<style scoped>
.card {
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  width: 240px;
  position: relative;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.15);
}

.card-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.card:hover .card-image {
  transform: scale(1.05);
}

.card-content {
  padding: 13px;
  height: 160px;
  display: flex;
  flex-direction: column;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-description {
  color: #666;
  font-size: 0.8rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
}

.card-url {
  font-size: 0.7rem;
  color: #999;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding: 0 px;
}

.tags-container {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
  overflow: hidden;
  max-width: 70%;
}

.tag {
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
  border: none;
  color: rgb(95, 93, 93);;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}

.tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 6px rgba(0,0,0,0.15);
}

.remaining-count {
  color: #888;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
}

.read-time {
  white-space: nowrap;
  color: #666;
  font-size: 0.8rem;
  font-weight: 500;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background-color: #ffffff;
}

.priority {
  display: flex;
  align-items: center;
}

.star-icon {
  color: #FFD900F5;
  font-size: 1.2rem;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
  cursor: pointer;
}

.star-icon.empty {
  color: #e7e7e7;
}

.star-icon:hover {
  transform: scale(1.1);
}

.settings {
  position: relative;
  margin-left: auto;
}


</style>