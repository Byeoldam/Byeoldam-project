// ===============================================================================================
// [알림 뱃지 업데이트]
// ===============================================================================================

chrome.storage.onChanged.addListener((changes, namespace) => {
  // 로컬 스토리지에서 notificationCount 가져오기
  chrome.storage.local.get(["notificationCount"], (result) => {
    const notificationCount = result.notificationCount || 0;

    // 알림 카운트 변경 감지 및 뱃지 업데이트
    if (notificationCount > 0) {
      chrome.action.setBadgeText({ text: notificationCount.toString() });
      chrome.action.setBadgeBackgroundColor({ color: "#FF0000" });
    } else {
      chrome.action.setBadgeText({ text: "" }); // 알림 카운트가 없으면 뱃지 비우기
    }
  });

  // 알림 없는 경우 뱃지 제거
  if (!changes.notificationCount?.newValue) {
    chrome.action.setBadgeText({ text: "" });
  }
});

// 테스트를 위한 초기 실행
// chrome.runtime.onInstalled.addListener(() => {
//   // 초기 상태 설정
//   chrome.storage.local.set({
//     notificationCount: 5,
//     hasNewFeed: true,
//   });
// });

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
    chrome.storage.local.remove(["userId", "access_token"]);
  }

  // popupOpened 처리 후 로그인 정보 저장
  if (message.action === "popupOpened") {
    if (cachedLoginData) {
      saveLoginData(cachedLoginData);
      // sendResponse({ status: "success" }); // 응답을 보내 popupOpened 처리가 완료되었음을 알림
    } else {
      // else >> *********** test용 **************
      const testLoginData = {
        userId: "jun@naver.com",
        access_token:
          "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6ImFjY2VzcyIsInVzZXJJZCI6MSwiZW1haWwiOiJqdW5AbmF2ZXIuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTczOTc3ODM3MSwiZXhwIjoxNzQwMzc4MzcxfQ.wFdrBIwfTlaaqu5cQZg_M0LLn-51gFA_7jVaOn1dgAo",
      };
      saveLoginData(testLoginData);

      // chrome.tabs.create({ url: "http://byeoldam.store/login" });
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

    // console.log("페이지 타이틀 : " , pageInfo.title);
    // console.log("언어별 전처리 통계:", readingTimeInfo.stats);
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
