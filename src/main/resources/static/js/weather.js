async function loadWeather() {
    try {
        // 現在地取得
        const pos = await new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(resolve, reject);
        });
        const lat = pos.coords.latitude;
        const lon = pos.coords.longitude;

        // 天気情報取得
        const res = await fetch(`/api/weather?lat=${lat}&lon=${lon}`);
        const data = await res.json();

        // 現在の天気と湿度
        const current = data.current;
        const daily = data.daily.slice(0, 7); // 7日分

        const widget = document.getElementById('weather-widget');
        widget.innerHTML = `
            <div class="p-3 bg-white rounded-xl shadow-lg border text-sm text-gray-700 w-64">
                <p class="font-semibold mb-1">現在地の天気</p>
                <p>${current.weather[0].description} ${current.temp.toFixed(1)}°C 湿度: ${current.humidity}%</p>
                <hr class="my-2" />
                <p class="font-semibold">週間予報</p>
                <ul class="text-xs">
                    ${daily.map(day => {
                        const date = new Date(day.dt * 1000);
                        const label = `${date.getMonth() + 1}/${date.getDate()}`;
                        const temp = `${day.temp.min.toFixecd(0)}~${day.temp.max.toFixed(0)}°C`;
                        const weather = day.weather[0].main;
                        return `<li>${label}: ${weather} (${temp})</li>`;
                    }).join('')}
                    </ul>
                </div>
            `;
        } catch (e) {
            console.error("天気取得エラー:", e);
        }
}

document.addEventListener("DOMContentLoaded", loadWeather);
