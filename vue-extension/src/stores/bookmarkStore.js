import { defineStore } from "pinia";
import { ref } from "vue";

export const useBookmarkStore = defineStore("bookmark", () => {
  // 사용자 인증 정보
  const user = ref({
    userId: null,
    accessToken: null,
  });

  const setUser = (id, token) => {
    userId.value = id;
    accessToken.value = token;
  };

  // 피드 목록
  const feedList = ref([]);

  const setFeedList = (feeds) => {
    feedList.value = feeds;
  };

  // 알림 목록
  const alarmList = ref([]);

  const setAlarmList = (alarms) => {
    alarmList.value = alarms;
    badges.value.notificationCnt = alarms.length;
  };

  const decreaseNotificationCount = () => {
    if (badges.value.notificationCnt > 0) {
      badges.value.notificationCnt--;
    }
  };

  const clearNotifications = () => {
    badges.value.notificationCnt = 0;
  };

  // 탭 배지 데이터
  const badges = ref([{ notificationCnt: 0, hasNewFeed: false }]);

  const setBadges = (badge) => {
    badges.value = badge;
  };

  return {
    user,
    setUser,
    feedList,
    setFeedList,
    alarmList,
    setAlarmList,
    badges,
    setBadges,
    decreaseNotificationCount,
    clearNotifications,
  };
});
