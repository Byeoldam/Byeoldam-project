<template>
  <div class="member-manage-modal">
    <h2 class="modal-title">멤버 관리</h2>
    
    <!-- 멤버 초대 섹션 -->
    <div class="invite-section">
      <input 
        v-model="inviteEmail"
        type="email"
        placeholder="초대할 멤버의 이메일을 입력하세요"
        class="invite-input"
      />
      <button class="invite-button" @click="handleInvite">
        초대하기
      </button>
    </div>

    <!-- 멤버 리스트 섹션 -->
    <div class="members-list">
      <div 
        v-for="member in members" 
        :key="member.userId"
        class="member-item"
      >
        <div class="member-info">
          <img :src="member.profileUrl" :alt="member.nickname" class="member-avatar"/>
          <div class="member-details">
            <div class="member-name">
              {{ member.nickname }}
              <span v-if="member.role === 'OWNER'" class="crown">👑</span>
            </div>
            <div class="member-email">{{ member.email }}</div>
          </div>
        </div>
        <!-- OWNER만 볼 수 있고, OWNER 자신은 제외한 멤버에 대해서만 보이는 강퇴 버튼 -->
        <button 
          v-if="isCurrentUserOwner && currentUserId !== member.userId && member.role !== 'OWNER'"
          class="eject-button"
          @click="confirmEject(member)"
        >
          <i class="fas fa-user-minus"></i>
        </button>
      </div>
    </div>

    <!-- 강퇴 확인 모달 -->
    <el-dialog
      v-model="showEjectConfirm"
      title="멤버 강퇴"
      width="30%"
      :show-close="false"
    >
      <span>{{ selectedMember?.nickname }}님을 강퇴하시겠습니까?</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEjectConfirm = false">취소</el-button>
          <el-button type="danger" @click="handleEject">네</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import { useCollectionStore } from '@/stores/collection';
import { ElMessage } from 'element-plus';

const props = defineProps({
  members: {
    type: Array,
    required: true
  },
  collectionId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close', 'refresh']);

const userStore = useUserStore();
const collectionStore = useCollectionStore();
const currentUserId = computed(() => userStore.getUserId);
const isCurrentUserOwner = computed(() => {
  return props.members.some(member => 
    member.userId === currentUserId.value && 
    member.role === 'OWNER'
  );
});

const inviteEmail = ref('');
const showEjectConfirm = ref(false);
const selectedMember = ref(null);

const handleInvite = async () => {
  try {
    // 초대 API 호출 (아직 구현되지 않음)
    ElMessage.success('초대 메일이 발송되었습니다.');
    inviteEmail.value = '';
  } catch (error) {
    ElMessage.error('초대 실패: ' + error.message);
  }
};

const confirmEject = (member) => {
  selectedMember.value = member;
  showEjectConfirm.value = true;
};

const handleEject = async () => {
  try {
    await collectionStore.removeMemberFromSharedCollection(
      props.collectionId, 
      selectedMember.value.userId
    );
    ElMessage.success('멤버가 강퇴되었습니다.');
    showEjectConfirm.value = false;
    emit('refresh');
  } catch (error) {
    ElMessage.error('강퇴 실패: ' + error.message);
  }
};
</script>

<style scoped>
.member-manage-modal {
  padding: 20px;
}

.modal-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
}

.invite-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.invite-input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.invite-button {
  padding: 8px 16px;
  background: #3730A3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.member-details {
  display: flex;
  flex-direction: column;
}

.member-name {
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.member-email {
  font-size: 0.9rem;
  color: #666;
}

.eject-button {
  padding: 8px;
  background: none;
  border: none;
  color: #dc3545;
  cursor: pointer;
}

.eject-button:hover {
  color: #c82333;
}

.crown {
  font-size: 1.2rem;
}
</style> 