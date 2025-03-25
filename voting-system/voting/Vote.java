package voting;

public class Vote {
    String voteId;
    String voterId;
    String electionId;
    String candidateId;

    public Vote(String voteId, String voterId, String electionId, String candidateId) {
        this.voteId = voteId;
        this.voterId = voterId;
        this.electionId = electionId;
        this.candidateId = candidateId;
    }
}