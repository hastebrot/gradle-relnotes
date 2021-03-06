package de.entera.gradle.relnotes.model

import java.time.ZonedDateTime
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includePackage = false, ignoreNulls = true)
class PullRequest {
    String number
    String title
    String baseRef
    ZonedDateTime mergedAt
    Release refRelease
}


