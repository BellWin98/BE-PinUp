// import { URLSearchParams } from 'https://jslib.k6.io/url/1.0.0/index.js';
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import { randomSeed } from 'k6';

// Initialize random seed for consistent test runs
randomSeed(42);

// Custom metrics
const errorRate = new Rate('error_rate');
const placeApiTrend = new Trend('place_api_request_time');

// Test configuration options
export const options = {
    scenarios: {
        load_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 1000 },
                { duration: '30s', target: 1000 },
                { duration: '10s', target: 3000 },
                { duration: '30s', target: 3000 },
                { duration: '10s', target: 0 },
            ],
        },
        peak_test: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 200,
            maxVUs: 500,
            stages: [
                { duration: '10s', target: 1000 },
                { duration: '20s', target: 5000 },
                { duration: '30s', target: 1000 },
                { duration: '10s', target: 5000 },
            ],
        },
    },
/*    stages: [
        { duration: '5s', target: 10 },   // Ramp-up to 10 users over 30 seconds
        { duration: '5s', target: 10 },    // Stay at 10 users for 1 minute
        { duration: '10s', target: 50 },   // Ramp-up to 50 users over 30 seconds
        { duration: '5s', target: 50 },    // Stay at 50 users for 3 minutes
        { duration: '5s', target: 100 },  // Ramp-up to 100 users over 30 seconds
        { duration: '5s', target: 100 },   // Stay at 100 users for 5 minutes
        { duration: '10s', target: 0 },     // Ramp-down to 0 users over 1 minute
    ],*/
};

// Main test function
export default function() {
    const bound = {
        swLatitude: 37.2939589,
        swLongitude: 126.676298,
        neLatitude: 37.70905,
        neLongitude: 126.9770487,
        currentLatitude: 37.5640034,
        currentLongitude: 126.8357822
    }
    const keyword = '';
    const category = 'ALL';
    const sort = 'NEAR';

    // Construct the URL with query parameters
    const baseUrl = 'http://localhost:8080/api/places?category=ALL&sort=NEAR&swLatitude=37.548608&swLongitude=126.795968&neLatitude=37.56994&neLongitude=126.844764&currentLatitude=37.562651&currentLongitude=126.826539';
/*    const searchParams = new URLSearchParams([
        ['query', keyword],
        ['category', category],
        ['sort', sort],
        ['swLatitude', bound.swLatitude],
        ['swLongitude', bound.swLongitude],
        ['neLatitude', bound.neLatitude],
        ['neLongitude', bound.neLongitude],
        ['currentLatitude', bound.currentLatitude],
        ['currentLongitude', bound.currentLongitude],
    ]);*/

    // Common headers for all requests
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTYwODEzNTUzNDE1Nzg2MDYzODMiLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQyMjc2MjI5LCJleHAiOjE3NDIyOTc4Mjl9.xfBauBbIc3C6zlW4bEhPzi49Zbsq9Oa-WCDmsZP-Hx0' // Replace with actual authentication if needed
    };

    // Make the API request
    const startTime = new Date().getTime();
/*    const response = http.get(`${baseUrl}?${searchParams.toString()}`, {
        headers: headers
    });*/
    const response = http.get(baseUrl, {
        headers: headers
    });
    const endTime = new Date().getTime();

    // Record custom metric for response time
    placeApiTrend.add(endTime - startTime);

    // Check if the request was successful
    const success = check(response, {
        'status is 200': (r) => r.status === 200,
        'response body has data': (r) => r.json('data') !== null,
    });

    // Record error rate
    errorRate.add(!success);

    // Log detailed info for failed requests
    if (!success) {
        console.log(`Request failed with status ${response.status}. Body: ${response.body}`);
    }

    // Add a sleep interval to simulate user behavior (1-5 seconds)
    sleep(Math.random() * 4 + 1);
}