import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

export let responseTime = new Trend('response_time');

export let options = {
    stages: [
        { duration: '10s', target: 5000 },
    ],
};

export default function () {
    let userId = Math.floor(Math.random() * 1000000) + 1;  // 1~100만 유저 랜덤 선택
    let url = `http://localhost:8080/api/v1/users/${userId}/coupons`;

    let params = {
        headers: {
            'Authorization': `${userId}`,
            'Content-Type': 'application/json'
        },
    };

    let payload = JSON.stringify({
        couponPolicyId: 1
    });

    let res = http.post(url, payload, params);

    responseTime.add(res.timings.duration);
    check(res, {
        '✅ 쿠폰 발급 요청 성공 (200)': (r) => r.status === 200,
        '❌ 서버 오류 (500)': (r) => r.status === 500,
    });

    sleep(Math.random() * 0.3);
}