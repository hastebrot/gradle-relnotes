package com.github.entera.gradle.relnotes.task

import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class ReleaseNotesTask extends ConventionTask {

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    @TaskAction
    void generate() {
        println "generate()"
    }

}
