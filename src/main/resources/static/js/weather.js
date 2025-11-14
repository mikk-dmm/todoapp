const DEFAULT_COORDINATES = { lat: 35.6812, lon: 139.7671 }; // 東京駅付近

const GEO_REASON_LABELS = {
    granted: '現在地',
    denied: '東京 (位置情報未許可)',
    timeout: '東京 (タイムアウト)',
    unavailable: '東京 (取得失敗)',
    unsupported: '東京 (非対応端末)'
};

const GEO_REASON_MESSAGES = {
    denied: '位置情報が拒否されたため、東京の天気を表示します。',
    timeout: '位置情報の取得に時間がかかったため、東京の天気を表示します。',
    unavailable: '位置情報を取得できなかったため、東京の天気を表示します。',
    unsupported: 'ブラウザが位置情報に対応していないため、東京の天気を表示します。'
};

function renderInfo(widget, message) {
    widget.innerHTML = `
        <div class="flex flex-col items-center justify-center gap-2 text-center text-[13px] sm:text-sm text-gray-500">
            <div class="h-12 w-12 rounded-full bg-blue-50 flex items-center justify-center text-blue-500 text-lg">☁️</div>
            <p class="leading-snug">${message}</p>
        </div>
    `;
}

function renderError(widget, message, detail) {
    widget.innerHTML = `
        <div class="space-y-3 text-center text-xs sm:text-sm">
            <p class="text-red-600 font-semibold">${message}</p>
            ${detail ? `<p class="text-[11px] text-gray-500 break-words leading-relaxed">${detail}</p>` : ''}
            <p class="text-[11px] text-gray-400">設定を確認してから再読込してください。</p>
        </div>
    `;
}

function formatTemperature(value) {
    return Number.isFinite(value) ? `${value.toFixed(1)}°C` : '--°C';
}

function formatHumidity(value) {
    return Number.isFinite(value) ? `${Number(value).toFixed(0)}%` : '--';
}

function formatWind(value) {
    return Number.isFinite(value) ? `${value.toFixed(1)}m/s` : '-- m/s';
}

function renderWeather(widget, { data, locationLabel, notes, isFallback }) {
    const current = data.current || {};
    const primaryWeather = Array.isArray(current.weather) && current.weather[0] ? current.weather[0] : {};
    const description = primaryWeather.description ? primaryWeather.description : '---';
    const iconCode = primaryWeather.icon ? primaryWeather.icon : null;
    const iconUrl = iconCode ? `https://openweathermap.org/img/wn/${iconCode}@4x.png` : null;
    const temperature = formatTemperature(current.temp);
    const humidity = formatHumidity(current.humidity);
    const wind = formatWind(current.wind_speed);
    const noteColor = isFallback ? 'text-amber-700' : 'text-gray-500';
    const detail = typeof data.detail === 'string' ? data.detail.trim() : '';
    const reasonLabel = data.reason || (isFallback ? '原因不明' : 'OK');
    const statusBadge = isFallback ? 'bg-rose-50 text-rose-700 ring-1 ring-rose-100' : 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-100';
    const statusText = isFallback ? `取得失敗 / ${reasonLabel}` : '取得成功';
    const locationName = typeof data.locationName === 'string' && data.locationName.trim().length > 0
        ? data.locationName.trim()
        : '';

    const notesHtml = notes.length
        ? `<ul class="text-[11px] ${noteColor} space-y-1 break-words list-disc list-inside">${notes.map((note) => `<li>${note}</li>`).join('')}</ul>`
        : '';
    const detailHtml = detail && isFallback
        ? `<p class="text-[11px] text-gray-400 break-words mt-1">詳細: ${detail}</p>`
        : '';

    widget.innerHTML = `
        <div class="space-y-4 text-gray-700">
            <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                    <p class="font-semibold text-sm sm:text-base">天気</p>
                    <p class="text-xs text-gray-500">現在の状況</p>
                </div>
                <div class="flex flex-col gap-1 text-right">
                    <span class="inline-flex items-center justify-center px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 text-[11px] sm:text-xs whitespace-nowrap">${locationLabel}</span>
                    <span class="inline-flex items-center justify-center px-2 py-0.5 rounded-full ${statusBadge} text-[11px] sm:text-xs whitespace-nowrap">${statusText}</span>
                </div>
            </div>
            ${notesHtml}
            ${detailHtml}
            <div class="grid gap-4 sm:grid-cols-2 items-stretch">
                <div class="space-y-3">
                    <p class="text-xs uppercase tracking-wide text-gray-500">現在</p>
                    <p class="text-sm sm:text-base break-words">${description}</p>
                    <div class="flex flex-wrap items-baseline gap-4">
                        <span class="text-2xl sm:text-3xl font-semibold font-mono whitespace-nowrap">${temperature}</span>
                        <span class="text-xs text-gray-500 whitespace-nowrap">湿度 ${humidity}</span>
                        <span class="text-xs text-gray-500 whitespace-nowrap">風速 ${wind}</span>
                    </div>
                    ${locationName ? `<p class="text-xs text-gray-500">観測地点: ${locationName}</p>` : ''}
                </div>
                <div class="flex items-center justify-center">
                    ${iconUrl
        ? `<div class="flex flex-col items-center gap-1">
                            <img src="${iconUrl}" alt="天気アイコン" class="w-20 h-20 sm:w-24 sm:h-24" loading="lazy" decoding="async" />
                            <p class="text-[11px] text-gray-500">${iconCode}</p>
                        </div>`
        : '<p class="text-xs text-gray-400 text-center">アイコン情報がありません</p>'}
                </div>
            </div>
        </div>
    `;
}

function interpretGeolocationError(error) {
    if (!error || typeof error.code !== 'number') {
        return 'unavailable';
    }
    const mapping = {
        1: 'denied',
        2: 'unavailable',
        3: 'timeout'
    };
    return mapping[error.code] || 'unavailable';
}

async function getCoordinates() {
    if (!navigator.geolocation) {
        return { coords: DEFAULT_COORDINATES, reason: 'unsupported' };
    }

    return new Promise((resolve) => {
        navigator.geolocation.getCurrentPosition(
            ({ coords }) => resolve({
                coords: { lat: coords.latitude, lon: coords.longitude },
                reason: 'granted'
            }),
            (error) => resolve({
                coords: DEFAULT_COORDINATES,
                reason: interpretGeolocationError(error)
            }),
            { timeout: 8000, maximumAge: 600000 }
        );
    });
}

async function fetchWeatherData(lat, lon) {
    const params = new URLSearchParams({
        lat: lat.toFixed(4),
        lon: lon.toFixed(4)
    });

    const response = await fetch(`/api/weather?${params.toString()}`, {
        headers: { Accept: 'application/json' },
        cache: 'no-store'
    });
    const text = await response.text();
    let data = {};

    if (text) {
        try {
            data = JSON.parse(text);
        } catch (parseError) {
            console.error('天気APIレスポンス解析エラー:', parseError);
            throw new Error('天気情報の解析に失敗しました');
        }
    }

    if (!response.ok) {
        const message = data && data.message ? data.message : `天気API呼び出しエラー: ${response.status}`;
        throw new Error(message);
    }

    return data;
}

async function loadWeather() {
    const widget = document.getElementById('weather-widget');
    if (!widget) return;

    renderInfo(widget, '位置情報を確認しています…');
    const slowNoticeId = window.setTimeout(() => {
        renderInfo(widget, '位置情報の取得に時間がかかっています…');
    }, 5000);

    try {
        const { coords, reason } = await getCoordinates();
        window.clearTimeout(slowNoticeId);
        renderInfo(widget, '天気情報を取得しています…');

        const data = await fetchWeatherData(coords.lat, coords.lon);
        if (!data || !data.current) {
            throw new Error('天気データの形式が不正です');
        }

        const locationLabel = GEO_REASON_LABELS[reason] || GEO_REASON_LABELS.granted;
        const notes = [];
        if (reason !== 'granted' && GEO_REASON_MESSAGES[reason]) {
            notes.push(GEO_REASON_MESSAGES[reason]);
        }
        if (typeof data.message === 'string' && data.message.trim().length > 0) {
            notes.push(data.message.trim());
        }
        if (data.success === false && typeof data.detail === 'string' && data.detail.trim().length > 0) {
            notes.push(`サーバー詳細: ${data.detail.trim()}`);
        }

        renderWeather(widget, {
            data,
            locationLabel,
            notes,
            isFallback: data.success === false
        });
    } catch (error) {
        window.clearTimeout(slowNoticeId);
        console.error('天気取得エラー:', error);
        const detail = error && error.message ? error.message : '';
        renderError(widget, '天気情報を取得できませんでした', detail);
    }
}

document.addEventListener('DOMContentLoaded', loadWeather);
