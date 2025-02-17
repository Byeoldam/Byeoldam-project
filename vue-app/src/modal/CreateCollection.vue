<template>
    <div class="create-collection">
        <div class="modal-header">
            <h2 class="title">컬렉션 만들기</h2>
            <button class="close-button" @click="$emit('close')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="description">컬렉션 명과 컬렉션 타입을 설정해 주세요.</p>
        
        <div class="input-group">
            <input 
                type="text" 
                v-model="collectionName"
                placeholder="컬렉션 이름을 입력하세요"
                class="collection-input"
            />
            <div class="type-selector">
                <button 
                    :class="['type-button', { active: collectionType === 'personal' }]"
                    @click="collectionType = 'personal'"
                >
                    Personal
                </button>
                <button 
                    :class="['type-button', { active: collectionType === 'share' }]"
                    @click="collectionType = 'share'"
                >
                    Share
                </button>
            </div>
        </div>

        <button 
            class="create-button"
            @click="handleCreate"
            :disabled="!isValid"
        >
            생성하기
        </button>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useCollectionStore } from '@/stores/collection';

const collectionStore = useCollectionStore();
const emit = defineEmits(['close']);

const collectionName = ref('');
const collectionType = ref('personal'); // 기본값은 personal

const isValid = computed(() => {
    return collectionName.value.trim().length > 0;
});

const handleCreate = async () => {
    if (!isValid.value) return;
    
    try {
        if (collectionType.value === 'personal') {
            await collectionStore.createPersonalCollection(collectionName.value);
        } else {
            await collectionStore.createSharedCollection(collectionName.value);
        }

        // 컬렉션 생성 후 목록 새로고침
        await collectionStore.fetchAllCollection();
        
        emit('close'); // 성공 시 모달 닫기
    } catch (error) {
        console.error('컬렉션 생성 중 오류 발생:', error);
        // 여기에 에러 처리 로직 추가 (예: 사용자에게 알림 표시)
    }
};
</script>

<style scoped>
.create-collection {
    max-width: 500px;
    padding: 24px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    position: relative;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.close-button {
    background: none;
    border: none;
    padding: 8px;
    cursor: pointer;
    font-size: 1.2rem;
    color: #666;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.close-button:hover {
    background-color: #f0f0f0;
    color: #333;
}

.title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin: 0;
}

.description {
    color: #666;
    margin: 0 0 24px 0;
    font-size: 0.95rem;
}

.input-group {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
}

.collection-input {
    flex: 1;
    padding: 10px 16px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 0.95rem;
}

.collection-input:focus {
    outline: none;
    border-color: #3730A3;
}

.type-selector {
    display: flex;
    gap: 8px;
}

.type-button {
    padding: 10px 16px;
    border: 1px solid #ddd;
    background: white;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: all 0.2s;
}

.type-button.active {
    background: #6366F1;
    color: white;
    border-color: #6366F1   ;
}

.type-button:hover:not(.active) {
    background: #f5f5f5;
}

.create-button {
    width: 100%;
    padding: 12px;
    background: #6366F1;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 1rem;
    cursor: pointer;
    transition: background 0.2s;
}

.create-button:hover:not(:disabled) {
    background: #4F46E5;
}

.create-button:disabled {
    background: #cccccc;
    cursor: not-allowed;
}
</style>