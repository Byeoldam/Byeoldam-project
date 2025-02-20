// ===============================================================================================
// [알림 뱃지 업데이트]
// ===============================================================================================

chrome.storage.onChanged.addListener((changes, namespace) => {
  if ("notificationCount" in changes) {
    const newCount = changes.notificationCount.newValue || 0;

    if (newCount > 0) {
      const badgeText = newCount > 99 ? "99+" : newCount.toString();

      // 뱃지 스타일 적용
      chrome.action.setBadgeText({ text: badgeText });
      chrome.action.setBadgeBackgroundColor({ color: "#3730A3" });
      chrome.action.setBadgeTextColor({ color: "#FFFFFF" });
    } else {
      // 알림 카운트가 0이거나 없을 때 뱃지 제거
      chrome.action.setBadgeText({ text: "" });
    }
  }
});

// ===============================================================================================
// [로그인 상태 관리 및 응답 처리]
// ===============================================================================================

let cachedLoginData = null;

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === "login") {
    cachedLoginData = message.loginData;
  }

  if (message.action === "logout") {
    cachedLoginData = null;
    chrome.storage.local.remove([
      "userId",
      "access_token",
      "notificationCount",
    ]);
  }

  // popupOpened 처리 후 로그인 정보 저장
  if (message.action === "popupOpened") {
    if (cachedLoginData) {
      saveLoginData(cachedLoginData);
    } else {
      chrome.tabs.create({ url: "http://byeoldam.store/login" });
    }
  }
});

// 로그인 정보를 Extension local Storage에 저장하는 함수
function saveLoginData(userLoginInfo) {
  if (!userLoginInfo) {
    console.warn("userLoginInfo가 null이어서 저장을 건너뜁니다.");
    return;
  }

  chrome.storage.local.get(["userId"], (result) => {
    if (result.userId !== userLoginInfo.userId) {
      chrome.storage.local.set({
        userId: userLoginInfo.userId,
        access_token: userLoginInfo.access_token,
      });
    }
  });
}

// ===============================================================================================
// [웹페이지 정보 수신 및 응답 처리]
// ===============================================================================================

// 페이지 정보를 저장할 변수
let pageInfo = {
  siteUrl: null,
  title: null,
};
let readingTimeInfo = {
  readingTime: null,
  stats: null,
};

// <1> contentScript.js로부터 페이지 정보 수신
chrome.runtime.onMessage.addListener((message, sender) => {
  if (message.action === "GET_PAGE_INFO_FROM_SITE") {
    pageInfo.siteUrl = message.url;
    pageInfo.title = message.title;
    readingTimeInfo.readingTime = message.readingTime;
    readingTimeInfo.stats = message.stats;
  }
});

// <2> StorageView로 페이지 정보 응답
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === "GET_PAGE_INFO_FROM_BACK") {
    sendResponse({
      pageInfo: pageInfo,
      readingTime: readingTimeInfo.readingTime,
    });
  }
  return true;
});

// <3> 페이지 정보 업데이트
async function getPageInfo(tabId) {
  try {
    // 1.content script가 해당 탭에 주입 가능한지 체크
    try {
      await chrome.scripting.executeScript({
        target: { tabId: tabId },
        func: () => true 
      });
    } catch (e) {
      return;
    }

    // 2. content script에 페이지 정보 수집 요청
    const response = await Promise.race([
      chrome.tabs.sendMessage(tabId, { action: "COLLECT_PAGE_INFO" }),
      new Promise((_, reject) => setTimeout(() => reject(new Error('Timeout')), 3000))
    ]);

    // 3. 응답이 있으면 페이지 정보 업데이트
    if (response) {
      pageInfo.siteUrl = response.url;
      pageInfo.title = response.title;
      readingTimeInfo.readingTime = response.readingTime;
      readingTimeInfo.stats = response.stats;
    }
  } catch (error) {
    if (!error.message?.includes('Timeout')) {
      console.error("Failed to get page info:", error);
    }
  }
}

// <4> 사용자 페이지 이동 방식에 따른 페이지 정보 요청
// 1. 탭 활성화 될때
chrome.tabs.onActivated.addListener(async (activeInfo) => {
  try {
    const tab = await chrome.tabs.get(activeInfo.tabId);
    if (
      !tab.url?.startsWith("chrome://") &&
      !tab.url?.startsWith("chrome-extension://")
    ) {
      await getPageInfo(activeInfo.tabId);
    }
  } catch (error) {
    console.error("Error handling tab activation:", error);
  }
});

// 2.페이지 업데이트 될때
chrome.tabs.onUpdated.addListener(async (tabId, changeInfo, tab) => {
  if (
    changeInfo.status === "complete" &&
    !tab.url?.startsWith("chrome://") &&
    !tab.url?.startsWith("chrome-extension://")
  ) {
    await getPageInfo(tabId);
  }
});
