package voting;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DataStore dataStore = new DataStore();
        ElectionService electionService = new ElectionService(dataStore);
        VoterService voterService = new VoterService(dataStore, electionService);

        Admin admin = new Admin("admin1", "Admin User");
        Candidate candidate1 = new Candidate("cand1", "Candidate A");
        Candidate candidate2 = new Candidate("cand2", "Candidate B");
        List<Candidate> candidates = Arrays.asList(candidate1, candidate2);

        Election election = new Election("election1", "Presidential Election", new Date(), new Date(System.currentTimeMillis() + 86400000), candidates); //ends in 24 hours
        electionService.createElection(admin, election);
        electionService.activateElection(admin, "election1");

        Voter voter1 = new Voter("voter1", "Voter One");
        Voter voter2 = new Voter("voter2", "Voter Two");
        voterService.registerVoter(voter1);
        voterService.registerVoter(voter2);

        voterService.castVote(voter1, new Vote("vote1", "voter1", "election1", "cand1"));
        voterService.castVote(voter2, new Vote("vote2", "voter2", "election1", "cand2"));

        Map<Candidate, Integer> results = electionService.getElectionResults("election1");
        System.out.println("Election Results:");
        results.forEach((candidate, count) -> System.out.println(candidate.name + ": " + count));

        // Test case: Duplicate voting
        voterService.castVote(voter1, new Vote("vote3", "voter1", "election1", "cand2")); // This should be ignored

        results = electionService.getElectionResults("election1");
        System.out.println("\nElection Results (after duplicate vote attempt):");
        results.forEach((candidate, count) -> System.out.println(candidate.name + ": " + count));

        // Test case: Inactive election
        Election inactiveElection = new Election("election2", "Local Election", new Date(), new Date(System.currentTimeMillis() + 86400000), candidates);
        electionService.createElection(admin, inactiveElection);
        voterService.castVote(voter1, new Vote("vote4", "voter1", "election2", "cand1")); //This should be ignored

        System.out.println("\nActive Elections:");
        List<Election> activeElections = voterService.getActiveElections();
        activeElections.forEach(e -> System.out.println(e.title));

        //Test case: election not found
        Map<Candidate, Integer> nonExistantElectionResults = electionService.getElectionResults("election3");
        System.out.println("\nElection Results of NonExistant election:");
        nonExistantElectionResults.forEach((candidate, count) -> System.out.println(candidate.name + ": " + count));

    }
}