package org.testfx.gradle

import org.junit.Before
import org.junit.Test

import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.parse

class NotesTest {

    List<Release> releases
    List<PullRequest> pulls
    List<Commit> commits
    List<Author> authors

    ReleaseNotes releaseNotes
    ReleaseNotesPrinter printer

    final String printedNotes0 = """
        ## v1.0.2-SNAPSHOT (December 19, 2014)

        5 commits by 2 authors:
        - **Alice Henderson** (@alice) &mdash; 4 commits
        - **Bob Sanders** (@bob) &mdash; 1 commit

        4 merged pull requests:
        - **chore(Quux):** ... (#21) &mdash; 1 commit
        - **fix(Quux):** ... (#22) &mdash; 2 commits
        - **test:** ... (#23) &mdash; 1 commit
        - ... (#24) &mdash; 1 commit
    """.stripIndent().trim() + "\n"

    final String printedNotes1 = """
        ## v1.0.1 (March 23, 2012)

        4 commits by 2 authors:
        - **Carol Sanders** (@carol) &mdash; 2 commits
        - **Ted Henderson** (@ted) &mdash; 2 commits

        2 merged pull requests:
        - **docs(Baz):** ... (#12) &mdash; 2 commits
        - **refactor(Bar):** ... (#11) &mdash; 2 commits
    """.stripIndent().trim() + "\n"

    final String printedNotes2 = """
        ## v1.0.0 (January 1, 2010)

        3 commits by 1 author:
        - **Alice Henderson** (@alice) &mdash; 3 commits

        1 merged pull request:
        - **feat(Foo):** ... (#1) &mdash; 3 commits
    """.stripIndent().trim() + "\n"

    @Before
    void setup() {
        releases = [
            new Release(tagName: "v1.0.0", releasedAt: parse("2010-01-01T00:00Z")),
            new Release(tagName: "v1.0.1", releasedAt: parse("2012-03-23T00:00Z")),
            new Release(tagName: "v1.0.2-SNAPSHOT", releasedAt: now()),
        ]
        pulls = [
            new PullRequest(number: "1", mergedAt: parse("2009-06-10T00:00Z"), title: "feat(Foo): ..."),

            new PullRequest(number: "11", mergedAt: parse("2011-06-10T00:00Z"), title: "refactor(Bar): ..."),
            new PullRequest(number: "12", mergedAt: parse("2011-06-10T00:00Z"), title: "docs(Baz): ..."),

            new PullRequest(number: "21", mergedAt: parse("2013-06-10T00:00Z"), title: "chore(Quux): ..."),
            new PullRequest(number: "22", mergedAt: parse("2013-06-10T00:00Z"), title: "fix(Quux): ..."),
            new PullRequest(number: "23", mergedAt: parse("2013-06-10T00:00Z"), title: "test: ..."),
            new PullRequest(number: "24", mergedAt: parse("2013-06-10T00:00Z"), title: "..."),
        ]
        commits = [
            new Commit(pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(pullNumber: "1", authorLogin: "alice", authorName: "Alice Henderson"),

            new Commit(pullNumber: "11", authorLogin: "ted", authorName: "Ted Henderson"),
            new Commit(pullNumber: "11", authorLogin: "ted", authorName: "Ted Henderson"),
            new Commit(pullNumber: "12", authorLogin: "carol", authorName: "Carol Sanders"),
            new Commit(pullNumber: "12", authorLogin: "carol", authorName: "Carol Sanders"),

            new Commit(pullNumber: "21", authorLogin: "bob", authorName: "Bob Sanders"),
            new Commit(pullNumber: "22", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(pullNumber: "22", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(pullNumber: "23", authorLogin: "alice", authorName: "Alice Henderson"),
            new Commit(pullNumber: "24", authorLogin: "alice", authorName: "Alice Henderson"),
        ]
        authors = [
            new Author(login: "alice", name: "Alice Henderson"),
            new Author(login: "bob", name: "Bob Sanders"),
            new Author(login: "carol", name: "Carol Sanders"),
            new Author(login: "ted", name: "Ted Henderson"),
        ]

        releaseNotes = new ReleaseNotes()
        releaseNotes.releases = releases
        releaseNotes.pulls = pulls
        releaseNotes.commits = commits
        releaseNotes.authors = authors
        printer = new ReleaseNotesPrinter()
        printer.releaseNotes = releaseNotes
    }

    @Test
    void "assignReleasesToPullRequests()"() {
        // when:
        releaseNotes.assignReleasesToPullRequests()

        // then:
        assert pulls.find { it.number == "1" }.refRelease.tagName == "v1.0.0"
        assert pulls.find { it.number == "11" }.refRelease.tagName == "v1.0.1"
        assert pulls.find { it.number == "12" }.refRelease.tagName == "v1.0.1"
        assert pulls.find { it.number == "21" }.refRelease.tagName == "v1.0.2-SNAPSHOT"
        assert pulls.find { it.number == "22" }.refRelease.tagName == "v1.0.2-SNAPSHOT"
    }

    @Test
    void "assignPullRequestsToCommits()"() {
        // when:
        releaseNotes.assignPullRequestsToCommits()

        // then:
        assert commits[0].refPullRequest == pulls[0]
        assert commits[1].refPullRequest == pulls[0]
        assert commits[2].refPullRequest == pulls[0]
        assert commits[3].refPullRequest == pulls[1]
        assert commits[4].refPullRequest == pulls[1]
        assert commits[5].refPullRequest == pulls[2]
        assert commits[5].refPullRequest == pulls[2]
    }

    @Test
    void "assignCommitsToAuthors()"() {
        // when:
        releaseNotes.assignCommitsToAuthors()

        // then:
        assert commits[0].refAuthor == authors[0]
        assert commits[1].refAuthor == authors[0]
        assert commits[2].refAuthor == authors[0]
        assert commits[3].refAuthor == authors[3]
        assert commits[4].refAuthor == authors[3]
        assert commits[5].refAuthor == authors[2]
        assert commits[6].refAuthor == authors[2]
    }

    @Test
    void "generateReleaseNotes()"() {
        // given:
        releaseNotes.assignReleasesToPullRequests()
        releaseNotes.assignPullRequestsToCommits()
        releaseNotes.assignCommitsToAuthors()

        // expect:
        releaseNotes.releases.reverse().each { rel ->
            println printer.generateReleaseNotes(rel)
        }
    }

}
