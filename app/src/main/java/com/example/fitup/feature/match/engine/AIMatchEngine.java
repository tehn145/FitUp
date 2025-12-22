package com.example.fitup.feature.match.engine;

import com.example.fitup.feature.match.model.MatchProfile;
import com.example.fitup.feature.match.model.MatchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AIMatchEngine {

    public static List<MatchResult> match(MatchProfile me, List<MatchProfile> candidates) {
        List<MatchResult> results = new ArrayList<>();

        for (MatchProfile other : candidates) {
            int score = 0;
            StringBuilder reason = new StringBuilder();

            if (me.goal != null && me.goal.equals(other.goal)) {
                score += 40;
                reason.append("Same goal. ");
            }

            if (Math.abs(me.level - other.level) <= 1) {
                score += 25;
                reason.append("Similar level. ");
            }

            if (me.availability != null && me.availability.equals(other.availability)) {
                score += 20;
                reason.append("Same schedule. ");
            }

            if (Math.abs(me.age - other.age) <= 5) {
                score += 15;
                reason.append("Close age. ");
            }

            results.add(new MatchResult(other, score, reason.toString().trim()));
        }

        Collections.sort(results, (a, b) -> b.score - a.score);
        return results;
    }
}
