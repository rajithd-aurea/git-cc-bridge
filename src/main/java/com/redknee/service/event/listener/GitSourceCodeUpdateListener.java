package com.redknee.service.event.listener;

import com.redknee.config.ApplicationProperty;
import com.redknee.config.ApplicationProperty.GitServer;
import com.redknee.service.event.SourceCodeEvent;
import com.redknee.service.exception.ApiException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GitSourceCodeUpdateListener {

    private final ApplicationProperty applicationProperty;

    GitSourceCodeUpdateListener(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    @EventListener
    public void handle(SourceCodeEvent sourceCode) {
        String dirPath = buildDirPath(sourceCode);
        GitServer gitServer = applicationProperty.getGitServer();
        CredentialsProvider credentials = gitServer.getUsername() != null && gitServer.getPassword() != null
                ? new UsernamePasswordCredentialsProvider(gitServer.getUsername(), gitServer.getPassword()) : null;
        try {
            log.info("Checking git directory exists for repo name {}", sourceCode.getRepoName());
            if (isDirectoryExists(dirPath)) {
                log.info("Directory exists for repo {} and start pulling from git", sourceCode.getRepoName());
                Git git = Git.open(new File(dirPath));
                git.reset().setMode(ResetType.HARD).call();
                PullCommand pullCommand = git.pull();
                if (credentials != null) {
                    pullCommand.setCredentialsProvider(credentials);
                }
                pullCommand.setRemoteBranchName(sourceCode.getBranchName());
                PullResult pullResult = pullCommand.call();
                Collection<TrackingRefUpdate> updates = pullResult.getFetchResult().getTrackingRefUpdates();
                log.info("Updates found {}", updates.size());
                git.close();
            } else {
                log.info("Directory not exists. Start cloning the repo {}", sourceCode.getRepoName());
                CloneCommand cloneCommand = Git.cloneRepository()
                        .setURI(sourceCode.getUrl()).setDirectory(new File(dirPath))
                        .setBranch(sourceCode.getBranchName());
                if (credentials != null) {
                    cloneCommand.setCredentialsProvider(credentials);
                }
                Git git = cloneCommand.call();
                git.close();
            }
        } catch (GitAPIException | IOException e) {
            throw new ApiException("[Git sync failed]: Error occur while getting update from git", e);
        }
    }

    private boolean isDirectoryExists(String dirName) {
        File src = new File(dirName);
        if (!src.isDirectory()) {
            return false;
        }
        String[] list = src.list();
        return list != null && list.length != 0;
    }

    private String buildDirPath(SourceCodeEvent sourceCode) {
        String workspace = applicationProperty.getGitServer().getWorkspace();
        return workspace + sourceCode.getRepoName() + "-" + sourceCode.getId();
    }

}
