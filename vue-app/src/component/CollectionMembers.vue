<template>
    <div class="members-container">
        <el-tooltip
            v-for="member in displayedMembers"
            :key="member.userId"
            placement="bottom"
            effect="dark"
            :show-after="200"
            :hide-after="200"
        >
            <template #content>
                <div class="tooltip-content">
                    <div class="nickname">
                        {{ member.nickname }}
                        <span v-if="member.role === 'OWNER'" class="crown">👑</span>
                    </div>
                    <div class="email">{{ member.email }}</div>
                </div>
            </template>
            <div class="member-avatar">
                <img 
                    :src="member.profileUrl" 
                    :alt="member.nickname"
                    class="avatar-image"
                />
            </div>
        </el-tooltip>
        <el-tooltip 
            v-if="remainingCount > 0"
            placement="bottom"
            effect="dark"
        >
            <template #content>
                <div v-for="member in remainingMembers" :key="member.userId" class="tooltip-content">
                    <div class="nickname">
                        {{ member.nickname }}
                        <span v-if="member.role === 'OWNER'" class="crown">👑</span>
                    </div>
                    <div class="email">{{ member.email }}</div>
                    <div v-if="member !== remainingMembers[remainingMembers.length-1]" class="divider"></div>
                </div>
            </template>
            <div class="more-members">
                +{{ remainingCount }}
            </div>
        </el-tooltip>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
    members: {
        type: Array,
        required: true,
        default: () => []
    }
});

const MAX_DISPLAY = 5; // 3에서 5로 수정

// 화면에 표시할 멤버 목록
const displayedMembers = computed(() => {
    return props.members.slice(0, MAX_DISPLAY);
});

// 더보기에 표시할 남은 멤버 수
const remainingCount = computed(() => {
    const remaining = props.members.length - MAX_DISPLAY;
    return remaining > 0 ? remaining : 0;
});

// 나머지 멤버 목록
const remainingMembers = computed(() => {
    return props.members.slice(MAX_DISPLAY);
});

const activeTooltip = ref(null);

const showTooltip = (userId) => {
    activeTooltip.value = userId;
};

const hideTooltip = () => {
    activeTooltip.value = null;
};
</script>

<style scoped>
.members-container {
    display: flex;
    align-items: center;
}

.member-avatar {
    position: relative;
    cursor: pointer;
    z-index: 1;
    margin-right: -8px; /* 멤버 아바타들을 살짝 겹치게 표시 */
}

.member-avatar:last-child {
    margin-right: 0; /* 마지막 아바타는 마진 제거 */
}

.avatar-image {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 2px solid white;
    transition: transform 0.2s;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.member-avatar:hover .avatar-image {
    transform: scale(1.1);
}

/* 툴팁 커스텀 스타일 */
:deep(.el-tooltip__popper) {
    padding: 12px 16px !important;
    border-radius: 8px !important;
    background-color: rgba(0, 0, 0, 0.85) !important;
    backdrop-filter: blur(8px) !important;
}

:deep(.el-tooltip__popper[data-popper-placement^='bottom'] .el-tooltip__arrow) {
    border-bottom-color: rgba(0, 0, 0, 0.85) !important;
}

.tooltip-content {
    text-align: center;
    padding: 4px 0;
}

.tooltip-content .nickname {
    font-weight: 600;
    font-size: 14px;
    color: white;
    margin-bottom: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
}

.tooltip-content .email {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
}

.tooltip-content .crown {
    font-size: 14px;
}

.divider {
    height: 1px;
    background-color: rgba(255, 255, 255, 0.1);
    margin: 8px 0;
}

.more-members {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #f0f0f0;
    color: #666;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    cursor: pointer;
    border: 2px solid white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    margin-left: -8px;
}

.more-members:hover {
    background: #e0e0e0;
    transform: scale(1.1);
    transition: all 0.2s ease;
}
</style> 