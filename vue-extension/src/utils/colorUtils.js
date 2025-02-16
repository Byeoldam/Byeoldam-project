// 이전 색상들을 저장할 배열
let previousHues = [];
const MIN_HUE_DIFFERENCE = 30; // 최소 색상 차이

// HSL을 HEX로 변환하는 함수
const hslToHex = (h, s, l) => {
  s /= 100;
  l /= 100;

  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
  const m = l - c / 2;
  let r = 0,
    g = 0,
    b = 0;

  if (0 <= h && h < 60) {
    r = c;
    g = x;
    b = 0;
  } else if (60 <= h && h < 120) {
    r = x;
    g = c;
    b = 0;
  } else if (120 <= h && h < 180) {
    r = 0;
    g = c;
    b = x;
  } else if (180 <= h && h < 240) {
    r = 0;
    g = x;
    b = c;
  } else if (240 <= h && h < 300) {
    r = x;
    g = 0;
    b = c;
  } else if (300 <= h && h < 360) {
    r = c;
    g = 0;
    b = x;
  }

  const toHex = (value) => {
    const hex = Math.round((value + m) * 255).toString(16);
    return hex.length === 1 ? "0" + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

// 새로운 색상이 이전 색상들과 충분히 다른지 확인하는 함수
const isDistinctHue = (hue) => {
  return previousHues.every((prevHue) => {
    const diff = Math.abs(hue - prevHue);
    // 색상환을 고려하여 360도 차이도 계산
    const wrappedDiff = Math.min(diff, 360 - diff);
    return wrappedDiff >= MIN_HUE_DIFFERENCE;
  });
};

// 구분되는 새로운 색상 생성
const getDistinctHue = () => {
  let attempts = 0;
  let hue;

  do {
    hue = Math.floor(Math.random() * 359);
    attempts++;
    // 100번 시도해도 못찾으면 이전 기록 초기화
    if (attempts > 100) {
      previousHues = [];
      break;
    }
  } while (!isDistinctHue(hue));

  // 배열이 너무 길어지는 것 방지
  if (previousHues.length >= 10) {
    previousHues.shift();
  }
  previousHues.push(hue);

  return hue;
};

// 부드러운 대비의 파스텔 색상 생성
export const generatePastelColors = () => {
  const hue = getDistinctHue();
  const saturation = 85 + Math.random() * 10;
  const lightness = 92 + Math.random() * 3;

  const tagColor = hslToHex(hue, saturation - 50, lightness);
  const tagBorderColor = hslToHex(hue, saturation - 20, lightness - 25);

  return { tagColor, tagBorderColor };
};
