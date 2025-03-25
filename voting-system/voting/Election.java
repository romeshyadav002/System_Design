package voting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Election {
    String electionId;
    String title;
    Date startDate;
    Date endDate;
    List<Candidate> candidates = new ArrayList<>();
    boolean isActive;

    public Election(String electionId, String title, Date startDate, Date endDate, List<Candidate> candidates) {
        this.electionId = electionId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.candidates = candidates;
    }

    public boolean isActive() {
        return isActive;
    }
}