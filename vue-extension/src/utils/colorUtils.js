// HSL을 HEX로 변환하는 함수
const hslToHex = (h, s, l) => {
  s /= 100;
  l /= 100;

  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - Math.abs((h / 60) % 2 - 1));
  const m = l - c/2;
  let r = 0, g = 0, b = 0;

  if (0 <= h && h < 60) {
    r = c; g = x; b = 0;
  } else if (60 <= h && h < 120) {
    r = x; g = c; b = 0;
  } else if (120 <= h && h < 180) {
    r = 0; g = c; b = x;
  } else if (180 <= h && h < 240) {
    r = 0; g = x; b = c;
  } else if (240 <= h && h < 300) {
    r = x; g = 0; b = c;
  } else if (300 <= h && h < 360) {
    r = c; g = 0; b = x;
  }

  const toHex = (value) => {
    const hex = Math.round((value + m) * 255).toString(16);
    return hex.length === 1 ? '0' + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

// 랜덤 파스텔 색상 생성 함수
export const generatePastelColors = () => {
  const hue = Math.floor(Math.random() * 359); // 0-359 범위의 색조
  const saturation = 65 + Math.random() * 15;  // 65-80% 채도 (더 부드러운 파스텔)
  const lightness = 75 + Math.random() * 15;   // 75-90% 명도 (더 밝은 색상)

  // 배경색 (더 밝은 색상)
  const tagColor = hslToHex(hue, saturation, lightness);
  
  // 테두리색 (더 진한 색상)
  const tagBorderColor = hslToHex(hue, saturation + 10, lightness - 15);

  return { tagColor, tagBorderColor };
};