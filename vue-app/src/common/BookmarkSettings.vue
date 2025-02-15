<template>
  <el-popover
    placement="bottom-end"
    :width="200"
    trigger="click"
    popper-class="bookmark-settings-popover"
    v-model:visible="isVisible"
  >
    <template #reference>
      <button class="settings-button">⋮</button>
    </template>
    
    <div class="popover-content">
      <el-button-group vertical class="settings-menu">
        <el-button link @click="togglePriority">
          {{ props.priority ? '중요 북마크 해제' : '중요 북마크로 설정' }}
        </el-button>
        
        <el-button link @click="copyToSharedCollection">
          다른 공유 컬렉션으로 복사
        </el-button>
        
        <el-button link @click="showMoveDialog">
          다른 개인 컬렉션으로 이동
        </el-button>
        
        <el-button link @click="showTagManagement">
          태그 관리
        </el-button>

        <el-button @click="openDeleteModal" class="delete-button">
          삭제
        </el-button>
        
      </el-button-group>
    </div>
  </el-popover>

  <el-dialog
    v-model="showCopyModal"
    :modal="true"
    :show-close="true"
    destroy-on-close
    append-to-body
    class="bookmark-copy-dialog"
  >
    <BookmarkCopyShared 
      :bookmark-id="props.bookmarkId"
      :is-personal="props.isPersonal"
      @close="showCopyModal = false" 
    />
  </el-dialog>

  <el-dialog
    v-model="showMoveModal"
    :modal="true"
    :show-close="true"
    destroy-on-close
    append-to-body
    class="bookmark-move-dialog"
  >
    <BookmarkMovePersonal 
      :bookmark-id="props.bookmarkId"
      :is-personal="props.isPersonal"
      @close="showMoveModal = false"
      @move-complete="handleMoveComplete"
    />
  </el-dialog>

  <el-dialog
    v-model="showTagModal"
    :modal="true"
    :show-close="true"
    destroy-on-close
    append-to-body
    class="bookmark-tag-dialog"
  >
    <BookmarkTagSetting 
      v-if="isTagSettingOpen"
      :bookmark-id="props.bookmarkId"
      :initial-tags="props.tag"
      @close="isTagSettingOpen = false"
    />
  </el-dialog>

  <el-dialog
      v-model="showDeleteModal"
      :modal="true"
      :show-close="true"
      destroy-on-close
      append-to-body
      width="400px"
      class="bookmark-delete-modal"
  >
      <BookmarkDel
          :bookmark-id="props.bookmarkId"
          @close="showDeleteModal = false"
          @confirm="handleDeleteConfirm"
      />
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useBookmarkStore } from '@/stores/bookmark'
import BookmarkCopyShared from '@/modal/BookmarkCopyShared.vue'
import BookmarkMovePersonal from '@/modal/BookmarkMovePersonal.vue'
import BookmarkTagSetting from '@/modal/BookmarkTagSetting.vue'
import BookmarkDel from '@/modal/BookmarkDel.vue'

const bookmarkStore = useBookmarkStore()
const emit = defineEmits(['update:priority'])
const props = defineProps({
  priority: {
    type: Boolean,
    default: false
  },
  bookmarkId: {
    type: Number,
    required: true
  },
  tag: {
    type: Array,
    required: true
  },
  isPersonal: {
    type: Boolean,
    required: true
  },
  collectionId: {
    type: Number,
    required: true
  }
})

const isVisible = ref(false)
const showCopyModal = ref(false)
const showMoveModal = ref(false)
const showTagModal = ref(false)
const showDeleteModal = ref(false)
const isTagSettingOpen = ref(false)

const togglePriority = async () => {
  try {
    const success = await bookmarkStore.changePriority(props.bookmarkId, !props.priority)
    if (success) {
      ElMessage.success(props.priority ? '중요 북마크가 해제되었습니다.' : '중요 북마크로 설정되었습니다.')
      emit('update:priority', !props.priority)
      await bookmarkStore.refreshCurrentPage(props.collectionId)
      isVisible.value = false
    } else {
      ElMessage.error('중요도 변경에 실패했습니다.')
    }
  } catch (error) {
    console.error('중요도 변경 중 오류 발생:', error)
    ElMessage.error('중요도 변경 중 오류가 발생했습니다.')
  }
}

const copyToSharedCollection = () => {
  isVisible.value = false
  showCopyModal.value = true
}

const showMoveDialog = () => {
  isVisible.value = false
  showMoveModal.value = true
}

const showTagManagement = () => {
  isVisible.value = false
  isTagSettingOpen.value = true
}


const openDeleteModal = () => {
  isVisible.value = false
  showDeleteModal.value = true
}

const handleMoveComplete = () => {
  ElMessage.success('북마크가 성공적으로 이동되었습니다.')
  // TODO: 필요한 경우 부모 컴포넌트에 이동 완료 이벤트 발생
}
</script>

<style scoped>
.settings-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.settings-button:hover {
  background-color: #f0f0f0;
}

.settings-menu {
  width: 100%;
}

.settings-menu .el-button {
  justify-content: flex-start;
  padding: 8px 16px;
  width: 100%;
}

.settings-menu .delete-button {
  color: #f56c6c;
  border: none;
  background: none;
}

</style>
