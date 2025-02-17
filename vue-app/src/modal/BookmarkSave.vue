<template>
    <div class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <h2>북마크 저장하기</h2>
                <button class="close-button" @click="closeModal">×</button>
            </div>
            
            <div class="modal-body">
                <div class="collection-select">
                    <p>저장할 개인 컬렉션을 선택하세요.</p>
                    <select v-model="selectedCollection" class="select-input">
                        <option value="" disabled>컬렉션을 선택하세요</option>
                        <option 
                            v-for="collection in collections" 
                            :key="collection.collection_id" 
                            :value="collection.collection_id"
                        >
                            {{ collection.collection_name }}
                        </option>
                    </select>
                </div>

                <div class="tags-section">
                    <p>태그</p>
                    <div class="selected-tags">
                        <span 
                            v-for="tag in selectedTags" 
                            :key="tag"
                            class="tag"
                        >
                            # {{ tag }}
                            <button class="remove-tag" @click="removeTag(tag)">×</button>
                        </span>
                    </div>
                    <div class="tag-input-container">
                        <input 
                            v-model="newTag"
                            type="text"
                            class="tag-input"
                            placeholder="태그를 입력하세요"
                            @keyup.enter="addTag"
                        >
                        <button class="add-button" @click="addTag">+</button>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button class="save-button" @click="handleSave">저장</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useCollectionStore } from '@/stores/collection';
import { useBookmarkStore } from '@/stores/bookmark';
import { ElMessage } from 'element-plus';

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
        required: true
    }
});

const emit = defineEmits(['close', 'save']);
const collectionStore = useCollectionStore();
const bookmarkStore = useBookmarkStore();

// 컴포넌트가 마운트될 때 컬렉션 데이터를 가져옵니다
await collectionStore.fetchAllCollections();

const selectedCollection = ref('');
const collections = computed(() => {
    console.log('collections raw:', collectionStore.allCollections);
    return collectionStore.allCollections.map(collection => ({
        collection_id: collection.collection_id || collection.collectionId,
        collection_name: collection.collection_name || collection.name,
        isPersonal: collection.isPersonal
    }));
});

const selectedTags = ref([]);
const newTag = ref('');

const addTag = () => {
    if (newTag.value.trim() && !selectedTags.value.includes(newTag.value.trim())) {
        selectedTags.value.push(newTag.value.trim());
        newTag.value = '';
    }
};

const removeTag = (tag) => {
    selectedTags.value = selectedTags.value.filter(t => t !== tag);
};

const closeModal = () => {
    emit('close');
};

const handleSave = async () => {
    try {
        if (!selectedCollection.value) {
            alert('컬렉션을 선택해주세요.');
            return;
        }

        // 태그 색상 배열 추가
        const tagColors = [
            '#e3f2fd', // 파란색 계열
            '#fff3e0', // 주황색 계열
            '#fce4ec', // 분홍색 계열
            '#f3e5f5', // 보라색 계열
            '#e8f5e9', // 초록색 계열
            '#fff8e1'  // 노란색 계열
        ];

        // 선택된 컬렉션 찾기
        const selectedCollectionInfo = collections.value.find(
            c => c.collection_id === selectedCollection.value
        );

        // API 요청 데이터 구성
        const bookmarkData = {
            url: props.url,
            collectionId: Number(selectedCollection.value),
            tags: selectedTags.value.map(tag => ({
                tagName: tag,
                tagColor: tagColors[Math.floor(Math.random() * tagColors.length)], // 랜덤 색상 지정
                tagBolder: "normal"
            })),
            readingTime: props.readingTime,
            isPersonal: selectedCollectionInfo.isPersonal
        };

        console.log('서버로 보내는 데이터:', bookmarkData);
        
        const response = await bookmarkStore.saveBookmark(bookmarkData);
        console.log('서버 응답:', response);
        ElMessage.success('북마크가 저장장되었습니다')
        
        emit('save');
        closeModal();
    } catch (error) {
        console.error('북마크 저장 중 오류 발생:', error);
        if (error.response) {
            console.error('서버 응답 데이터:', error.response.data);
            console.error('요청 데이터:', error.config.data);
        }
        ElMessage.error(error.response.data.message);
    }
};

// 디버깅을 위한 watch
watch(() => collections.value, (newCollections) => {
    console.log('컬렉션 데이터 변경:', newCollections);
});

watch(selectedCollection, (newVal) => {
    console.log('선택된 컬렉션 값:', newVal, typeof newVal);
});
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.modal-content {
    background: white;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    padding: 20px;
    position: relative;
    z-index: 10000;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.modal-header h2 {
    margin: 0;
    font-size: 1.5rem;
}

.close-button {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0;
}

.select-input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    margin-top: 8px;
    color: #000;
    background-color: #fff;
}

.select-input option {
    color: #000;
    background-color: #fff;
    padding: 8px;
}

.tags-section {
    margin-top: 20px;
}

.selected-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin: 10px 0;
}

.tag {
    display: flex;
    align-items: center;
    padding: 4px 8px;
    background: #e9ecef;
    border-radius: 16px;
    font-size: 0.9rem;
}

.tag.redis {
    background: #e3f2fd;
}

.tag.smtp {
    background: #fff3e0;
}

.tag.spring {
    background: #fce4ec;
}

.remove-tag {
    background: none;
    border: none;
    margin-left: 4px;
    cursor: pointer;
    padding: 0 4px;
}

.tag-input-container {
    display: flex;
    gap: 8px;
}

.tag-input {
    flex: 1;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.add-button {
    background: #4285f4;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 0 16px;
    cursor: pointer;
}

.modal-footer {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

.save-button {
    background: #4285f4;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 8px 24px;
    cursor: pointer;
}

.save-button:hover {
    background: #3367d6;
}
</style>
