# Manual tests

## Step 1: login and navigation (requires a backend that implements login)
1. Start the backend and the frontend. Open `http://localhost:8081`: it must redirect to `/login`.
2. Wrong credentials: a Spanish error without technical details.
3. Backend stopped: "El servicio no está disponible."
4. Valid login: the empty dashboard and only the menu entries allowed for the role.
5. Open `/password-recovery` while logged out: the form renders and the submit button is disabled.
6. In DevTools > Application: no token in localStorage, sessionStorage or JS-readable cookies.
7. In DevTools > Network: no request goes to the backend host; only to the frontend origin.
8. In DevTools > Console: no Content Security Policy violations on `/login` and `/`.
9. Enable "reduce motion" in the operating system and reload: no entrance animations.
10. Let the session expire: the browser must land on `/login?expired`.

## Real MJPEG phone webcam (steps 4 and 5)
Prerequisites: a phone running IP Webcam Pro on the same network as the backend or the LocalCameraGateway,
for example `http://192.168.101.11:8080`.
1. From a browser on that network, open the app's MJPEG stream URL (IP Webcam usually serves `/video`; confirm in the app)
   and check that video appears. The app's "video connections" counter should increase.
2. Register the camera and run the connection test: success must be reported only if the backend reaches the stream.
3. Open the camera detail: video must come through the Vaadin same-origin resource. In DevTools > Network the phone's IP must never appear.
4. Close the phone app: the view must show an unavailable state with a retry action, no frozen frame and no animation that suggests it works.
5. If no real camera is reachable, this test stays **pending**. It is never replaced by a simulation.
