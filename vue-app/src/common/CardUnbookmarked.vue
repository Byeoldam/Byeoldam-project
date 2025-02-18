<template>
    <div class="card">
        <div class="card-header">
            <div class="priority">
                <!-- 빈 공간 유지 -->
            </div>
            <div class="settings">
                <button class="save-button" @click="openModal">
                    저장
                </button>
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
                        v-if="props.tag && props.tag.length > 0"
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

    <!-- Suspense 컴포넌트로 BookmarkSave 감싸기 -->
    <Suspense>
        <template #default>
            <BookmarkSave 
                v-if="showSaveModal" 
                :url="props.url"
                :title="props.title"
                :description="props.description"
                :img="imageSrc"
                :readingTime="props.readingTime"
                @close="showSaveModal = false"
                @save="showSaveModal = false"
            />
        </template>
        <template #fallback>
            <div>로딩 중...</div>
        </template>
    </Suspense>
</template>

<script setup>
import { ref, computed } from 'vue';
import BookmarkSave from '@/modal/BookmarkSave.vue';

const props = defineProps({
    url: {
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
    img: {
        type: String,
        required: true
    },
    readingTime: {
        type: Number,
        required: false
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
    }
});

// 모달 상태 관리
const showSaveModal = ref(false);

const visibleTags = computed(() => props.tag?.slice(0, 2) || []);
const remainingTagsCount = computed(() => Math.max(0, (props.tag?.length || 0) - 2));

const imageSrc = computed(() => {
    return props.img && props.img.startsWith('http') 
        ? props.img 
        : 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?q=80&w=2128&auto=format&fit=crop';
});

const handleImageClick = () => {
    window.open(props.url, '_blank', 'noopener,noreferrer');
};

const openModal = () => {
    console.log('이전 showSaveModal 값:', showSaveModal.value);
    showSaveModal.value = true;
    console.log('변경된 showSaveModal 값:', showSaveModal.value);
    console.log('북마크 정보:', {
        url: props.url,
        title: props.title,
        description: props.description,
        readingTime: props.readingTime,
        img: imageSrc.value
    });
};
</script>

<style scoped>
.card {
    position: relative;
    border-radius: 18px;
    background: #ffffff;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    overflow: hidden;
    transition: all 0.3s ease;
    width: 240px;
    position: relative;

}

.card:hover {
    transform: translateY(-4px);
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
  transform: scale(1.03);
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
.remaining-count {
  color: #888;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
}

.priority {
    width: 24px; /* star 아이콘 자리 크기만큼 확보 */
    height: 24px;
}

.settings {
    display: flex;
    align-items: center;
}

.reading-time {
    color: white;
    font-size: 0.8rem;
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    border-radius: 12px;
    background-color: rgba(0,0,0,0.5);
}

.read-time {
  white-space: nowrap;
  color: #666;
  font-size: 0.8rem;
  font-weight: 500;
}

.save-button {
    padding: 4px 12px;
    background-color: #6366F1;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 0.9rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.save-button:hover {
    background-color: #3830a3c5;
}
</style>
