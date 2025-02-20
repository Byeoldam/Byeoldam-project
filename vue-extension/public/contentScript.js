// <1> 별담 사이트에서 postMessage로 전달된 로그인 정보 수신
window.addEventListener("message", (event) => {
  if (event.origin !== window.location.origin || event.source !== window)
    return;

  // 로그인 이벤트 수신
  if (event.data && event.data.type === "LOGIN") {
    chrome.runtime.sendMessage({
      action: "login",
      loginData: event.data.data,
    });
  }

  // 로그아웃 이벤트 수신
  if (event.data && event.data.type === "LOGOUT") {
    chrome.runtime.sendMessage({
      action: "logout",
      loginData: null,
    });
  }
});

// <2> 웹페이지의 DOM에 직접 접근하여 읽기 시간 계산(WPM)
function calculateReadingTime() {
  // 1. 본문 찾기 및 정제
  const mainContent = document.querySelector("article, main") || document.body;
  const cleanContent = cleanupContent(mainContent);
  const text = cleanContent.textContent.trim();

  // 2. 문자 분석
  const stats = {
    koreanChars: (text.match(/[\uAC00-\uD7AF]/g) || []).length,
    englishWords: (text.match(/[a-zA-Z]+/g) || []).length,
    numbers: (text.match(/\d+/g) || []).length,
    specialChars: (text.match(/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]+/g) || [])
      .length,
  };

  // 3. 분당 읽기 시간 계산
  const readingSpeed = {
    korean: 600,
    english: 300,
    number: 300,
    special: 1000,
  };

  const readingTime =
    stats.koreanChars / readingSpeed.korean +
    stats.englishWords / readingSpeed.english +
    stats.numbers / readingSpeed.number +
    stats.specialChars / readingSpeed.special;

  const totalTime = Math.max(1, Math.ceil(readingTime));

  return {
    totalTime,
    stats,
    text,
  };
}

// 불필요한 요소 제거
function cleanupContent(content) {
  const excludeSelectors = [
    "nav",
    "header",
    "footer",
    "aside",
    ".navigation",
    ".nav",
    ".menu",
    ".sidebar",
    ".ad",
    ".advertisement",
    ".comments",
    ".social-share",
    "script",
    "style",
    "iframe",
  ].join(",");

  const tempDiv = content.cloneNode(true);
  const elementsToRemove = tempDiv.querySelectorAll(excludeSelectors);
  elementsToRemove.forEach((el) => el.remove());

  return tempDiv;
}

// <3> 페이지 정보 수집
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === "COLLECT_PAGE_INFO") {
    try {
      const readingTimeInfo = calculateReadingTime();
      const pageTitle =
        document.querySelector('meta[property="og:title"]')?.content ||
        document.title;

      sendResponse({
        url: window.location.href,
        title: pageTitle,
        readingTime: readingTimeInfo.totalTime,
        stats: readingTimeInfo.stats,
      });
    } catch (error) {
      console.error("Error collecting page info:", error);
      sendResponse(null);
    }
  }
  return true;
});

// <4> 페이지 로드 시 페이지 정보 전송
window.addEventListener('load', () => {
  const readingTimeInfo = calculateReadingTime();
  const pageTitle =
    document.querySelector('meta[property="og:title"]')?.content ||
    document.title;

  chrome.runtime.sendMessage({
    action: "GET_PAGE_INFO_FROM_SITE",
    url: window.location.href,
    title: pageTitle,
    readingTime: readingTimeInfo.totalTime,
    stats: readingTimeInfo.stats,
  }, (response) => {
    if (chrome.runtime.lastError) {
      console.error('Failed to send initial page info:', chrome.runtime.lastError);
    }
  });
});