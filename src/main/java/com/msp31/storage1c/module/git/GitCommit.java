package com.msp31.storage1c.module.git;

import lombok.Value;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Calendar;

@Value
public class GitCommit {
    String id;
    String message;
    GitIdentity author;
    Calendar when;

    public static GitCommit fromRevCommit(RevCommit revCommit) {
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(revCommit.getCommitTime() * 1000L);
        return new GitCommit(
                revCommit.getName(),
                revCommit.getFullMessage(),
                GitIdentity.fromPersonIdent(revCommit.getAuthorIdent()),
                calendar
        );
    }
}
