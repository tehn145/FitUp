import * as admin from "firebase-admin";
import * as logger from "firebase-functions/logger";

import { onSchedule } from "firebase-functions/v2/scheduler";
import { onDocumentUpdated } from "firebase-functions/v2/firestore";
import { onRequest } from "firebase-functions/v2/https";

import { getFirestore, FieldValue, Timestamp, FirestoreDataConverter } from "firebase-admin/firestore";
import type { Request, Response } from "express";

// -------------------- INIT --------------------
const app = admin.apps.length ? admin.app() : admin.initializeApp();
const db = getFirestore(app);

// -------------------- TYPES --------------------
interface Workout {
  name: string;
  reps: number;
  sets: number;
}

type UserDoc = {
  name?: string;
  role?: string;
  location?: string;
  time?: string;
  goal?: string;
  experience?: number;
  rating?: number;
  bio?: string;
  email?: string;
  phone?: string | number;

  gender?: string;
  age?: number;
  level?: number;
  height?: number;
  weight?: number;

  gem?: number;
};

export interface Session {
  scheduledTimestamp: Timestamp;
  startHour: number;
  startMinute: number;
  endHour: number;
  endMinute: number;
  status?: "pending" | "expired" | "cancelled" | "completed";
}

type ChallengeDoc = {
  id: string;
  workouts: Workout[];
  createdAt?: FirebaseFirestore.Timestamp | FirebaseFirestore.FieldValue;
  updatedAt?: FirebaseFirestore.Timestamp | FirebaseFirestore.FieldValue;
};

export const sessionConverter: FirestoreDataConverter<Session> = {
  toFirestore(session: Session) {
    return session;
  },
  fromFirestore(snapshot) { 
    return snapshot.data() as Session;
  }
};

// -------------------- HELPERS --------------------
function generateRandomWorkouts(): Workout[] {
  const workouts = [
    "Push Ups",
    "Squats",
    "Pull Ups",
    "Burpees",
    "Sit Ups",
    "Lunges",
    "Plank",
    "Mountain Climbers",
    "Jumping Jacks",
  ];

  const getRandom = <T,>(arr: T[]): T =>
    arr[Math.floor(Math.random() * arr.length)];

  return Array.from({ length: 3 }, () => ({
    name: getRandom(workouts),
    reps: Math.floor(Math.random() * 10) + 10,
    sets: Math.floor(Math.random() * 2) + 1,
  }));
}

// --------------- EXPIRED FUNCT

export function isSessionExpired(sessionDoc : Session) {
  const {
    scheduledTimestamp,
    endHour,
    endMinute
  } = sessionDoc;

  // Convert Firestore Timestamp → JS Date
  const sessionDate = scheduledTimestamp.toDate();

  // Build session end datetime
  const sessionEnd = new Date(sessionDate);
  sessionEnd.setHours(endHour, endMinute, 0, 0);

  return Date.now() > sessionEnd.getTime();
}


export const expireSessionsJob = onSchedule("every 30 minutes", async () => {
  const now = new Date();

  const snapshot = await db
    .collection("sessions")
    .withConverter(sessionConverter)
    .where("scheduledTimestamp", "<=", now)
    .where("status", "==", "pending")
    .get();

  const batch = db.batch();

  snapshot.docs.forEach((doc) => {
    if (isSessionExpired(doc.data())) {
      batch.update(doc.ref, {
        status: "expired",
        expiredAt: Timestamp.now()
      });
    }
  });

  await batch.commit();
});

async function updateDailyChallenges(): Promise<void> {
  const challengeIds = ["challenge01", "challenge02", "challenge03"];

  for (const id of challengeIds) {
    const ref = db.collection("challenges").doc(id);
    const snap = await ref.get();
    const newWorkouts = generateRandomWorkouts();

    if (!snap.exists) {
      logger.info(`Creating ${id}...`);
      const challengeData: ChallengeDoc = {
        id,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        workouts: newWorkouts,
      };
      await ref.set(challengeData);
    } else {
      logger.info(`Updating ${id}...`);
      await ref.update({
        workouts: newWorkouts,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
  }

  logger.info("Daily challenges updated successfully");
}

export const dailyChallengeUpdater = onSchedule(
  { schedule: "every 24 hours", timeZone: "Asia/Bangkok" },
  async () => {
    await updateDailyChallenges();
  }
);

export const onDailyChallengeComplete = onDocumentUpdated(
  "users/{userId}/daily_progress/{date}",
  async (event) => {
    if (!event.data) {
      logger.warn("No event data provided. Exiting.");
      return;
    }

    const afterSnap = event.data.after;
    const beforeSnap = event.data.before;

    if (!afterSnap?.exists || !beforeSnap?.exists) {
      logger.warn("Document snapshots missing or deleted.");
      return;
    }

    const newData = afterSnap.data() as any;
    const oldData = beforeSnap.data() as any;

    if (
      newData.all_tasks_completed !== true ||
      oldData.all_tasks_completed === true
    ) {
      logger.info("Not a new completion event.");
      return;
    }

    if (newData.gem_awarded_today === true) {
      logger.info("Gem already awarded for this day.");
      return;
    }

    const { userId } = event.params;
    const userRef = db.collection("users").doc(userId);

    logger.info(`Awarding 1 gem to user: ${userId}`);

    const batch = db.batch();

    batch.update(userRef, { gem: FieldValue.increment(1) });

    batch.update(afterSnap.ref, { gem_awarded_today: true });

    try {
      await batch.commit();
      logger.info(`Successfully awarded gem to ${userId}.`);
    } catch (error) {
      logger.error("Error committing gem award batch:", error);
    }
  }
);

export const matchCoaches = onRequest(
  { cors: true, region: "asia-southeast1" },
  async (req: Request, res: Response) => {
    try {
      const uid = String(req.query.uid ?? "").trim();
      const q = String(req.query.q ?? "").trim().toLowerCase();

      if (!uid) {
        res.status(400).json({ ok: false, message: "Missing uid" });
        return;
      }

      // 1) Load ME
      const meSnap = await db.collection("users").doc(uid).get();
      if (!meSnap.exists) {
        res
          .status(404)
          .json({ ok: false, message: `User not found: ${uid}` });
        return;
      }
      const me = (meSnap.data() ?? {}) as UserDoc;

      // 2) Load COACHES
      const coachSnap = await db
        .collection("users")
        .where("role", "==", "coach")
        .limit(50)
        .get();

      const coaches: Array<{ id: string; data: UserDoc }> = coachSnap.docs
        .filter((d) => d.id !== uid)
        .map((d) => ({ id: d.id, data: (d.data() ?? {}) as UserDoc }));

      const score = (c: UserDoc) => {
        let s = 0;

        if (me.goal && c.goal && me.goal === c.goal) s += 40;
        if (me.location && c.location && me.location === c.location) s += 25;
        if (me.time && c.time && me.time === c.time) s += 15;

        const rating = Number(c.rating ?? 0);
        const exp = Number(c.experience ?? 0);
        s += Math.min(20, rating * 4);
        s += Math.min(10, exp);

        if (q) {
          const name = String(c.name ?? "").toLowerCase();
          const bio = String(c.bio ?? "").toLowerCase();
          if (name.includes(q) || bio.includes(q)) s += 10;
        }

        return s;
      };

      const results = coaches
        .map(({ id, data }) => ({
          userId: id,
          name: data.name ?? "Unknown",
          role: data.role ?? "coach",
          location: data.location ?? "",
          time: data.time ?? "",
          goal: data.goal ?? "",
          experience: data.experience ?? null,
          rating: data.rating ?? null,
          bio: data.bio ?? "",
          score: score(data),
        }))
        .sort((a, b) => b.score - a.score)
        .slice(0, 3);

      res.json({ ok: true, uid, results });
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : String(e);
      res.status(500).json({ ok: false, message: msg });
    }
  }
);

// -------------------- RUN ON START (EMULATOR/DEPLOY) --------------------
(async () => {
  // Chỉ chạy khi emulator hoặc đang deploy/serve thật
  const isEmulator =
    !!process.env.FUNCTIONS_EMULATOR ||
    process.env.FUNCTIONS_EMULATOR === "true";
  const hasProject = !!process.env.GCLOUD_PROJECT;

  if (isEmulator || hasProject) {
    try {
      logger.info("Running initial daily challenge update...");
      await updateDailyChallenges();
    } catch (err) {
      logger.error("Initial daily challenge run failed:", err);
    }
  }
})();
