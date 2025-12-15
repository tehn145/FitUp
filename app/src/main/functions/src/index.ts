import { onSchedule } from "firebase-functions/v2/scheduler";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";
import { getFirestore, FieldValue } from "firebase-admin/firestore";
import { onDocumentUpdated } from "firebase-functions/v2/firestore";

const app = admin.initializeApp();
const db = getFirestore(app);

interface Workout {
  name: string;
  reps: number;
  sets: number;
}

interface Challenge {
  id: string;
  createdAt?: FirebaseFirestore.FieldValue;
  updatedAt?: FirebaseFirestore.FieldValue;
  workouts: Workout[];
}

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
  const getRandom = <T>(arr: T[]): T => arr[Math.floor(Math.random() * arr.length)];

  return Array.from({ length: 3 }, () => ({
    name: getRandom(workouts),
    reps: Math.floor(Math.random() * 20) + 10,
    sets: Math.floor(Math.random() * 4) + 1,
  }));
}

async function updateDailyChallenges(): Promise<void> {
  const challengeIds = ["challenge01", "challenge02", "challenge03"];

  for (const id of challengeIds) {
    const ref = db.collection("challenges").doc(id);
    const snap = await ref.get();
    const newWorkouts = generateRandomWorkouts();

    if (!snap.exists) {
      logger.info(`Creating ${id}...`);
      const challengeData: Challenge = {
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
    // ✅ Add guards for TypeScript safety
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

    const newData = afterSnap.data();
    const oldData = beforeSnap.data();

    if (newData.all_tasks_completed !== true || oldData.all_tasks_completed === true) {
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

    // Action 1: Increment the user's gem count
    batch.update(userRef, { gem: FieldValue.increment(1) });

    // Action 2: Mark gem_awarded_today = true
    batch.update(afterSnap.ref, { gem_awarded_today: true });

    try {
      await batch.commit();
      logger.info(`Successfully awarded gem to ${userId}.`);
    } catch (error) {
      logger.error("Error committing gem award batch:", error);
    }
  }
);



(async () => {
  if (process.env.FUNCTIONS_EMULATOR || process.env.GCLOUD_PROJECT) {
    try {
      logger.info("Running initial daily challenge update after deploy...");
      await updateDailyChallenges();
    } catch (err) {
      logger.error("Initial daily challenge run failed:", err);
    }
  }
})();
