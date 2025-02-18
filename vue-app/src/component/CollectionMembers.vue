<template>
    <div class="members-list">
        <div v-for="(member, index) in members" 
             :key="member.userId" 
             class="member-item"
             :style="{ marginLeft: index > 0 ? '-8px' : '0' }"
             @mouseover="showTooltip(member.userId)"
             @mouseleave="hideTooltip"
        >
            <img 
                :src="member.profileUrl" 
                :alt="member.nickname"
                class="member-avatar"
            />
            <div class="tooltip" v-if="activeTooltip === member.userId">
                <div class="tooltip-content">
                    <strong>{{ member.nickname }}</strong>
                    <span>{{ member.email }}</span>
                </div>
                <div class="tooltip-arrow"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
    members: {
        type: Array,
        required: true,
        default: () => []
    }
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
.members-list {
    display: flex;
    align-items: center;
}

.member-item {
    position: relative;
    cursor: pointer;
    z-index: 1;
}

.member-item:hover {
    z-index: 2;
}

.member-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 2px solid white;
    transition: transform 0.2s;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.member-item:hover .member-avatar {
    transform: scale(1.1);
}

.tooltip {
    position: absolute;
    bottom: calc(100% + 10px);
    left: 50%;
    transform: translateX(-50%);
    background: #333;
    color: white;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 0.9rem;
    white-space: nowrap;
    z-index: 1000;
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.tooltip-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
}

.tooltip-content strong {
    color: #fff;
}

.tooltip-content span {
    color: #ccc;
    font-size: 0.8rem;
}

.tooltip-arrow {
    position: absolute;
    bottom: -5px;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 0;
    border-left: 6px solid transparent;
    border-right: 6px solid transparent;
    border-top: 6px solid #333;
}
</style> 