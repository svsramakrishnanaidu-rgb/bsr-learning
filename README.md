# Beacon — owner-authorized lost-device recovery

Beacon is a full-stack, open-source starter for locating **your own paired Android devices**. It deliberately does not claim that an IMEI reveals GPS position: public applications cannot query carrier location by IMEI. The companion reports permission-granted GPS telemetry over the internet; when it cannot, the UI labels the device offline and retains only its last genuine report.

## Included

- Responsive React/TypeScript dashboard, OpenStreetMap + Leaflet map, device cards, offline state, status and lost-mode controls.
- Express/PostgreSQL API with ownership-scoped queries, hashed one-time pairing credentials, validation, secure headers, JWT middleware, rate limiting, location history, event/audit records, and deletion.
- PostgreSQL schema for users, devices, reports, geofences, reconnect/lost-mode events and audit logs.
- Kotlin Android reference client using Fused Location, WorkManager and Firebase Cloud Messaging.
- Privacy and terms templates. No sample coordinates are represented as real API tracking data; dashboard values are clearly a UI preview until connected.

## Architecture

`Android GPS -> authenticated HTTPS telemetry -> Express -> PostgreSQL -> dashboard`. In production, publish inserts through Supabase Realtime or a narrowly scoped WebSocket room keyed to the authenticated owner. A worker evaluates the Haversine distance for each geofence and queues entry/exit and reconnection notifications through FCM/email. FCM data must be created only by the trusted server. Never expose its service-account key to web or Android clients.

## Local setup

1. Install Node 20+, Docker and JDK 17. Run `cp .env.example apps/api/.env` and replace both secrets (`openssl rand -base64 32`).
2. Run `docker compose up -d`; the schema is loaded on the first empty database initialization.
3. Run `npm install && npm run dev`. Open `http://localhost:5173`; API health is at `http://localhost:4000/health`.
4. Create verified users through your identity provider (Supabase Auth is a free option) or add an auth controller that writes bcrypt/Argon2 hashes to `users`. Sign API JWTs with a short expiry and `sub=user.id`. Production email verification must be complete before a pairing token can be issued.
5. Open `android/` in Android Studio, change `API_BASE_URL`, add the Firebase project `google-services.json` locally, and request location/notification permissions with an in-app disclosure before scheduling `LocationWorker`.

## Production checklist

- Deploy the web app to Cloudflare Pages/Vercel, API to Fly.io/Render, and PostgreSQL to Supabase/Neon free tiers (subject to their current limits). Apply `database/schema.sql` with a migration tool.
- Terminate TLS, rotate JWT/encryption/FCM secrets, encrypt IMEIs with a KMS-backed key, log secret access, tighten CORS, and set an explicit history-retention job.
- Add email verification and refresh-token rotation through the selected identity provider. Use opaque, revocable device tokens and Android Keystore; never log them.
- Implement the documented server-side FCM queue and geofence worker. The API currently records durable events and returns `push: queued`; it never falsely reports delivery.
- Configure offline detection (for example, no heartbeat for 5 minutes), realtime row authorization, backups, alerting, abuse thresholds and owner export/deletion workflows.
- Obtain legal/privacy review. Background location is subject to Android and app-store policy and may be throttled. IMEI access is restricted on modern Android and should be omitted unless the app qualifies and consent/law permit it.

## Repository layout

```text
apps/web/       React dashboard
apps/api/       Express API
android/        Kotlin companion reference
database/       PostgreSQL schema
docs/           Privacy and terms templates
```

## API outline

Owner requests use `Authorization: Bearer <JWT>`. Device reports use `X-Device-Token`; only its SHA-256 digest is stored. Routes: `GET /api/devices`, `POST /api/devices/pair`, `POST /api/device/:id/telemetry`, `GET|DELETE /api/devices/:id/locations`, and `POST /api/devices/:id/lost-mode`.
