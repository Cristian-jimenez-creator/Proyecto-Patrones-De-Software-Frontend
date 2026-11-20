# PoolGuard web interface

This repository builds the Vaadin web application. Its Cameras page reads the latest JPEG from the protected PoolGuard API and refreshes about every 1.5 seconds.

## Deploy the web service to Render

The existing Render service uses `PoolGuardVaadin` as its Root Directory and `./Dockerfile` as its Dockerfile. Keep those settings. Set these environment variables on the web service:

- `POOLGUARD_BACKEND_URL`: the public HTTPS URL of the API service, for example `https://YOUR-API.onrender.com`.
- `POOLGUARD_INGEST_TOKEN`: the same private token configured on the API service.
- `POOLGUARD_CAMERA_ID`: `phone-camera`, unless the local client uses another ID.

The API URL and token stay on the web server; the browser receives only the current image rendered by the Vaadin page. Do not put the token in client-side JavaScript or commit it to Git.

## Connect the phone camera

1. Start IP Webcam on the phone and leave it connected to Wi-Fi.
2. Run the `PoolGuardBackend` local Java client on a computer on the same Wi-Fi.
3. Configure the local client with the stream URL from IP Webcam, the public HTTPS API URL, and the shared token.
4. Open the Cameras page. It shows the most recently received frame and reports when the local client stops sending.

The service stores the most recent frame in memory only. It does not record video. A separate real risk-analysis service is still required for detections; without it the application must remain in a waiting state.
