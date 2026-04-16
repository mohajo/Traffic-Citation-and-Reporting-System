package tcrs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class for traffic school info extractor
public class TrafficSchoolInfoExtractor {

    public static class SessionInfo {
        private int sessionNumber;
        private LocalDate sessionDate;
        private int sessionDuration;

        public SessionInfo(int sessionNumber, LocalDate sessionDate, int sessionDuration) {
            this.sessionNumber = sessionNumber;
            this.sessionDate = sessionDate;
            this.sessionDuration = sessionDuration;
        }

        public int getSessionNumber() {
            return sessionNumber;
        }

        public LocalDate getSessionDate() {
            return sessionDate;
        }

        public int getSessionDuration() {
            return sessionDuration;
        }
    }

    private List<SessionInfo> sessions;
    private String additionalNotes;

    public TrafficSchoolInfoExtractor(String trafficSchoolString) {
        sessions = new ArrayList<>();

        Pattern sessionPattern = Pattern.compile("Session (\\d+): Date: (\\d{4}-\\d{2}-\\d{2}), Duration: (\\d+) hours");
        Matcher sessionMatcher = sessionPattern.matcher(trafficSchoolString);

        while (sessionMatcher.find()) {
            int sessionNumber = Integer.parseInt(sessionMatcher.group(1));
            LocalDate sessionDate = LocalDate.parse(sessionMatcher.group(2));
            int sessionDuration = Integer.parseInt(sessionMatcher.group(3));

            sessions.add(new SessionInfo(sessionNumber, sessionDate, sessionDuration));
        }

        Pattern notesPattern = Pattern.compile("Additional Notes: (.+)");
        Matcher notesMatcher = notesPattern.matcher(trafficSchoolString);

        if (notesMatcher.find()) {
            additionalNotes = notesMatcher.group(1);
        }
    }

    public List<SessionInfo> getSessions() {
        return sessions;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public int getLength() {
        return sessions.size();
    }
}
