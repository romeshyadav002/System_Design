VotingSystem/
└── voting/
    ├── Voter.java
    ├── Admin.java
    ├── Election.java
    ├── Candidate.java
    ├── Vote.java
    ├── ElectionService.java
    ├── VoterService.java
    ├── DataStore.java
    └── Main.java

classDiagram
    class Voter {
        - voterId : String
        - name : String
        - votedElections : Set<String>
        + Voter(voterId: String, name: String)
        + hasVoted(electionId: String) : boolean
        + addVotedElection(electionId: String) : void
    }
    class Admin {
        - adminId : String
        - name : String
        + Admin(adminId: String, name: String)
    }
    class Election {
        - electionId : String
        - title : String
        - startDate : Date
        - endDate : Date
        - candidates : List<Candidate>
        - isActive : boolean
        + Election(electionId: String, title: String, startDate: Date, endDate: Date, candidates: List<Candidate>)
        + isActive() : boolean
    }
    class Candidate {
        - candidateId : String
        - name : String
        + Candidate(candidateId: String, name: String)
    }
    class Vote {
        - voteId : String
        - voterId : String
        - electionId : String
        - candidateId : String
        + Vote(voteId: String, voterId: String, electionId: String, candidateId: String)
    }
    class ElectionService {
        - dataStore : DataStore
        + ElectionService(dataStore: DataStore)
        + createElection(admin: Admin, election: Election) : void
        + activateElection(admin: Admin, electionId: String) : void
        + deactivateElection(admin: Admin, electionId: String) : void
        + getElectionResults(electionId: String) : Map<Candidate, Integer>
    }
    class VoterService {
        - dataStore : DataStore
        - electionService : ElectionService
        + VoterService(dataStore: DataStore, electionService: ElectionService)
        + registerVoter(voter: Voter) : void
        + castVote(voter: Voter, vote: Vote) : void
        + getActiveElections() : List<Election>
    }
    class DataStore {
        - voters : Map<String, Voter>
        - elections : Map<String, Election>
        - votes : List<Vote>
        + saveVoter(voter: Voter) : void
        + getVoter(voterId: String) : Voter
        + saveElection(election: Election) : void
        + getElection(electionId: String) : Election
        + saveVote(vote: Vote) : void
        + getVotes(electionId: String) : List<Vote>
        + getAllElections() : List<Election>
    }

    ElectionService --> DataStore : uses
    VoterService --> DataStore : uses
    VoterService --> ElectionService : uses
    Election --> Candidate : contains
    Vote --> Voter : associated with
    Vote --> Election : associated with
    Vote --> Candidate : associated with

Let's break down the role of each class in the voting system and how they interact:

1. Voter:

Role: Represents a registered user who can cast votes.
Responsibilities:
Stores the voter's unique ID and name.
Keeps track of which elections the voter has already participated in to prevent duplicate voting.
Interactions:
Used by VoterService to register voters and manage their voting status.
Used by Vote to associate a vote with a specific voter.

2. Admin:

Role: Represents an administrator who manages elections.
Responsibilities:
Stores the administrator's unique ID and name.
Has the authority to create, activate, and deactivate elections.
Interactions:
Used by ElectionService to perform administrative tasks related to elections.

3. Election:

Role: Represents an election with a set of candidates.
Responsibilities:
Stores election details like ID, title, start/end dates, and a list of candidates.
Tracks whether the election is currently active.
Interactions:
Created and managed by ElectionService.
Used by VoterService to display active elections.
Used by Vote to associate a vote with a specific election.
Used by DataStore to save and retrieve election information.

4. Candidate:

Role: Represents a candidate participating in an election.
Responsibilities:
Stores the candidate's unique ID and name.
Interactions:
Associated with an Election.
Used by Vote to indicate which candidate a voter has chosen.
Used by Election service to display election results.

5. Vote:

Role: Represents a single vote cast by a voter.
Responsibilities:
Stores the vote's unique ID, the voter's ID, the election ID, and the candidate's ID.
Interactions:
Created by VoterService when a voter casts a vote.
Used by ElectionService to calculate election results.
Used by DataStore to save and retrieve vote information.

6. ElectionService:

Role: Manages election-related operations.
Responsibilities:
Creates new elections.
Activates and deactivates elections.
Calculates and retrieves election results.
Interactions:
Uses DataStore to persist election data.
Used by VoterService to get active elections.
Used by Main to get the results of an election.

7. VoterService:

Role: Manages voter-related operations.
Responsibilities:
Registers voters.
Allows voters to cast votes.
Retrieves a list of active elections.
Interactions:
Uses DataStore to persist voter data.
Uses ElectionService to access election information.
Uses Main to register voters and cast votes.

8. DataStore:

Role: Handles persistent storage of data.
Responsibilities:
Saves and retrieves Voter, Election, and Vote objects.
Acts as a data access layer, abstracting the underlying storage mechanism (e.g., in-memory, database).
Interactions:
Used by ElectionService and VoterService to interact with the data.
How They Work Together:

Initialization: The Main class creates instances of DataStore, ElectionService, and VoterService.
Election Creation: An Admin uses ElectionService to create and activate an Election.
Voter Registration: Voters are registered using VoterService.
Vote Casting: Voters use VoterService to cast Votes.
Result Retrieval: ElectionService retrieves Votes from DataStore and calculates the results.
Data Persistence: DataStore handles the actual storage of all objects.
This design provides a clear separation of concerns, making the system more maintainable and extensibl